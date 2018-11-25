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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by markus on 03/11/2016.
 */
@DirtiesContext
@ContextConfiguration
public class AbstractKafkaIntegrationTest {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, "test");

    protected BlockingQueue<ConsumerRecord<String, String>> records;
    private KafkaMessageListenerContainer<String, String> container;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        container = new KafkaMessageListenerContainer<>(cf, new ContainerProperties("test"));
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) record -> {
            System.out.println(record.value());
            records.add(record);
        });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        records.clear();
        container.stop();
    }


    @TestConfiguration
    public static class TestKafkaConfiguration {

        @Autowired
        private KafkaProperties kafkaProperties;

        @Bean
        public Map<String, Object> producerConfigs() {
            Map<String, Object> producerConfigs = kafkaProperties.buildProducerProperties();
            producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka()).get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
            return producerConfigs;
        }

        @Bean
        public Map<String, Object> consumerConfigs() {
            Map<String, Object> consumerConfigs = kafkaProperties.buildConsumerProperties();
            consumerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka()).get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            return consumerConfigs;
        }
    }
}
