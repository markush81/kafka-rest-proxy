# kafka-rest-proxy
Get your data via REST into Apache Kafka

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
	brew services start zookeeper
	zkServer start
	```
	
3. Create a kafka topic

	```
	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
	```

4. Check if topic is there

   ```
   bin/kafka-topics.sh --list --zookeeper localhost:2181
   ```

## Usage of kafka-rest-proxy

1. Clone git repository
2. Build executable `./gradlew clean build`
3. Start kafka-rest-proxy `java -jar build/libs/kafka-rest-proxy-0.0.1.jar`

## Configure kafka-rest-proxy

At the moment nothing needed. Further configuration will be SSL, in case your kafka is "protected" by SSL.

## REST API

### POST to any topic

```
curl -XPOST -d "{\"name\":\"Marku Helbig\", \"message\": \"kafka-rest-proxy first version is out!\"}" http://localhost/topics/test
```

### GET status and other information

```
curl -XGET http://localhost/info
curl -XGET http://localhost/topics/info
curl -XGET http://localhost/topcis/test/info
```

-

\* Note: kafka-0.10.0.1, java version "1.8.0_102", Homebrew 0.9.9
