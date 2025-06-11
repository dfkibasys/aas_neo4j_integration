package de.dfki.cos.aas2graph.kafka.docker;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;


public class EnvironmentAccess {
	
	private final ConnectedSubmodelRepository submodelRepo;
	private final ConnectedAasRepository aasRepo;
	
	public EnvironmentAccess(String envUri) {
		this.submodelRepo = new ConnectedSubmodelRepository(envUri);
		this.aasRepo = new ConnectedAasRepository(envUri);		
	}
	
	public ConnectedSubmodelRepository smRepo() {
		return submodelRepo;
	}
	
	public ConnectedAasRepository aasRepo() {
		return aasRepo;
	}

	public void cleanUp() {
		PaginationInfo all = new PaginationInfo(Integer.MAX_VALUE, null);
		for (AssetAdministrationShell shell : aasRepo.getAllAas(all).getResult()) {
			aasRepo.deleteAas(shell.getId());
		}
		for (Submodel sm : submodelRepo.getAllSubmodels(all).getResult()) {
			submodelRepo.deleteSubmodel(sm.getId());
		}
		
	}
}