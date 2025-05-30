package de.dfki.cos.aas2graph.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

public class Kafka2Neo4jConnector extends SinkConnector {

	private Map<String, String> configProps;

	@Override
	public String version() {
		return "1.0.0";
	}

	@Override
	public void start(Map<String, String> props) {
		this.configProps = props;
	}

	@Override
	public Class<? extends Task> taskClass() {
		return Kafka2Neo4jSinkTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {
		List<Map<String, String>> configs = new ArrayList<>();
		for (int i = 0; i < maxTasks; i++) {
			configs.add(configProps);
		}
		return configs;
	}

	@Override
	public void stop() {
	}

	@Override
	public ConfigDef config() {
		return Kafka2Neo4jConnectorConfig.config();
	}

}
