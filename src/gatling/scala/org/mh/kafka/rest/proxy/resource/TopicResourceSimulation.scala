package org.mh.kafka.rest.proxy.resource

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TopicResourceSimulation extends Simulation {

  val feeder = Iterator.continually(Map("message" -> s"""kafka-rest-proxy ${System.currentTimeMillis()}."""))

  val httpConf = http
    .baseURL("http://localhost:8080")
//    .contentTypeHeader("application/json;charset=UTF-8")

  val scn = scenario("TopicResourceSimulation")
    .during(60) {
      feed(feeder).
      exec(http("Post2Topic")
        .post("/topics/test")
        .body(StringBody("""{ "message": "${message}" }""")).asJSON
        .check(status.is(201)))
    }

  setUp(
    scn.inject(atOnceUsers(20))
  ).protocols(httpConf)
}