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
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Created by markus on 30/09/2016.
 */
@Component
@EnableScheduling
@Scope(value = SCOPE_SINGLETON)
public class TopicListInfo {

    @Autowired
    private Consumer<String, String> consumer;

    private List<String> topcis;

    public TopicListInfo() {
        topcis = Lists.newArrayList();
    }

    @Scheduled(fixedDelay = 2000)
    void updateTopicList() {
        Set<String> topics = consumer.listTopics().keySet();
        if (!topics.isEmpty()) {
            topcis.clear();
            topcis.addAll(topics);
        }
    }

    List<String> getTopcis() {
        return topcis;
    }
}
