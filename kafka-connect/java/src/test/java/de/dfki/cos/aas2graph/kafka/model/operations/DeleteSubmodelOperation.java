package de.dfki.cos.aas2graph.kafka.model.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class DeleteSubmodelOperation extends IntegrationTestOperation<Void> {

	private final String id;

	@JsonCreator
	public DeleteSubmodelOperation(@JsonProperty("id") String id) {
		this.id = id;
	}

	@Override
	public Void getBody() {
		return null;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.smRepo().deleteSubmodel(id);
	}
}