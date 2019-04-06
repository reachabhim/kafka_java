package com.github.abhim.kafka.tutorial;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerThreadDemo {
	public static void main(String args[]) {
		
		new ConsumerThreadDemo().run();
		
	}
	public ConsumerThreadDemo() {
		
	}
	
	public void run() {
		final Logger logger = LoggerFactory.getLogger(ConsumerThreadDemo.class);
		String topic = "first_topic";
		String boostrapServers = "localhost:9092";
		String groupId = "my_first_app";
		
		//latch for dealing with multiple thread
		CountDownLatch latch =new CountDownLatch(1);
		
		logger.info("Creating the consumer thread!");

		//create the consumer runnable
		Runnable myConsumerRunnable = new ConsumerRunnable(latch, topic, boostrapServers, groupId);
	
		//start the thread
		Thread myThread = new Thread(myConsumerRunnable);
		myThread.start();
		
		
		//add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread( ()-> {
			logger.info("Caught shutdown hook");
			((ConsumerRunnable) myConsumerRunnable).shutdown();
			try {
				latch.wait();
			}catch(Exception e) {
				e.printStackTrace();
			}
			logger.info("Application has exited!");
		}));
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("Application got interrupted",e);
		}finally {
			logger.info("Application is closing");
		}
	}

	public class ConsumerRunnable implements Runnable {
		private CountDownLatch latch;
		private KafkaConsumer<String, String> consumer;

		final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

		public ConsumerRunnable(CountDownLatch latch, String topic, String boostrapServers, String groupId) {
			this.latch = latch;

			// Create consumer properties
			Properties properties = new Properties();
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			// create consumer
			consumer = new KafkaConsumer<String, String>(properties);

			// subscribe to topic(s)
			consumer.subscribe(Collections.singleton(topic));
		}

		public void run() {
			//poll for new data
			try {
				while (true) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

					for (ConsumerRecord<String, String> record : records) {
						logger.info("key: " + record.key() + " ,value: " + record.value());
						logger.info("partition: " + record.partition() + " ,Offset: " + record.offset());
					}

				}
			} catch (WakeupException we) {
				logger.error("Received shutdown signal!");
			}finally {
				consumer.close();
				
				//tell the main code that we are done with the consumer
				latch.countDown();
			}

		}

		// wakeup method is a special method to interrupt consumer.poll
		public void shutdown() {
			consumer.wakeup();
		}

	}

}
