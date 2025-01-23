package org.eclipse.basyx.kafka.connect.neo4j.model;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTestResult {

	private List<String> errors = new ArrayList<String>();
		
	private List<IntegrationTestResultNode> nodes = new ArrayList<>();

	private List<IntegrationTestResultRelationShips> relationships = new ArrayList<>();

	public List<String> getErrors() {
		return errors;
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	public List<IntegrationTestResultNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<IntegrationTestResultNode> nodes) {
		this.nodes = nodes;
	}

	public List<IntegrationTestResultRelationShips> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<IntegrationTestResultRelationShips> relationships) {
		this.relationships = relationships;
	}
}
