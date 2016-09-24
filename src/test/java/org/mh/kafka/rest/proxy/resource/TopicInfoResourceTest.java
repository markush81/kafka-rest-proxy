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
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class TopicInfoResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private Consumer<String, String> consumer;

    @Before
    public void setUp() {
        reset(kafkaTemplate, consumer);
        //noinspection unchecked
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock(ListenableFuture.class));
    }

    @Test
    public void testListTopics() throws Exception {
        when(consumer.listTopics()).thenAnswer(new Answer<Map<String, List<PartitionInfo>>>() {
            @Override
            public Map<String, List<PartitionInfo>> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, List<PartitionInfo>> topics = Maps.newHashMap();
                topics.put("test", Lists.newArrayList());
                return topics;
            }
        });
        mvc.perform(get("/topicslist"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"test\"]"));
    }

    @Test
    public void testGetTopicInfo() throws Exception {
        when(kafkaTemplate.partitionsFor("test")).thenAnswer(new Answer<List<PartitionInfo>>() {
            @Override
            public List<PartitionInfo> answer(InvocationOnMock invocation) throws Throwable {
                return Lists.newArrayList(new PartitionInfo("test", 1, new Node(1, "localhost", 1), new Node[]{}, new Node[]{}));
            }
        });
        mvc.perform(get("/topicsinfo/test"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"{topic: test, partition: 1, leader: {id: 1 }}\"]"));
    }

    @Test
    public void testMetrics() throws Exception {
        when(kafkaTemplate.metrics()).thenAnswer(new Answer<Map<MetricName, ? extends Metric>>() {
            @Override
            public Map<MetricName, ? extends Metric> answer(InvocationOnMock invocation) throws Throwable {
                Map<MetricName, Metric> metrics = Maps.newHashMap();
                MetricName metricName = new MetricName("name", "group", "description", Maps.newHashMap());
                metrics.put(metricName, new Metric() {
                    @Override
                    public MetricName metricName() {
                        return metricName;
                    }

                    @Override
                    public double value() {
                        return 0;
                    }
                });
                return metrics;
            }
        });
        mvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"MetricName [name=name, group=group, description=description, tags={}]\":{}}"));
    }
}