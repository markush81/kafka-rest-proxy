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
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * Created by markus on 17/09/16.
 */
public class KafkaConfigurationTest {

    private KafkaConfiguration sut;

    @Before
    public void setUp() {
        sut = new KafkaConfiguration();
    }

    @Test
    public void testFlatProducerProperties() {
        HashMap<String, Object> producer = Maps.newHashMap();
        producer.put("property1", "value1");
        HashMap<String, Object> subproperties = Maps.newHashMap();
        subproperties.put("subproperty1", "subvalue2");
        producer.put("property2", subproperties);
        sut.setProducer(producer);

        Map<String, Object> flatConsumerProperties = sut.getProducerProperties();
        assertThat(flatConsumerProperties, hasEntry("property1", "value1"));
        assertThat(flatConsumerProperties, hasEntry("property2.subproperty1", "subvalue2"));
    }

}