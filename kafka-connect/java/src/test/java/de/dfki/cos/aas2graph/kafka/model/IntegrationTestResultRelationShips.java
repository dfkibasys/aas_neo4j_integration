package de.dfki.cos.aas2graph.kafka.model;

import java.util.HashMap;
import java.util.Map;

public class IntegrationTestResultRelationShips {

	private String id;
	private String type;
	private String startNode;
	private String endNode;
	private Map<String, Object> properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	public void setProperties(Map<String, Object> properties) {
		if (properties != null && !properties.isEmpty()) {
			this.properties = properties;
		}
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
}
