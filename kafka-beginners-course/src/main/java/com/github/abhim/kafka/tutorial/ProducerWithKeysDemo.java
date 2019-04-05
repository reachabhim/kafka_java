package com.github.abhim.kafka.tutorial;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerWithKeysDemo {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final Logger logger = LoggerFactory.getLogger(ProducerWithKeysDemo.class);
		String boostrapServers = "localhost:9092";
		
		//Create producer properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//Create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		for (int i=1;i<=10;i++) {
			String topic = "first_topic";
			String value = "Hello_"+i;
			String key = "key_ID_"+i;
			
			logger.info("KEY: "+key); //log the key
			
			//Create producer record with KEY
			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, key, value);
			
			//send data- asynchronous
			producer.send(producerRecord, new Callback() {
				//executes every time a record being sent or exception being thrown
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					if(e==null) {
						logger.info("Received new metadata: \n"
								+ "Topic: "+recordMetadata.topic()+" \n"
										+ "Partition: "+recordMetadata.partition()+" \n"
												+ "Offset: "+recordMetadata.offset()+" \n"
														+ "Timestamp: "+recordMetadata.timestamp());
					}else {
						logger.error("Error while producing message: "+e);
					}
					
				}
			}).get(); //block send to make it synchronous- DONT DO in PRODUCTION CODE
		}
		
		
		//flush data
		producer.flush();
		
		//close producer
		producer.close();
	}

}
