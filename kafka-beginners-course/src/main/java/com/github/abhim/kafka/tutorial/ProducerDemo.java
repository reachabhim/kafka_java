package com.github.abhim.kafka.tutorial;

import java.util.Properties;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerDemo {

	public static void main(String[] args) {
		
		String boostrapServers = "localhost:9092";
		
		//Create producer properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//Create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		//Create producer record
		ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("first_topic", "hello world");
		
		//send data- aysnch
		producer.send(producerRecord);
		
		//flush data
		producer.flush();
		
		//close producer
		producer.close();
	}

}
