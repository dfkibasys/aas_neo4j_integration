package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

public class AssetReferenceInfo extends ReferenceInfo {

	private String assetId;

	public AssetReferenceInfo(String assetId) {
		super("globalAssetId");
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
}
