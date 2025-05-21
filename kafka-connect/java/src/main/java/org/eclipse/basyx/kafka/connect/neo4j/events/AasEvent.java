package org.eclipse.basyx.kafka.connect.neo4j.events;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public class AasEvent implements Event {

	private AasEventType type;
	private String id;
	private String submodelId;
	private AssetAdministrationShell aas;
	private Reference reference;
	private AssetInformation assetInformation;
	
	public AasEventType getType() {
		return type;
	}
	
	public void setType(AasEventType type) {
		this.type = type;
	}

	public void setAas(AssetAdministrationShell shell) {
		this.aas = shell;
	}
	
	public AssetAdministrationShell getAas() {
		return aas;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
	
	public Reference getReference() {
		return reference;
	}

	public void setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
	}
	
	public String getSubmodelId() {
		return submodelId;
	}

	public void setAssetInformation(AssetInformation aasInfo) {
		this.assetInformation = aasInfo;
	}	
	
	public AssetInformation getAssetInformation() {
		return assetInformation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aas, assetInformation, id, reference, submodelId, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AasEvent other = (AasEvent) obj;
		return Objects.equals(aas, other.aas) && Objects.equals(assetInformation, other.assetInformation)
				&& Objects.equals(id, other.id) && Objects.equals(reference, other.reference)
				&& Objects.equals(submodelId, other.submodelId) && type == other.type;
	}

	@Override
	public String toString() {
		return "AasEvent [type=" + type + ", id=" + id + ", submodelId=" + submodelId + ", aas=" + aas + ", reference="
				+ reference + ", assetInformation=" + assetInformation + "]";
	}
}
