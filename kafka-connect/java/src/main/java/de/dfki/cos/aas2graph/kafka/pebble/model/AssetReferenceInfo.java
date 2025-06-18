package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.Objects;

public class AssetReferenceInfo extends ReferenceInfo {

	private String assetId;

	public AssetReferenceInfo(String assetId) {
		super("globalAssetId", "AssetId");
		this.assetId = assetId;	
	}
	
	public String getId() {
		return assetId;
	}

	@Override
	public String getLabel() {
		return "Asset";
	}

	@Override
	public boolean isSemantics() {
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assetId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssetReferenceInfo other = (AssetReferenceInfo) obj;
		return Objects.equals(assetId, other.assetId);
	}

	@Override
	public String toString() {
		return "AssetReferenceInfo [assetId=" + assetId + "]";
	}
	
	
}
