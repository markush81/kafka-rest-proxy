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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.mh.kafka.rest.proxy.consumer.KafkaProxyConsumer;
import org.mh.kafka.rest.proxy.producer.KafkaProxyProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by markus on 27/08/16.
 */
@RestController("/topics")
@RequestMapping(path = "/service")
public class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class);

    private KafkaProxyProducer kafkaProxyProducer;
    private KafkaProxyConsumer kafkaProxyConsumer;

    public TopicResource(KafkaProxyProducer kafkaProxyProducer, KafkaProxyConsumer kafkaProxyConsumer) {
        this.kafkaProxyProducer = kafkaProxyProducer;
        this.kafkaProxyConsumer = kafkaProxyConsumer;
    }

    @PostMapping(path = "/{topic}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity postMessage(@RequestParam(value = "topic") String topic, String value) throws InterruptedException, ExecutionException, TimeoutException {
        if (Strings.isNullOrEmpty(topic)) {
            return ResponseEntity.badRequest().body("Pls. specify a topic.");
        }
        if (Strings.isNullOrEmpty(value) || "{}".equals(value)) {
            return ResponseEntity.badRequest().body("No payload specified.");
        }
        LOGGER.debug("{}: {}", topic, value);
        kafkaProxyProducer.send(topic, value, (metadata, exception) -> {
            if (metadata != null) {
                LOGGER.debug("RecordMetadata: {}", metadata);
            }
            if (exception != null) {
                LOGGER.error("{}", exception.getMessage(), exception);
            }
        });
        return ResponseEntity.created(null).build();
    }

    @GetMapping(path = "/{topic}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getMessages(@RequestParam(value = "topic") String topic) throws InterruptedException, ExecutionException, TimeoutException {
        if (Strings.isNullOrEmpty(topic)) {
            return ResponseEntity.badRequest().body("Pls. specify a topic.");
        }
        return ResponseEntity.ok(kafkaProxyConsumer.poll(topic));
    }

    @GetMapping
    public ResponseEntity getTopics() {
        return ResponseEntity.ok(Lists.newArrayList(kafkaProxyConsumer.getTopics()));
    }
}
