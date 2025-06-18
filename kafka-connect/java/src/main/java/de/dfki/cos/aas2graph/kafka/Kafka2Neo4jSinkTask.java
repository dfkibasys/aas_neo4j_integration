package de.dfki.cos.aas2graph.kafka;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Kafka2Neo4jSinkTask extends SinkTask {

	private final HttpClient client = HttpClient.newHttpClient();
	private static final Logger log = LoggerFactory.getLogger(Kafka2Neo4jSinkTask.class);

	private String targetUrl;
	private Map<String, String> headers;
	private int maxRetries;
	private long backoffMs;
	private int connectTimeoutMs;
	private int readTimeoutMs;

	private CypherTransformation<SinkRecord> transformation;

	@Override
	public String version() {
		return "1.0.0";
	}

	@Override
	public void start(Map<String, String> props) {
		transformation = new CypherTransformation<SinkRecord>();
		this.targetUrl = props.get(Kafka2Neo4jConnectorConfig.TARGET_URL);
		this.headers = Kafka2Neo4jConnectorConfig.parseHeaders(props.get(Kafka2Neo4jConnectorConfig.HEADERS));
		this.maxRetries = Integer.parseInt(props.getOrDefault(Kafka2Neo4jConnectorConfig.RETRY_MAX, "3"));
		this.backoffMs = Long.parseLong(props.getOrDefault(Kafka2Neo4jConnectorConfig.RETRY_BACKOFF_MS, "1000"));
		this.connectTimeoutMs = Integer.parseInt(props.getOrDefault(Kafka2Neo4jConnectorConfig.CONNECT_TIMEOUT_MS, "10000"));
		this.readTimeoutMs = Integer.parseInt(props.getOrDefault(Kafka2Neo4jConnectorConfig.READ_TIMEOUT_MS, "10000"));
		
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		if (records.isEmpty()) {
			return;
		}
		
		for (SinkRecord r : records) {
			long startTime = System.nanoTime();
			long afterTrans = writeRecord(r);
			long now = System.nanoTime();
			long fromIns = System.currentTimeMillis() - r.timestamp();
			long transTime = (afterTrans - startTime) / 1_000_000;
			long httpTime = (now - afterTrans) / 1_000_000;
			long total = (now - startTime) / 1_000_000;
			
			log.info("EVAL-RECORD-{}-{}-transformation{}ms-httpCypher{}-total{}-sinceInsertion{}",  r.topic(), r.key(), transTime, httpTime, total, fromIns);
		}
	}

	private long writeRecord(SinkRecord record) {

		String payload = (String) transformation.apply(record).value();
		
		long afterTrans = System.nanoTime();
		boolean success = false;
		int status = -1;
		String responseBody = null;
		
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(targetUrl));
		for (Entry<String, String> eachHeader : headers.entrySet()) {
			builder.header(eachHeader.getKey(), eachHeader.getValue());
		}
		builder.timeout(Duration.ofMillis(connectTimeoutMs));
		log.info("Sending cypher request: {}", payload);
		HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(payload)).build();
		
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			
			try {
				HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
				status = response.statusCode();
				ObjectMapper mapper = AasIo.jsonMapper();
				responseBody = new String(response.body(), StandardCharsets.UTF_8);
				JsonNode root = mapper.readTree(response.body());
				JsonNode errors = root.path("errors");
				
				if (errors.isArray() && errors.size() > 0) {
					log.error("Neo4j responded with semantic/logical errors: {}", errors.toPrettyString());
					throw new ConnectException("Neo4j rejected the query: " + errors.toString());
				}

				log.info("HttpResponse: Successfully sent record! statusCode={} statusMessage='Success' responseBody='{}' ", status, responseBody);
				success = true;
				break;

			} catch (Exception e) {
				log.error("Error while sending record (attempt {}): {}", attempt, e.getMessage(), e);
			}

			try {
				Thread.sleep(backoffMs);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		
		
		if (!success) {
			log.error("HttpResponse: Failed to send record! statusCode={} statusMessage='Failed' responseBody='{}' ", status, responseBody);

			throw new ConnectException("Failed to send record after " + maxRetries + " attempts.");
		}
		log.error("HttpResponse: Successfully send record! statusCode={} statusMessage='Success' responseBody='{}' ", status, responseBody);
	
		return afterTrans;
	}

	@Override
	public void stop() {
		if (transformation != null) {
			transformation.close();
		}
	}
}
