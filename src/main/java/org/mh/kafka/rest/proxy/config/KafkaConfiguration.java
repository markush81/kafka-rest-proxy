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

package org.mh.kafka.rest.proxy.config;

import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfiguration {

    private Map<String, Object> producer;
    private Map<String, Object> consumer;
    private List<String> brokers;

    @SuppressWarnings("unused")
    public Map<String, Object> getProducer() {
        return producer;
    }

    @SuppressWarnings("WeakerAccess")
    public void setProducer(Map<String, Object> producer) {
        this.producer = producer;
    }

    public Map<String, Object> getProducerProperties() {
        Map<String, Object> producerProperties = flatProperties(producer, Maps.newHashMap(), null);
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        return producerProperties;
    }

    @SuppressWarnings("unused")
    public Map<String, Object> getConsumer() {
        return consumer;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setConsumer(Map<String, Object> consumer) {
        this.consumer = consumer;
    }

    public List<String> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<String> brokers) {
        this.brokers = brokers;
    }

    public Map<String, Object> getConsumerProperties() {
        Map<String, Object> consumerProperties = flatProperties(consumer, Maps.newHashMap(), null);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "healthcheck");
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return consumerProperties;
    }

    private Map<String, Object> flatProperties(Map<String, Object> input, Map<String, Object> result, String current) {
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() instanceof Map) {
                if (current == null) {
                    //noinspection unchecked
                    flatProperties((Map<String, Object>) entry.getValue(), result, entry.getKey());
                } else {
                    //noinspection unchecked
                    flatProperties((Map<String, Object>) entry.getValue(), result, current + "." + entry.getKey());
                }
            } else if (current == null) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                result.put(current + "." + entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
