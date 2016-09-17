/*
 *  Copyright 2016 Markus Helbig
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.mh.kafka.rest.proxy.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mh.kafka.rest.proxy.consumer.KafkaProxyConsumer;
import org.mh.kafka.rest.proxy.producer.KafkaProxyProducer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by markus on 27/08/16.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class TopicResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private KafkaProxyConsumer kafkaProxyConsumer;
    @MockBean
    private KafkaProxyProducer kafkaProxyProducer;

    @Test
    public void testPostEmptyBody() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
        verify(kafkaProxyProducer, times(0)).send(any(), any());
    }

    @Test
    public void testPostNoMediaType() throws Exception {
        mvc.perform(post("/topics"))
                .andExpect(status().isNotFound());
        verify(kafkaProxyProducer, times(0)).send(any(), any());
    }

    @Test
    public void testPostEmptyTopic() throws Exception {
        mvc.perform(post("/topics").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
        verify(kafkaProxyProducer, times(0)).send(any(), any());
    }

    @Test
    public void testPostEmptyJson() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8).content("{}"))
                .andExpect(status().isBadRequest());
        verify(kafkaProxyProducer, times(0)).send(any(), any());
    }

    @Test
    public void testPostEmptyJsonNoMediaTypeSet() throws Exception {
        mvc.perform(post("/topics/test").content("{}"))
                .andExpect(status().isUnsupportedMediaType());
        verify(kafkaProxyProducer, times(0)).send(any(), any());
    }

    @Test
    public void testPost() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy first proof of concept is out\"}"))
                .andExpect(status().isCreated());
        verify(kafkaProxyProducer, times(1)).send(eq("test"), eq("{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy first proof of concept is out\"}"), any());
    }

    @Test
    public void testGetTopic() throws Exception {
        when(kafkaProxyConsumer.poll("test")).thenAnswer(new Answer<ConsumerRecords<String, String>>() {
            @Override
            public ConsumerRecords<String, String> answer(InvocationOnMock invocation) throws Throwable {
                HashMap<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitionListHashMap = Maps.newHashMap();
                return new ConsumerRecords<>(topicPartitionListHashMap);
            }
        });
        mvc.perform(get("/topics/test"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void testGetTopicNotFound() throws Exception {
        mvc.perform(get("/topics"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTopicsInfoEmpty() throws Exception {
        mvc.perform(get("/topics/info"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testGetTopicsInfo() throws Exception {
        when(kafkaProxyConsumer.getTopics()).thenReturn(Sets.newHashSet("test"));
        mvc.perform(get("/topics/info"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"test\"]"));
    }

    @Test
    public void testGetTopicInfo() throws Exception {
        when(kafkaProxyConsumer.getTopicInfo("test")).thenAnswer(new Answer<List<PartitionInfo>>() {
            @Override
            public List<PartitionInfo> answer(InvocationOnMock invocation) throws Throwable {
                return Lists.newArrayList(new PartitionInfo("test", 1, new Node(1, "localhost", 1), new Node[]{}, new Node[]{}));
            }
        });
        mvc.perform(get("/topics/test/info"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));
    }


    @Test
    public void testGetTopicFullInfo() throws Exception {
        when(kafkaProxyConsumer.getTopicInfo("test")).thenAnswer(new Answer<List<PartitionInfo>>() {
            @Override
            public List<PartitionInfo> answer(InvocationOnMock invocation) throws Throwable {
                return Lists.newArrayList(new PartitionInfo("test", 1, new Node(1, "localhost", 1), new Node[]{new Node(1, "localhost", 1)}, new Node[]{new Node(1, "localhost", 1)}));
            }
        });
        mvc.perform(get("/topics/test/info"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));
    }

}