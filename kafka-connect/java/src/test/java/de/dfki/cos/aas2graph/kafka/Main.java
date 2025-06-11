package de.dfki.cos.aas2graph.kafka;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;
import de.dfki.cos.aas2graph.kafka.model.operations.PostShellOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostSubmodelOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PutAssetInformationOperation;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Main {

	private static final String SM_ID = "http://sm.dfki.de/mir_100/1/test";
	private static final String ASSET_ID = "http://asset.dfki.de/mir_100/1";
	private static final String AAS_IDSHORT = "MiR100_1";
	private static final String SM_IDSHORT = "Test";
	private static final String AAS_ID = "http://aas.dfki.de/mir_100/1";

	public static void main(String[] args) throws Exception {
		EnvironmentAccess access = new EnvironmentAccess("http://localhost:8081");

		AssetAdministrationShell shell = newTestShell();
		new PostShellOperation(shell).execute(access);
		Submodel sm = newTestSubmodel();
		new PostSubmodelOperation(sm).execute(access);
//		AssetInformation info = newTestAssetInfo();
//		new PutAssetInformationOperation(shell.getId(), info).execute(access);

	}

	private static Submodel newTestSubmodel() throws JsonMappingException, JsonProcessingException {
		String json = """
				{
					"modelType" : "Submodel",
					"id" : "http://sm.dfki.de/mir_100/1/test",
					"idShort" : "Sm1",
					"submodelElements" : [
						{
							"modelType" : "SubmodelElementCollection",
							"idShort" : "Col",
							"value" : [
								{
									"modelType" : "Property",
									"idShort" : "Prop1",
									"value" : "1"
								}
							]
						}
					]
				}
				""";

		return AasIo.jsonMapper().readValue(json, Submodel.class);
	}

	private static AssetInformation newTestAssetInfo() throws JsonMappingException, JsonProcessingException {
		String json = """
			{
				"assetType" : "Robot",
				"assetKind" : "Type",
				"globalAssetId" : "http://asset.dfki.de/mir_100"
			}
			""";
		return AasIo.jsonMapper().readValue(json, AssetInformation.class);

	}

	private static AssetAdministrationShell newTestShell() throws JsonMappingException, JsonProcessingException {
		String json = """
				{
					"modelType" : "Submodel",
					"id" : "http://aas.dfki.de/mir_100/1",
					"idShort" : "MiR100_1",
					"assetInformation" : {
						"assetType" : "Robot",
						"assetKind" : "Instance",
						"globalAssetId" : "http://asset.dfki.de/mir_100/1"
					},
					"submodels" : [
						{
							"type": "ModelReference",
							"keys" : [
								{
									"type" : "Submodel",
									"value" : "http://sm.dfki.de/mir_100/1/test"
								}
							]
						}
					]
				}
				""";
		return AasIo.jsonMapper().readValue(json, AssetAdministrationShell.class);
	}

}
