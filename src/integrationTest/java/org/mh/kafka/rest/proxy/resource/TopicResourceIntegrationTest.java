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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mh.kafka.rest.proxy.config.KafkaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasKey;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicResourceIntegrationTest {

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, "test");

    private static final String MESSAGE = String.format("{\"message\": \"kafka-rest-proxy is out (%s).\"}", System.currentTimeMillis());

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Autowired
    private TestRestTemplate client;

    private BlockingQueue<ConsumerRecord<String, String>> records;
    private KafkaMessageListenerContainer<String, String> container;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        container = new KafkaMessageListenerContainer<>(cf, new ContainerProperties("test"));
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) record -> {
            records.add(record);
        });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void testPostJson() throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<Void> response = client.postForEntity("/topics/test", new HttpEntity<>(MESSAGE, headers), Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        ConsumerRecord<String, String> result = records.poll(10, TimeUnit.SECONDS);
        assertThat(result, hasValue(MESSAGE));
    }

    @Test
    public void testPostJsonWithKey() throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<Void> response = client.postForEntity("/topics/test?key={key}", new HttpEntity<>(MESSAGE, headers), Void.class, "theKey");
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        ConsumerRecord<String, String> result = records.poll(10, TimeUnit.SECONDS);
        assertThat(result, hasValue(MESSAGE));
        assertThat(result, hasKey("theKey"));
    }

    @TestConfiguration
    public static class TestKafkaConfiguration {

        @Autowired
        private KafkaConfiguration kafkaConfiguration;

        @Bean
        public Map<String, Object> producerConfigs() {
            Map<String, Object> producerConfigs = kafkaConfiguration.getProducerProperties();
            producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestUtils.producerProps(embeddedKafka).get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
            return producerConfigs;
        }
    }
}
