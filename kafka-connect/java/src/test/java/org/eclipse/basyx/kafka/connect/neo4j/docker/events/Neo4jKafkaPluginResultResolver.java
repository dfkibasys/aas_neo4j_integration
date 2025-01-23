package org.eclipse.basyx.kafka.connect.neo4j.docker.events;

import java.time.Duration;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.basyx.kafka.connect.neo4j.docker.containers.ConfiguredKafkaNeo4jPluginContainer;
import org.testcontainers.containers.output.OutputFrame;

public class Neo4jKafkaPluginResultResolver implements AutoCloseable {

	private final static String DEAD_LETTER_QUEUE = "dlq-aas-events";

	private final LinkedBlockingDeque<ProcessingEvent> eventDeque = new LinkedBlockingDeque<>();
	private final EventProcessor eventProcessor;
	private volatile KafkaConsumer<String, String> consumer;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private final AtomicBoolean isRunning = new AtomicBoolean();

	public Neo4jKafkaPluginResultResolver(ConfiguredKafkaNeo4jPluginContainer container) {
		eventProcessor = new EventProcessor(eventDeque);
		container.withLogConsumer(eventProcessor);
	}

	public void start(String externalBrokerAddress) {
		service.submit(() -> startKafkaConsumer(externalBrokerAddress));
	}

	private void startKafkaConsumer(String externalBrokerAddress) {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", externalBrokerAddress);
		properties.put("group.id", "test-runner");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(List.of(DEAD_LETTER_QUEUE));
		isRunning.set(true);
		while (isRunning.get()) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
			for (ConsumerRecord<String, String> eachRecord : records) {
				System.out.printf("Offset: %d, Key: %s, Value: %s%n", eachRecord.offset(), eachRecord.key(),
						eachRecord.value());
				eventDeque.offer(new DeadLetterQueueEvent(eachRecord.value()));
			}
		}
	}

	@Override
	public void close() throws Exception {
		isRunning.set(false);
		service.shutdown();
		service.awaitTermination(2, TimeUnit.SECONDS);
		consumer.close();
	}

	public void clear() {
		eventDeque.clear();
	}

	public ProcessingEvent awaitNext() throws InterruptedException {
		return eventDeque.poll(10, TimeUnit.MINUTES);
	}

	private static class EventProcessor implements Consumer<OutputFrame> {

		private Deque<ProcessingEvent> deque;

		public EventProcessor(Deque<ProcessingEvent> deque) {
			this.deque = deque;
		}

		@Override
		public void accept(OutputFrame t) {
			String msg = t.getUtf8String();
			Optional<HttpResponseLog> responseOpt = HttpResponseLog.fromLog(msg);
			if (responseOpt.isPresent()) {
				deque.offer(responseOpt.get());
			}
		}

	}

}
