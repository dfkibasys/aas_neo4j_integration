package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.Objects;

public class ParentInfo {

	private  String idShortPath;
	
	public String getIdShortPath() {
		return idShortPath;
	}

	public void setIdShortPath(String idShortPath) {
		this.idShortPath = idShortPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idShortPath);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParentInfo other = (ParentInfo) obj;
		return Objects.equals(idShortPath, other.idShortPath);
	}

	@Override
	public String toString() {
		return "ParentInfo [idShortPath=" + idShortPath + "]";
	}
}
