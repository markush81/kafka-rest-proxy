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

package org.mh.kafka.rest.proxy;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.mh.kafka.rest.proxy.config.KafkaRestProxyConfiguration;
import org.mh.kafka.rest.proxy.consumer.KafkaProxyConsumer;
import org.mh.kafka.rest.proxy.health.KafkaRestProxyHealthCheck;
import org.mh.kafka.rest.proxy.producer.KafkaProxyProducer;
import org.mh.kafka.rest.proxy.resource.TopicResource;

/**
 * Created by markus on 26/08/16.
 */
public class KafkaRestProxy extends Application<KafkaRestProxyConfiguration> {

    public static void main(String[] args) throws Exception {
        new KafkaRestProxy().run(args);
    }

    @Override
    public String getName() {
        return "kafka-rest-proxy";
    }

    @Override
    public void run(KafkaRestProxyConfiguration configuration, Environment environment) throws Exception {
        environment.healthChecks().register("application", new KafkaRestProxyHealthCheck());

        environment.jersey().register(new TopicResource(new KafkaProxyProducer(configuration), new KafkaProxyConsumer(configuration)));
    }
}
