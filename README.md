# kafka-rest-proxy
Get your data via REST into Apache Kafka

# WARNING - Proof of Concept

```
I just started, so it is far away from being complete ... and has to be considered as proof of concept
```

## Idea

Often in your environment you might not be allowed to directly access your kafka from web (e.g. you have a web- and application-zone, but kafka is installed in application zone and not accessible via direct-path). But still you have the need to get data coming from outside into your topics. So one easy way is: kafka-rest-proxy. It provides a simple RESTful API, forwarding your payload to kafka.

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
3. Start `./gradlew run`

## Configure kafka-rest-proxy

Configuration file can be found in `src/conf/` and is named `kafka-rest-proxy.yml`.

- Put all Apache Kafka properties beneath node `kafka` (see [Configuration](http://kafka.apache.org/documentation.html#configuration))
- For configuration of [Dropwizard](http://www.dropwizard.io) see [Configuration](http://www.dropwizard.io/1.0.0/docs/manual/core.html#configuration)

## REST API

### POST to any topic

```
curl -XPOST -H "Content-Type:application/json" -d "{\"name\":\"Markus Helbig\", \"message\": \"kafka-rest-proxy proof of concept is out\"}" http://localhost:8080/topics/test
```

### GET status and other information

```
curl http://localhost:8081/topics
```

## Healthcheck

```
curl http://localhost:8081/healthcheck
```

-

\* Note: kafka-0.10.0.1, dropwizard-1.0.0, java-1.8.0_102, Homebrew-0.9.9
