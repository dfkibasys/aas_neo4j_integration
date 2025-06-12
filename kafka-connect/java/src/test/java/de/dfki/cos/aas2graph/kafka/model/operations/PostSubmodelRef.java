package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PostSubmodelRef extends IntegrationTestOperation<Reference> {

	private final String id;
	private final Reference body;

	@JsonCreator
	public PostSubmodelRef(@JsonProperty("id") String id, @JsonProperty("body") Reference elem) {
		this.id = id;
		this.body = elem;
	}

	@Override
	public Reference getBody() {
		return body;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.aasRepo().addSubmodelReference(id, body);
	}
}
