package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;

public class IdentifiableReferenceInfo extends ReferableReferenceInfo {

	private final String id;
	
	public IdentifiableReferenceInfo(KeyTypes targetType, String id, String refType) {
		super(targetType, refType);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
