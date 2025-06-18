package de.dfki.cos.aas2graph.kafka.model.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class DeleteSubmodelElementOperation extends IntegrationTestOperation<Void> {

	private final String smId;
	private final String path;

	@JsonCreator
	public DeleteSubmodelElementOperation(@JsonProperty("smId") String smId, @JsonProperty("path") String path) {
		this.smId = smId;
		this.path = path;
	}

	@Override
	public Void getBody() {
		return null;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.smRepo().deleteSubmodelElement(smId, path);
	}
}
