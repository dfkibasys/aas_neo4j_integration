package de.dfki.cos.aas2graph.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

public class TransformationAdapter {

	
	 public void start() {
	        String bootstrapServers = "localhost:9099"; 
	        String groupId = "debug-transformer";

	        Properties props = new Properties();
	        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
	        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

	        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
	        consumer.subscribe(List.of("aas-events", "submodel-events"));

	        CypherTransformation<SourceRecord> transformer = new CypherTransformation<>();

	        while (true) {
	            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
	            for (ConsumerRecord<String, String> record : records) {
	                // Dummy SourceRecord bauen
	                SourceRecord srcRecord = new SourceRecord(
	                        null, null,
	                        record.topic(), record.partition(),
	                        Schema.STRING_SCHEMA, record.key(),
	                        Schema.STRING_SCHEMA, record.value(),
	                        record.timestamp()
	                );

	                try {
	                    SourceRecord transformed = transformer.apply(srcRecord);
	                    System.out.println("Transformed payload:");
	                    System.out.println(transformed.value());
	                } catch (Exception e) {
	                    System.err.println("Transformation failed: " + e.getMessage());
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	
}
