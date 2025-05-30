package de.dfki.cos.aas2graph.kafka;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class  Kafka2Neo4jSinkTask extends SinkTask {

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
		for (SinkRecord record : records) {
			writeRecord(record);
		}
	}

	private void writeRecord(SinkRecord record) {
		String payload = (String) transformation.apply(record).value();
		boolean success = false;
		int status = -1;
		String responseBody = null;
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			HttpURLConnection conn = null;

			try {
				log.debug("Sending record to {} (attempt {})", targetUrl, attempt);
				conn = (HttpURLConnection) new URL(targetUrl).openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(connectTimeoutMs);
				conn.setReadTimeout(readTimeoutMs);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");

				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}

				try (OutputStream os = conn.getOutputStream()) {
					byte[] input = payload.getBytes(StandardCharsets.UTF_8);
					os.write(input);
				}

				status = conn.getResponseCode();

				try (InputStream is = conn.getInputStream()) {
					responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				}

				ObjectMapper mapper = AasIo.jsonMapper();
				JsonNode root = mapper.readTree(responseBody);
				JsonNode errors = root.path("errors");

				if (errors.isArray() && errors.size() > 0) {
					log.error("Neo4j responded with semantic/logical errors: {}", errors.toPrettyString());
					throw new ConnectException("Neo4j rejected the query: " + errors.toString());
				}

				log.info("HttpResponse: Successfully sent record! statusCode={} statusMessage='Success' responseBody='{}' ", status , responseBody);
				success = true;
				break;

			} catch (Exception e) {
				log.error("Error while sending record (attempt {}): {}", attempt, e.getMessage(), e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				Thread.sleep(backoffMs);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		if (!success) {
			log.info("HttpResponse: Failed to send record! statusCode={} statusMessage='Success' responseBody='{}' ", status , responseBody);
			throw new ConnectException("Failed to send record after " + maxRetries + " attempts.");
		}
	}

	@Override
	public void stop() {
		if (transformation != null) {
			transformation.close();
		}
	}
}
