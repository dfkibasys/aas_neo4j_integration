package org.eclipse.basyx.kafka.connect.neo4j.model.operations;

import org.eclipse.basyx.kafka.connect.neo4j.docker.EnvironmentAccess;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSubmodelOperation extends IntegrationTestOperation<Submodel> {

	private Submodel submodel;
	
	@JsonCreator
	public PostSubmodelOperation(@JsonProperty("body") Submodel submodel) {
		this.submodel = submodel;
	}
	
	@Override
	public Submodel getBody() {
		return submodel;
	}
	
	
	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.submodels().post(submodel);
	}
}