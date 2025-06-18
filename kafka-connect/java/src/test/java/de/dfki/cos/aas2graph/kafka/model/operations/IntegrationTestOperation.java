package de.dfki.cos.aas2graph.kafka.model.operations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PostShellOperation.class, name =  "POST_SHELL"),
		@JsonSubTypes.Type(value = PostSubmodelOperation.class, name = "POST_SUBMODEL"),
		@JsonSubTypes.Type(value = PutAssetInformationOperation.class, name = "PUT_ASSET_INFORMATION"),
		@JsonSubTypes.Type(value = PostSubmodelElementOperation.class, name = "POST_SUBMODEL_ELEMENT"),
		@JsonSubTypes.Type(value = PostSubmodelRefOperation.class, name = "POST_SUBMODEL_REF"),
		@JsonSubTypes.Type(value = DeleteSubmodelRefOperation.class, name="DELETE_SUBMODEL_REF"),
		@JsonSubTypes.Type(value = DeleteShellOperation.class, name="DELETE_SHELL"),
		@JsonSubTypes.Type(value = DeleteSubmodelOperation.class, name="DELETE_SUBMODEL"),
		@JsonSubTypes.Type(value = PutShellOperation.class, name="PUT_SHELL"),
		@JsonSubTypes.Type(value = PutSubmodelOperation.class, name="PUT_SUBMODEL"),
		@JsonSubTypes.Type(value = DeleteSubmodelElementOperation.class, name="DELETE_SUBMODEL_ELEMENT"),
		@JsonSubTypes.Type(value = PutSubmodelElementOperation.class, name="PUT_SUBMODEL_ELEMENT")

})
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
