package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PutShellOperation extends IntegrationTestOperation<AssetAdministrationShell> {

	private final String id;
	private final AssetAdministrationShell body;

	@JsonCreator
	public PutShellOperation(@JsonProperty("id") String id, @JsonProperty("body") AssetAdministrationShell body) {
		this.id = id;
		this.body = body;
	}

	@Override
	public AssetAdministrationShell getBody() {
		return body;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.aasRepo().updateAas(id, body);
	}
}
