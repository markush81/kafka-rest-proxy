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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/topics", method = RequestMethod.POST)
class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping(path = "/{topic}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity postMessage(@PathVariable(value = "topic") String topic, @RequestParam(value = "key", required = false) String key, @RequestBody String value) throws InterruptedException, ExecutionException, TimeoutException {
        if (value == null) {
            return ResponseEntity.badRequest().body("No payload specified.");
        }
        LOGGER.debug("{}: {} - {}", topic, key, value);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable exception) {
                LOGGER.error("{}", exception.getMessage(), exception);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.debug("RecordMetadata: {}", result.getRecordMetadata());
            }
        });
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
