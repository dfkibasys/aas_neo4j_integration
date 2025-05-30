package de.dfki.cos.aas2graph.kafka.pebble.model;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.dfki.cos.aas2graph.kafka.util.AasIo;

public abstract class ReferableReferenceInfo extends ReferenceInfo {


	private final KeyTypes targetType;
	
	public ReferableReferenceInfo(KeyTypes targetType, String refType) {
		super(refType);
		this.targetType = targetType;
	}
	
	@Override
	public String getLabel() {
		try {
			String label = AasIo.jsonMapper().writeValueAsString(targetType);
			if (label.length() > 1) {
				return label.substring(1, label.length() - 1);
			} else {
				return label;
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean isSemantics() {
		return false;
	}
}
