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

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.concurrent.ListenableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@RunWith(SpringRunner.class)
public class TopicResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TopicListInfo topicListInfo;

    @MockBean
    private Consumer<String, String> consumer;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Before
    public void setUp() {
        reset(kafkaTemplate, consumer, topicListInfo);
        //noinspection unchecked
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock(ListenableFuture.class));
    }

    @Test
    public void testPostEmptyBody() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
        verify(kafkaTemplate, times(0)).send(any(), eq(null), any());
    }

    @Test
    public void testPostNoMediaType() throws Exception {
        mvc.perform(post("/topics"))
                .andExpect(status().isNotFound());
        verify(kafkaTemplate, times(0)).send(any(), eq(null), any());
    }

    @Test
    public void testPostEmptyTopic() throws Exception {
        mvc.perform(post("/topics").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
        verify(kafkaTemplate, times(0)).send(any(), eq(null), any());
    }

    @Test
    public void testPostEmptyJson() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8).content("{}"))
                .andExpect(status().isCreated());
        verify(kafkaTemplate, times(1)).send(any(), eq(null), any());
    }

    @Test
    public void testPostEmptyJsonNoMediaTypeSet() throws Exception {
        mvc.perform(post("/topics/test").content("{}"))
                .andExpect(status().isUnsupportedMediaType());
        verify(kafkaTemplate, times(0)).send(any(), eq(null), any());
    }

    @Test
    public void testPost() throws Exception {
        mvc.perform(post("/topics/test").contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"message\": \"kafka-rest-proxy is out\"}"))
                .andExpect(status().isCreated());
        verify(kafkaTemplate, times(1)).send(eq("test"), eq(null), eq("{\"message\": \"kafka-rest-proxy is out\"}"));
    }

    @Test
    public void testPostWithKey() throws Exception {
        mvc.perform(post("/topics/test")
                .param("key", "key")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"message\": \"kafka-rest-proxy is out\"}"))
                .andExpect(status().isCreated());
        verify(kafkaTemplate, times(1)).send(eq("test"), eq("key"), eq("{\"message\": \"kafka-rest-proxy is out\"}"));
    }

    @Test
    public void testGetTopicNotFound() throws Exception {
        mvc.perform(get("/topics"))
                .andExpect(status().isNotFound());
    }
}