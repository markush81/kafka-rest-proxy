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

import com.google.common.collect.Sets;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mh.kafka.rest.proxy.consumer.KafkaProxyConsumer;
import org.mh.kafka.rest.proxy.producer.KafkaProxyProducer;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

/**
 * Created by markus on 27/08/16.
 */
public class TopicResourceTest {

    private static final KafkaProxyProducer KAFKA_PROXY_PRODUCER = mock(KafkaProxyProducer.class);
    private static final KafkaProxyConsumer KAFKA_PROXY_CONSUMER = mock(KafkaProxyConsumer.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new TopicResource(KAFKA_PROXY_PRODUCER, KAFKA_PROXY_CONSUMER)).build();

    @Before
    public void setUp() {
        reset(KAFKA_PROXY_PRODUCER, KAFKA_PROXY_CONSUMER);
        //noinspection unchecked
        when(KAFKA_PROXY_PRODUCER.send(any(), any())).thenReturn(mock(Future.class));
    }

    @Test
    public void testPostEmptyBody() {
        assertThat(resources.client()
                .target("/topics/test")
                .request()
                .post(null)
                .getStatus(), equalTo(400));
        verify(KAFKA_PROXY_PRODUCER, times(0)).send(any(), any());
    }

    @Test
    public void testPostEmptyTopic() {
        assertThat(resources.client()
                .target("/topics")
                .request()
                .post(null)
                .getStatus(), equalTo(405));
        verify(KAFKA_PROXY_PRODUCER, times(0)).send(any(), any());
    }

    @Test
    public void testPostEmptyJson() {
        assertThat(resources.client()
                .target("/topics/test")
                .request()
                .post(Entity.entity("{}", MediaType.APPLICATION_JSON_TYPE))
                .getStatus(), equalTo(400));
        verify(KAFKA_PROXY_PRODUCER, times(0)).send(any(), any());
    }

    @Test
    public void testPost() {
        assertThat(resources.client()
                .target("/topics/test")
                .request()
                .post(Entity.json("{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy first proof of concept is out\"}"))
                .getStatus(), equalTo(201));
        verify(KAFKA_PROXY_PRODUCER, times(1)).send(eq("test"), eq("{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy first proof of concept is out\"}"), any());
    }

    @Test
    public void testGetTopicsEmpty() {
        assertThat(resources.client()
                .target("/topics")
                .request()
                .get()
                .getStatus(), equalTo(200));
    }

    @Test
    public void testGetTopics() {
        when(KAFKA_PROXY_CONSUMER.getTopics()).thenReturn(Sets.newHashSet("test"));
        assertThat(resources.client()
                .target("/topics")
                .request()
                .get()
                .readEntity(String.class), equalTo("[\"test\"]"));
    }

}