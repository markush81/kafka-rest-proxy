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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.*;
import org.mh.kafka.rest.proxy.KafkaRestProxy;
import org.mh.kafka.rest.proxy.config.KafkaRestProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by markus on 27/08/16.
 */
public class TopicResourceIntegrationTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(TopicResourceIntegrationTest.class);

    @ClassRule
    public static final DropwizardAppRule<KafkaRestProxyConfiguration> RULE = new DropwizardAppRule<>(KafkaRestProxy.class, ResourceHelpers.resourceFilePath("kafka-rest-proxy-test.yml"));

    private static Client client;
    private static String baseTarget;

    private KafkaConsumer<String, String> consumer;

    @BeforeClass
    public static void setUp() throws Exception {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");
        baseTarget = String.format("http://localhost:%d", RULE.getLocalPort());
    }

    @Before
    public void setUpTest() {
        Map<String, Object> testConsumer = Maps.newHashMap();
        testConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        testConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        testConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        testConsumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(testConsumer);
    }

    @After
    public void tearDownTest() {
        consumer.commitSync();
        consumer.close();
    }


    @Test
    public void testHealth() {
        Response response = client
                .target(String.format("http://localhost:%d/healthcheck", RULE.getAdminPort()))
                .request()
                .get();
        assertThat(response.getStatus(), equalTo(200));
    }

    @Test
    public void testPostJson() throws InterruptedException {
        String message = String.format("{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy first proof of concept is out (%s).\"}", System.currentTimeMillis());
        Response response = client
                .target(baseTarget + "/topics/test")
                .request()
                .post(Entity.json(message));
        assertThat(response.getStatus(), equalTo(201));

//        consumer.subscribe(Lists.newArrayList("test"));
//        ConsumerRecords<String, String> consumerRecords = consumer.poll(2000);
//        assertThat(consumerRecords.count(), equalTo(1));
//        consumerRecords.forEach(record -> assertThat(record.value(), equalTo(message)));
    }
}
