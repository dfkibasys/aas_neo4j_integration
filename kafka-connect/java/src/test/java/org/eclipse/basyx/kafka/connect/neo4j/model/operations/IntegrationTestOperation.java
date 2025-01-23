package org.eclipse.basyx.kafka.connect.neo4j.model.operations;

import java.util.Map;

import org.eclipse.basyx.kafka.connect.neo4j.docker.EnvironmentAccess;
import org.eclipse.basyx.kafka.connect.neo4j.util.SerializationTools;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PostShellOperation.class, name = "POST_SHELL"),
		@JsonSubTypes.Type(value = PostSubmodelOperation.class, name = "POST_SUBMODEL") })
public abstract class IntegrationTestOperation {

	private OperationType type;
	private Map<String, Object> body;

	public OperationType getType() {
		return type;
	}

	public Map<String, Object> getBody() {
		return body;
	}

	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

	public void setType(OperationType type) {
		this.type = type;
	}

	public abstract void execute(EnvironmentAccess access, SerializationTools iotools) throws Exception;

}
