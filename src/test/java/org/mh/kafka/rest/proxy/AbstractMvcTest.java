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

package org.mh.kafka.rest.proxy;

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Before;
import org.mh.kafka.rest.proxy.resource.TopicListInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.concurrent.ListenableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by markus on 03/11/2016.
 */
public class AbstractMvcTest {

    @Autowired
    protected MockMvc mvc;

    @MockBean
    protected KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private Consumer<String, String> consumer;
    @MockBean
    protected TopicListInfo topicListInfo;

    @Before
    public void setUp() {
        reset(kafkaTemplate, consumer, topicListInfo);
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock(ListenableFuture.class));
    }
}
