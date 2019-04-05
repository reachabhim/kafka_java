package com.github.abhim.kafka.tutorial;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComsumerDemo {
	public static void main(String args[]) {
		final Logger logger = LoggerFactory.getLogger(ComsumerDemo.class);
		String boostrapServers = "localhost:9092";
		String groupId = "my_third_app";
		String topic = "first_topic";
		
		//Create producer properties
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		
		//create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		
		
		//subscribe to topic(s)
		consumer.subscribe(Collections.singleton(topic));
		while(true) {
			ConsumerRecords<String, String> records= consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String, String> record:records) {
				logger.info("key: "+record.key()+" ,value: "+record.value());
				logger.info("partition: "+record.partition()+" ,Offset: "+record.offset());
			}
			
		}
	}

}
