KAFKA
-----------------------
1. Kafka is Distributed messaging system
2. Kafka has Topic(s) which receives messaged
	- Each topic is has partition, and partition is having offset numbering to store message
3. In a typical Kafka cluster has multiple Brokers(servers), 3 brokers is a good number to start with
	- Partition of topic can be distributed across multiple broker
	- With replication setting a topic partition will be duplicated across multiple broker(HA)
		- There will be leader partition which will receive the message, and it will be replicated to replicated to other partition in another broker.

4. Producers
	- Write data to topics
	- Automatically know which broker, partition to write
	- Recovers when Broker fails
	- Can choose to receive acknowledgement of data writes
		- acks=0: Wont wait for acknowledgement (possible data loss)
		- acks=1: Wait for leader to acknowledgement (limited data loss)
		- acks=all: Wait for leader and replicas to acknowledgement  (no data loss)
5. Message Keys
	- Producers can choose the send a key with message
		- Keys guarantees to the message with same key will be always in same partition
			- ex. GPS data from same truck
		- No Key, data will be stored in partitions a round robin

6. Consumer
	-Read data from topic
	-knows which broker to read
	- In case of broker failure automatically recovers
	- Data is read in order for partition
7. Consumer Group
	- Consumer read in consumer groups
	- Each consumer within a group reads from exclusive partition
	- If number of consumer more than partition, then some consumer will be inactive
		- 3 Consumer, 3 Partition-> Each consumer will read from dedicated partition
		- 2 Consumer, 3 Partition-> One consumer will read from two partition and second consumer will read from the other partition

8. Consumer offsets
	- Kafka stored offsets at which a consumer groups is reading
	- It stores the current read offset in topic named '__consumer_offsets'
	- It helps a consumer to resume reading from where it lefts if it has died for some reason
	- Consumer can choose when to commit offsets
		- At most once:
		- At least once
		- Exactly one

9. Broker Discovery
	- All broker are bootstrap broker
	- Need to connect one broker
	- Connected broker will give information abt all other broker

10. Zookeeper
	- Manages brokers, KAFKA cant work without it
	- Helps is performing leader election in partitions
	- Messages Kafka in case of changes(broker dies, new topic, broker comes up etc.)
	- Zookeeper designed to work with odd number of servers
	- Zookeeper has a leader(handle writes)and the rest servers are follower(handles reads)

11. Installation:
	- Install Java 8
	- Install Kafka
		- Download Binary from Apache Kafka site
		- Extract and add the bin to PATH environment variable
		- Create to folde data/zookeeper, data/kafka
		- Update config
			- Update config/zookeeper.properties to update data dir
			- Update config/server.properties to update logs.dir for Kafka
		- Start zookeeper
			- zookeeper-server-start.sh config/zookeeper.properties
		- Start Kafka 
			- kafka-server-start.sh config/server.properties
			
12. Kafka CLI
	- Create topic: kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1
	- List topic: kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
	- Describe topic: kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe
	- Delete topic: kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --delete

13. Kafka Console Producer CLI
	- Enter you message, ctrl+c when done
		- kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
	- Enter messages that require acknowledgement
		- kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all
	
	- *** If producing message for a topic which does not exist, first message will fail and the topic will be created with default number partition as defined in "num.partitions" of config/server.properties. (Note: Update to minimum 3)

14. Kafka Console Consumer CLI
	- kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
	- **It will read only real time messages, to read all messages use --from-beginning option
	
-Consumer in group
	- Start kafka-console-consumer.sh with Group options
		- kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my_first_app
		- kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my_first_app

16. Kafka Console Consumer Group CLI
	- List consumer groups
		- kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --list
	- Describe a consumer group
		-  kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my_first_app

17. Reset Offsets
	- Use the --reset-offsets option to reset offset number for consumer group
	- for more details on this option, see help of " kafka-console-consumer.sh"
