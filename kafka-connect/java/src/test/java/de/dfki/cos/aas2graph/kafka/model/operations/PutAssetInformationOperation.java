package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PutAssetInformationOperation extends IntegrationTestOperation<AssetInformation> {

	private final String id;
	private final AssetInformation info;
	
	@JsonCreator
	public PutAssetInformationOperation(@JsonProperty("id") String id, @JsonProperty("body") AssetInformation info) {
		this.id = id;
		this.info = info;
	}
	
	@Override
	public AssetInformation getBody() {
		return info;
	}
	
	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.aasRepo().setAssetInformation(id, info);
	}
}
