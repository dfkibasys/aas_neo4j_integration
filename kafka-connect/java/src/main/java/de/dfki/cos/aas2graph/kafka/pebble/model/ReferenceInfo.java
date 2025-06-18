package de.dfki.cos.aas2graph.kafka.pebble.model;

public abstract class ReferenceInfo {
	private final String refName;
	private final String refType;

	public ReferenceInfo(String refName, String refType) {
		this.refName = refName;
		this.refType = refType;
	}

	public abstract String getLabel();

	public String getRefName() {
		return refName;
	}
	
	public String getRefType() {
		return refType;
	}
	
	
	public abstract boolean isSemantics();
}

