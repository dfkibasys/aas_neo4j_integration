package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(id, idShortPath);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmodelElementReferenceInfo other = (SubmodelElementReferenceInfo) obj;
		return Objects.equals(id, other.id) && Objects.equals(idShortPath, other.idShortPath);
	}

	@Override
	public String toString() {
		return "SubmodelElementReferenceInfo [idShortPath=" + idShortPath + ", id=" + id + "]";
	}
	
	
	
}
