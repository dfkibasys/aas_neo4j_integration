package de.dfki.cos.aas2graph.kafka.model.operations;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;

public class PostShellOperation extends IntegrationTestOperation<AssetAdministrationShell> {

	private AssetAdministrationShell shell;
	
	@JsonCreator
	public PostShellOperation(@JsonProperty("body") AssetAdministrationShell shell) {
		this.shell = shell;
	}
	
	@Override
	public AssetAdministrationShell getBody() {
		return shell;
	}
	
	@Override
	public void execute(EnvironmentAccess access) throws Exception {
		access.shells().post(shell);
	}
}
