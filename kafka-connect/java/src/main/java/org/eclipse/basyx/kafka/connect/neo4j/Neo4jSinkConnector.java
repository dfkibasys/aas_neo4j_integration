package org.eclipse.basyx.kafka.connect.neo4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

public class Neo4jSinkConnector extends SinkConnector {

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
		return Neo4jSinkTask.class;
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
		return Neo4jSinkConnectorConfig.config();
	}

}
