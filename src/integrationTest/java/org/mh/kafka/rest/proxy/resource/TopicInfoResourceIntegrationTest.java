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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mh.kafka.rest.proxy.AbstractKafkaIntegrationTest;
import org.mh.kafka.rest.proxy.config.KafkaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpMethod.GET;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicInfoResourceIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private TestRestTemplate client;

    @Test
    public void testListTopics() throws InterruptedException {
        ResponseEntity<List<String>> topicBody = client.exchange("/topicslist", GET, null, new ParameterizedTypeReference<List<String>>() {
        });
        List<String> topicList = topicBody.getBody();
        assertThat(topicList, hasSize(greaterThanOrEqualTo(1)));
        assertThat(topicList, hasItem("test"));
    }

    @Test
    public void testTopicInfo() throws InterruptedException {
        ResponseEntity<List<?>> metricsBody = client.exchange("/topicsinfo/test", GET, null, new ParameterizedTypeReference<List<?>>() {
        });
        assertThat(metricsBody.getBody(), hasSize(2));
    }

    @Test
    public void testMetrics() throws InterruptedException {
        ResponseEntity<Map<String, ?>> metricsBody = client.exchange("/metrics", GET, null, new ParameterizedTypeReference<Map<String, ?>>() {
        });
        assertThat(metricsBody.getBody().size(), greaterThan(1));
    }


}
