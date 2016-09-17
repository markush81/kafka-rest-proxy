# producer-rest-proxy
Get your data via REST into Apache Kafka

# WARNING - Proof of Concept

```
This is currently in a proof of concept stage! Pls. be aware when using it.
```

## Idea

Often in your environment you might not be allowed to directly access your producer from web (e.g. you have a web- and application-zone, but producer is installed in application zone and not accessible via direct-path). But still you have the need to get data coming from outside into your topics. So one easy way is: producer-rest-proxy. It provides a simple RESTful API, forwarding your payload to producer.

## Prerequiste

1. Install [Apache Kafka](http://producer.apache.org) download and unarchive or `brew install producer`
2. Start producer

	```
	bin/zookeeper-server-start.sh config/zookeeper.properties
	bin/producer-server-start.sh config/server.properties
	```
	
	or if installed via brew
	
	```
	zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
	kafka-server-start /usr/local/etc/kafka/server.properties
	```
	
3. Create a producer topic

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

## Usage of producer-rest-proxy

1. Clone git repository
2. Build executable `./gradlew clean build`
3. Start `./gradlew run`

## Configure producer-rest-proxy

Configuration file can be found in `src/main/resources` and is named `application.yml`.

- Put all Apache Kafka properties beneath node `producer` (see [Configuration](http://producer.apache.org/documentation.html#configuration))
- For configuration of [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/html/) see [Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## REST API

### POST to any topic

```
curl -XPOST -H "Content-Type:application/json" -d "{\"name\":\"Markus Helbig\", \"message\": \"producer-rest-proxy proof of concept is on github.\"}" http://localhost:8080/topics/test
```

### GET available topics

```
curl -XGET http://localhost:8080/topics/info
```

## Healthcheck

```
curl -XGET http://localhost:8080/health
```

-

\* Note: producer-0.10.0.1, spring-boot-1.4.0, java-1.8.0_102, Homebrew-0.9.9
