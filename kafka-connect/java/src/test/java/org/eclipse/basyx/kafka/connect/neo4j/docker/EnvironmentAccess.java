package org.eclipse.basyx.kafka.connect.neo4j.docker;

import org.eclipse.basyx.kafka.connect.neo4j.util.SerializationTools;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;


public class EnvironmentAccess {
	
	private final RepositoryAccess<AssetAdministrationShell> shellRepoAccess;
	private final RepositoryAccess<Submodel> smRepoAccess;
	
	public EnvironmentAccess(SerializationTools ioTools, String envUri) {
		String shellUri = envUri + "/shells"; 
		shellRepoAccess = new RepositoryAccess<>(ioTools, AssetAdministrationShell.class, shellUri);
		String smUri = envUri + "/submodels";
		smRepoAccess = new RepositoryAccess<>(ioTools, Submodel.class, smUri);		
	}
	
	public RepositoryAccess<AssetAdministrationShell> shells() {
		return shellRepoAccess;
	}

	public RepositoryAccess<Submodel> submodels() {
		return smRepoAccess;
	}	
}