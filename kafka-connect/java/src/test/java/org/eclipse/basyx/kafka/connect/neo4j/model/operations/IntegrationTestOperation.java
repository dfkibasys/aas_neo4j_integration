package org.eclipse.basyx.kafka.connect.neo4j.model.operations;

import org.eclipse.basyx.kafka.connect.neo4j.docker.EnvironmentAccess;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
