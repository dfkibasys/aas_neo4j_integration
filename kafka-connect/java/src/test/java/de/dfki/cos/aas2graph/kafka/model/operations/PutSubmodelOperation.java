package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PutSubmodelOperation extends IntegrationTestOperation<Submodel> {

	private final String id;
	private final Submodel body;

	@JsonCreator
	public PutSubmodelOperation(@JsonProperty("id") String id, @JsonProperty("body") Submodel body) {
		this.id = id;
		this.body = body;
	}

	@Override
	public Submodel getBody() {
		return body;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.smRepo().updateSubmodel(id, body);
	}
}
