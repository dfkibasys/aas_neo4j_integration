
package de.dfki.cos.aas2graph.kafka.events;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;


public class SubmodelEvent implements Event {

	private SubmodelEventType type;
	private String id;
	private Submodel submodel;
	private SubmodelElement smElement;
	private String smElementPath;
	
	public SubmodelEventType getType() {
		return type;
	}
	
	public void setType(SubmodelEventType type) {
		this.type = type;
	}

	public Submodel getSubmodel() {
		return submodel;
	}
	
	public void setSubmodel(Submodel submodel) {
		this.submodel = submodel;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setSmElement(SubmodelElement element) {
		this.smElement = element;
	}
	
	public SubmodelElement getSmElement() {
		return smElement;
	}
	
	public void setSmElementPath(String path) {
		this.smElementPath = path;
	}
	
	public String getSmElementPath() {
		return smElementPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, smElement, smElementPath, submodel, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmodelEvent other = (SubmodelEvent) obj;
		return Objects.equals(id, other.id) && Objects.equals(smElement, other.smElement)
				&& Objects.equals(smElementPath, other.smElementPath) && Objects.equals(submodel, other.submodel)
				&& type == other.type;
	}

	@Override
	public String toString() {
		return "SubmodelEvent [type=" + type + ", id=" + id + ", submodel=" + submodel + ", smElement=" + smElement
				+ ", smElementPath=" + smElementPath + "]";
	}
}
