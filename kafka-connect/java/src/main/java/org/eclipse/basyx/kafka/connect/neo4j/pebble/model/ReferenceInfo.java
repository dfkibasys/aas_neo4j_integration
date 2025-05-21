package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

public abstract class ReferenceInfo {

	private final String refType;

	public ReferenceInfo(String refType) {
		this.refType = refType;
	}

	public abstract String getLabel();

	public String getRefType() {
		return refType;
	}
}

