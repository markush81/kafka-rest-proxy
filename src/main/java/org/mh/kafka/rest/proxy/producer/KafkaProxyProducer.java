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

package org.mh.kafka.rest.proxy.producer;

import org.apache.kafka.clients.producer.*;
import org.mh.kafka.rest.proxy.config.KafkaRestProxyConfiguration;

import java.util.concurrent.Future;

/**
 * Created by markus on 27/08/16.
 */
public class KafkaProxyProducer {

    private Producer<String, String> producer;

    public KafkaProxyProducer(KafkaRestProxyConfiguration configuration) {
        producer = new KafkaProducer<>(configuration.getKafka().get("producer"));
    }

    public Future<RecordMetadata> send(String topic, String value) {
        return producer.send(new ProducerRecord<>(topic, value));
    }

    public Future<RecordMetadata> send(String topic, String value, Callback callback) {
        return producer.send(new ProducerRecord<>(topic, value), callback);
    }

    @Override
    protected void finalize() throws Throwable {
        producer.close();
        super.finalize();
    }
}
