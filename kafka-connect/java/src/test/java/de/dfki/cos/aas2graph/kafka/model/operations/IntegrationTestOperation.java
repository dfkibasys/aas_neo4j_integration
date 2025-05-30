package de.dfki.cos.aas2graph.kafka.model.operations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PostShellOperation.class, name = "POST_SHELL"),
		@JsonSubTypes.Type(value = PostSubmodelOperation.class, name = "POST_SUBMODEL") })
public abstract class IntegrationTestOperation<T> {

	private OperationType type;

	public OperationType getType() {
		return type;
	}

	public abstract T getBody();

	public void setType(OperationType type) {
		this.type = type;
	}

	public abstract void execute(EnvironmentAccess access) throws Exception;

}
