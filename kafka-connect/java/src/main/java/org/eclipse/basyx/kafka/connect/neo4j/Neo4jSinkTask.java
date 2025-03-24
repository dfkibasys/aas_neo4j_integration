package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jSinkTask extends SinkTask {

	private static final Logger log = LoggerFactory.getLogger(Neo4jSinkTask.class);

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
		this.targetUrl = props.get(Neo4jSinkConnectorConfig.TARGET_URL);
		this.headers = Neo4jSinkConnectorConfig.parseHeaders(props.get(Neo4jSinkConnectorConfig.HEADERS));
		this.maxRetries = Integer.parseInt(props.getOrDefault(Neo4jSinkConnectorConfig.RETRY_MAX, "3"));
		this.backoffMs = Long.parseLong(props.getOrDefault(Neo4jSinkConnectorConfig.RETRY_BACKOFF_MS, "1000"));
		this.connectTimeoutMs = Integer.parseInt(props.getOrDefault(Neo4jSinkConnectorConfig.CONNECT_TIMEOUT_MS, "10000"));
		this.readTimeoutMs = Integer.parseInt(props.getOrDefault(Neo4jSinkConnectorConfig.READ_TIMEOUT_MS, "10000"));
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
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				log.debug("Sending record to {}", targetUrl);
				HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
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
					os.write(input, 0, input.length);
				}

				int status = conn.getResponseCode();
				if (status >= 200 && status < 300) {
					log.info("Successfully sent record to {} [HTTP {}]", targetUrl, status);
					success = true;
					break;
				} else {
					log.warn("HTTP error code: {} on attempt {}", status, attempt);
				}

			} catch (Exception e) {
				log.error("Error on attempt {} to send record", attempt, e);
			}

			try {
				Thread.sleep(backoffMs);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		if (!success) {
			throw new RuntimeException("Failed to send record after " + maxRetries + " attempts.");
		}
	}

	@Override
	public void stop() {
		if (transformation != null) {
			transformation.close();
		}
	}
}
