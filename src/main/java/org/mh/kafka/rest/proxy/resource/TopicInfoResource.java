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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/", method = RequestMethod.GET)
class TopicInfoResource {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TopicListInfo topicListInfo;

    @GetMapping(path = {"/topicsinfo/{topic}"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getInfo(@PathVariable(value = "topic") String topic) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        List<PartitionInfo> body = kafkaTemplate.partitionsFor(topic);
        List<String> formatted = body.stream().map(p -> String.format("{topic: %s, partition: %s, leader: {id: %s }}", p.topic(), p.partition(), p.leader().id())).collect(Collectors.toList());
        return ResponseEntity.ok(formatted);
    }

    @GetMapping(path = {"/topicslist"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getList() throws InterruptedException, ExecutionException, TimeoutException {
        return ResponseEntity.ok(topicListInfo.getTopcis());
    }

    @GetMapping(path = {"/metrics"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<MetricName, ? extends Metric>> getMetrics() throws InterruptedException, ExecutionException, TimeoutException {
        return ResponseEntity.ok(kafkaTemplate.metrics());
    }
}
