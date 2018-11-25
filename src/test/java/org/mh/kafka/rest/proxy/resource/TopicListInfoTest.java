/*
 *  Copyright 2016, 2018 Markus Helbig
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
import org.apache.kafka.common.PartitionInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TopicListInfoTest {

    @MockBean
    private Consumer<String, String> consumer;

    @SpyBean
    private TopicListInfo topicListInfo;

    @Before
    public void setUp() {
        reset(consumer, topicListInfo);
    }

    @Test
    public void testListTopics() throws Exception {
        when(consumer.listTopics()).thenAnswer((Answer<Map<String, List<PartitionInfo>>>) invocation -> Map.of("test", List.of()));
        topicListInfo.updateTopicList();
        assertThat(topicListInfo.getTopcis(), containsInAnyOrder("test"));
    }
}