# kafka-rest-proxy
Get your data via REST into Apache Kafka

[![Build Status](https://travis-ci.org/markush81/kafka-rest-proxy.svg?branch=master)](https://travis-ci.org/markush81/kafka-rest-proxy) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=markush81.org.mh.kafka.rest.proxy)](https://sonarqube.com/dashboard/index/markush81.org.mh.kafka.rest.proxy)


## Idea

Often in your environment you might not be allowed to directly access your kafka from web (e.g. you have a web- and application-zone, but kafa is installed in application zone and not accessible via direct-path). But still you have the need to get data coming from outside into your topics. So one easy way is: kafka-rest-proxy. It provides a simple RESTful API, forwarding your payload to kafka.

## Prerequiste

1. Install [Apache Kafka](http://kafka.apache.org) download and unarchive or `brew install kafka`
2. Start kafka

	```
	bin/zookeeper-server-start.sh config/zookeeper.properties
	bin/kafka-server-start.sh config/server.properties
	```
	
	or if installed via brew
	
	```
	zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
	kafka-server-start /usr/local/etc/kafka/server.properties
	```
	
3. Create a kafka topic

   Homebrew:
   ```
   kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
   ```

4. Check if topic is there

   Homebrew:
   ```
   kafka-topics --list --zookeeper localhost:2181
   ```

5. Start a console conusmer to check your posted messsages

   Homebrew:
   ```
   kafka-console-consumer --topic test --zookeeper localhost:2181
   ```

## Usage of kafka-rest-proxy

1. Clone git repository
2. Build executable `./gradlew clean build`
3. Start `./gradlew bootRun`

## Configure kafka-rest-proxy

Configuration file can be found in `src/main/resources` and is named `application.yml`.

- Put all Apache Kafka properties beneath node `producer` (see [Configuration](http://kafka.apache.org/documentation.html#configuration))
- For configuration of [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/html/) see [Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## REST API

### POST to any topic

```
curl -XPOST -H "Content-Type:application/json" -d "{\"message\": \"kafka-rest-proxy is on github.\"}" http://localhost:8080/topics/test

curl -XPOST -H "Content-Type:application/json" -d "{\"message\": \"kafka-rest-proxy is on github.\"}" http://localhost:8080/topics/test?key=key

```

### Information endpoints

```
curl -XGET http://localhost:8080/topicslist  //since consumer is not thread-safe topic-list gets up-to-date approx. every 2 sec in a scheduled task.

curl -XGET http://localhost:8080/metrics

curl -XGET http://localhost:8080/topicsinfo/test
```


### Healthcheck

```
curl -XGET http://localhost:8080/health
```

-

\* Note: spring-boot-1.4.1, spring-kafka-1.1.1, java-1.8.0_102, Homebrew-1.0.0
