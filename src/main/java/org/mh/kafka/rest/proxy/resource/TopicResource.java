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
import io.dropwizard.jersey.errors.ErrorMessage;
import org.mh.kafka.rest.proxy.consumer.KafkaProxyConsumer;
import org.mh.kafka.rest.proxy.producer.KafkaProxyProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by markus on 27/08/16.
 */
@Path("/topics")
@Produces(MediaType.APPLICATION_JSON)
public class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class);

    private KafkaProxyProducer kafkaProxyProducer;
    private KafkaProxyConsumer kafkaProxyConsumer;

    public TopicResource(KafkaProxyProducer kafkaProxyProducer, KafkaProxyConsumer kafkaProxyConsumer) {
        this.kafkaProxyProducer = kafkaProxyProducer;
        this.kafkaProxyConsumer = kafkaProxyConsumer;
    }

    @POST
    @Path(value = "/{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postMessage(@PathParam(value = "topic") String topic, String value) throws InterruptedException, ExecutionException, TimeoutException {
        int badRequestStatusCode = Response.Status.BAD_REQUEST.getStatusCode();
        if (Strings.isNullOrEmpty(topic)) {
            return Response.status(badRequestStatusCode).entity(new ErrorMessage(badRequestStatusCode, "Pls. specify a topic.")).build();
        }
        if (Strings.isNullOrEmpty(value) || "{}".equals(value)) {
            return Response.status(badRequestStatusCode).entity(new ErrorMessage(badRequestStatusCode, "No payload specified.")).build();
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
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path(value = "/{topic}")
    public Response getMessages(@PathParam(value = "topic") String topic) throws InterruptedException, ExecutionException, TimeoutException {
        int badRequestStatusCode = Response.Status.BAD_REQUEST.getStatusCode();
        if (Strings.isNullOrEmpty(topic)) {
            return Response.status(badRequestStatusCode).entity(new ErrorMessage(badRequestStatusCode, "Pls. specify a topic.")).build();
        }
        return Response.status(Response.Status.OK).entity(kafkaProxyConsumer.poll(topic)).build();
    }

    @GET
    public Response getTopics() {
        return Response.ok().entity(Lists.newArrayList(kafkaProxyConsumer.getTopics())).build();
    }
}
