package de.dfki.cos.aas2graph.kafka.pebble.model;

public class SemanticConceptInfo extends ReferenceInfo {

	private final String id;
	
	public SemanticConceptInfo(String id, String refName, String refType) {
		super(refName, refType);
		this.id = id;
	}

	@Override
	public String getLabel() {
		return "SemanticConcept";
	}

	@Override
	public boolean isSemantics() {
		return true;
	}
	
	public String getId() {
		return id;
	}
}
