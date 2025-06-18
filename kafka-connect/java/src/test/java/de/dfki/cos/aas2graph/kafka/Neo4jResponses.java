package de.dfki.cos.aas2graph.kafka;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResult;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResultNode;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResultRelationShips;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Neo4jResponses {

	public Neo4jResponses() {
	}

	public static IntegrationTestResult toTestResult(String json) throws IOException, DeserializationException {
		ObjectMapper mapper = AasIo.jsonMapper();
		Neo4jResponse response = mapper.readValue(json, Neo4jResponse.class);
		IntegrationTestResult result = new IntegrationTestResult();
	
		addErrors(response.errors, result.getErrors());

		Set<String> seenNodeIds = new HashSet<>();
		Set<String> seenRelationshipIds = new HashSet<>();
		if (response.results == null) {
			return result;
		}
		for (Result res : response.results) {
			for (Data data : res.data) {
				Graph graph = data.graph;
				// Nodes
				for (Node node : graph.nodes) {
					if (seenNodeIds.add(node.id)) {
						addNodes(node, result.getNodes());
					}
				}

				for (Relationship rel : graph.relationships) {
					if (seenRelationshipIds.add(rel.id)) {
						addRelationShip(rel, result.getRelationships());
					}
				}
			}
		}

		return result;
	}

	private static void addNodes(Node node, List<IntegrationTestResultNode> nodes) {
		IntegrationTestResultNode newNode = new IntegrationTestResultNode();
		newNode.setId(node.id);
		newNode.setLabels(node.labels);
		newNode.setProperties(node.properties);
		nodes.add(newNode);
	}

	private static void addRelationShip(Relationship rel, List<IntegrationTestResultRelationShips> relationships) {
		IntegrationTestResultRelationShips newRel = new IntegrationTestResultRelationShips();
		newRel.setId(rel.id);
		newRel.setType(rel.type);
		newRel.setStartNode(rel.startNode);		
		newRel.setEndNode(rel.endNode);
		newRel.setProperties(rel.properties);
		relationships.add(newRel);
	}

	private static void addErrors(List<Object> source, List<String> target) {
		if (source == null) {
			return;
		}
		source.stream().map(String::valueOf).forEach(target::add);
	}

	private static class Neo4jResponse {
		public List<Result> results;
		public List<Object> errors;
		public List<String> lastBookmarks;
	}

	static class Result {
		public List<String> columns;
		public List<Data> data;
	}

	private static class Data {
		public Graph graph;
	}

	private static class Graph {
		public List<Node> nodes;
		public List<Relationship> relationships;
	}

	private static class Node {
		public String id;
		public String elementId;
		public List<String> labels;
		public Map<String, Object> properties;
	}

	private static class Relationship {
		public String id;
		public String elementId;
		public String type;
		public String startNode;
		public String endNode;
		public String startNodeElementId;
		public String endNodeElementId;
		public Map<String, Object> properties;
	}
}
