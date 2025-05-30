package de.dfki.cos.aas2graph.kafka.pebble.model;

public abstract class ReferenceInfo {

	private final String refType;

	public ReferenceInfo(String refType) {
		this.refType = refType;
	}

	public abstract String getLabel();

	public String getRefType() {
		return refType;
	}
	
	public abstract boolean isSemantics();
}

