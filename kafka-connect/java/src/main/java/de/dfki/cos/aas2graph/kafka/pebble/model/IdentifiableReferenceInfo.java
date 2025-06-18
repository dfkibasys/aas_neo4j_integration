package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;

public class IdentifiableReferenceInfo extends ReferableReferenceInfo {

	private final String id;
	
	public IdentifiableReferenceInfo(KeyTypes targetType, String id, String refName, String refType) {
		super(targetType, refName, refType);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifiableReferenceInfo other = (IdentifiableReferenceInfo) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "IdentifiableReferenceInfo [id=" + id + "]";
	}

	
}
