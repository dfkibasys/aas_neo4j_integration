package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PostSubmodelElementOperation extends IntegrationTestOperation<SubmodelElement> {

	private final String smId;
	private final String path;
	private final SubmodelElement body;

	@JsonCreator
	public PostSubmodelElementOperation(@JsonProperty("smId") String smId, @JsonProperty("path") String path, @JsonProperty("Body") SubmodelElement elem) {
		this.smId = smId;
		this.path = path;
		this.body = elem;
	}

	@Override
	public SubmodelElement getBody() {
		return body;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		if (path == null) {
			access.smRepo().createSubmodelElement(smId, body);
		} else {
			access.smRepo().createSubmodelElement(smId, path, body);
		}
	}
}
