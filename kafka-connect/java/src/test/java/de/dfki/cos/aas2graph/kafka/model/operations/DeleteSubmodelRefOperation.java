package de.dfki.cos.aas2graph.kafka.model.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class DeleteSubmodelRefOperation extends IntegrationTestOperation<Void> {

	private final String id;
	private final String submodelId;

	@JsonCreator
	public DeleteSubmodelRefOperation(@JsonProperty("id") String id, @JsonProperty("submodelId") String submodelId) {
		this.id = id;		
		this.submodelId = submodelId;
	}

	@Override
	public Void getBody() {
		return null;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.aasRepo().removeSubmodelReference(id, submodelId);
	}
}
