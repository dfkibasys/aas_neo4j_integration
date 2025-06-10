package de.dfki.cos.aas2graph.kafka.docker.events;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.testcontainers.containers.output.OutputFrame;

import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredKafkaNeo4jPluginContainer;

public class Neo4jKafkaPluginResultResolver implements AutoCloseable {

	private final LinkedBlockingDeque<ProcessingEvent> eventDeque = new LinkedBlockingDeque<>();
	private final EventProcessor eventProcessor;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private final AtomicBoolean isRunning = new AtomicBoolean();

	public Neo4jKafkaPluginResultResolver(ConfiguredKafkaNeo4jPluginContainer container) {
		eventProcessor = new EventProcessor(eventDeque);
		container.withLogConsumer(eventProcessor);
	}

	@Override
	public void close() throws Exception {
		isRunning.set(false);
		service.shutdown();
		service.awaitTermination(2, TimeUnit.SECONDS);
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
			System.err.println(msg);
			Optional<HttpResponseLog> responseOpt = HttpResponseLog.fromLog(msg);
			if (responseOpt.isPresent()) {
				deque.offer(responseOpt.get());
			}
		}

	}

}
