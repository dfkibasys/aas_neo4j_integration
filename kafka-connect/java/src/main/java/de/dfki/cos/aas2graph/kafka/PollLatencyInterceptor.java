package de.dfki.cos.aas2graph.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public class PollLatencyInterceptor implements ConsumerInterceptor<Object, Object> {
	private long lastPollTime = System.nanoTime();

	@Override
	public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> records) {
		long now = System.nanoTime();
		long waitMs = (now - lastPollTime) / 1_000_000;
		System.out.println("CONNECT_POLL_WAIT=" + waitMs + "ms for " + records.count() + " records");
		lastPollTime = now;
		return records;
	}
	// (commit, onCommit, close, configure … leer lassen)

	@Override
	public void configure(Map<String, ?> configs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}