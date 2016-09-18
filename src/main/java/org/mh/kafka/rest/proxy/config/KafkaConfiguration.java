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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by markus on 27/08/16.
 */
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfiguration {

    private Map<String, Object> producer;

    @SuppressWarnings("unused")
    public Map<String, Object> getProducer() {
        return producer;
    }

    @SuppressWarnings("WeakerAccess")
    public void setProducer(Map<String, Object> producer) {
        this.producer = producer;
    }

    public Map<String, Object> getProducerProperties() {
        return flatProperties(producer, Maps.newHashMap(), null);
    }

    private Map<String, Object> flatProperties(Map<String, Object> input, Map<String, Object> result, String current) {
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() instanceof Map) {
                //noinspection unchecked
                flatProperties((Map<String, Object>) entry.getValue(), result, entry.getKey());
            } else if (current == null) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                result.put(current + "." + entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
