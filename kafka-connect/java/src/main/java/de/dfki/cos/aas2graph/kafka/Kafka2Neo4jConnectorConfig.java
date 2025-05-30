package de.dfki.cos.aas2graph.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

public class Kafka2Neo4jConnectorConfig extends AbstractConfig {

	public static final String TARGET_URL = "target.url";
	public static final String HEADERS = "headers";
	public static final String RETRY_MAX = "retry.max.retries";
	public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
	public static final String CONNECT_TIMEOUT_MS = "http.connection.timeout.ms";
	public static final String READ_TIMEOUT_MS = "http.read.timeout.ms";

	public Kafka2Neo4jConnectorConfig(Map<String, String> props) {
        super(config(), props);
    }

	public static ConfigDef config() {
		return new ConfigDef().define(TARGET_URL, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Target HTTP URL").define(HEADERS, ConfigDef.Type.STRING, "", ConfigDef.Importance.MEDIUM, "Static HTTP headers")
				.define(RETRY_MAX, ConfigDef.Type.INT, 3, ConfigDef.Importance.MEDIUM, "Max retries").define(RETRY_BACKOFF_MS, ConfigDef.Type.LONG, 1000L, ConfigDef.Importance.MEDIUM, "Backoff in ms between retries")
				.define(CONNECT_TIMEOUT_MS, ConfigDef.Type.INT, 10000, ConfigDef.Importance.LOW, "Connect timeout in ms").define(READ_TIMEOUT_MS, ConfigDef.Type.INT, 10000, ConfigDef.Importance.LOW, "Read timeout in ms");
	}

	public static Map<String, String> parseHeaders(String rawHeaders) {
		Map<String, String> map = new HashMap<>();
		if (rawHeaders == null || rawHeaders.isBlank())
			return map;
		String[] entries = rawHeaders.split(",");
		for (String entry : entries) {
			String[] kv = entry.split(":", 2);
			if (kv.length == 2)
				map.put(kv[0].trim(), kv[1].trim());
		}
		return map;
	}

}
