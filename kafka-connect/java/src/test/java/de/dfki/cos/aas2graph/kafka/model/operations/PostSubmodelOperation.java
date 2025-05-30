package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

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