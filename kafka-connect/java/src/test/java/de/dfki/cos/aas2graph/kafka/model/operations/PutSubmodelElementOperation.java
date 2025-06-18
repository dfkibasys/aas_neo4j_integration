package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PutSubmodelElementOperation extends IntegrationTestOperation<SubmodelElement> {

	private final String smId;
	private final String path;
	private final SubmodelElement body;

	@JsonCreator
	public PutSubmodelElementOperation(@JsonProperty("smId") String smId, @JsonProperty("path") String path, @JsonProperty("body") SubmodelElement body) {
		this.smId = smId;
		this.path = path;
		this.body = body;
	}

	@Override
	public SubmodelElement getBody() {
		return body;
	}

	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.smRepo().updateSubmodelElement(smId, path, body);
	}
}
