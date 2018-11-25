/*
 *  Copyright 2016, 2018 Markus Helbig
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

package org.mh.kafka.rest.proxy.resource

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TopicInfoResourceSimulation extends Simulation {

  val feeder = Iterator.continually(Map("message" -> s"""kafka-rest-proxy ${System.currentTimeMillis()}."""))

  val httpConf = http
    .baseURL("http://localhost:8080")
//    .contentTypeHeader("application/json;charset=UTF-8")

  val scn = scenario("TopicInfoResourceSimulation")
    .during(30) {
      feed(feeder).
      exec(http("GetTopicsList")
        .get("/topicslist")
        .check(status.is(200)))
    }

  setUp(
    scn.inject(atOnceUsers(10))
  ).protocols(httpConf)
}