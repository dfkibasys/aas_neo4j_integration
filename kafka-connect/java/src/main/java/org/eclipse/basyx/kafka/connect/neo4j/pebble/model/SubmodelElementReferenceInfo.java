package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;

public class SubmodelElementReferenceInfo extends ReferableReferenceInfo {

	private final String idShortPath;
	private final String id;

	public SubmodelElementReferenceInfo(KeyTypes targetType, String id, String idShortPath, String refType) {
		super(targetType, refType);
		this.idShortPath =  idShortPath;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getIdShortPath() {
		return idShortPath;
	}
}
