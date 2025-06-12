package de.dfki.cos.aas2graph.kafka;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.util.AasUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultExtension;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;
import de.dfki.cos.aas2graph.kafka.model.operations.DeleteShellOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostShellOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostSubmodelOperation;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleAssetAdministrationShell;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Main {

	private static final String SM_ID = "http://sm.dfki.de/mir_100/1/test";
	private static final String ASSET_ID = "http://asset.dfki.de/mir_100/1";
	private static final String AAS_IDSHORT = "MiR100_1";
	private static final String SM_IDSHORT = "Test";
	private static final String AAS_ID = "http://aas.dfki.de/mir_100/1";

	public static void main(String[] args) throws Exception {

		EnvironmentAccess access = new EnvironmentAccess("http://localhost:8081");
		//
		Submodel sm = new DefaultSubmodel.Builder().id("http://test.sm").kind(ModellingKind.INSTANCE).submodelElements(new DefaultProperty.Builder().idShort("P").value("5").build()).build();
	//	new PostSubmodelOperation(sm).execute(access);
		AssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id("http://test.shell").assetInformation(new DefaultAssetInformation.Builder().globalAssetId("http://test.asset").build()).build();
	//	new PostShellOperation(shell).execute(access);
		//
		Reference ref = new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE).keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(sm.getId()).build())
				.keys(new DefaultKey.Builder().type(KeyTypes.PROPERTY).value("P").build()).build();
		AssetAdministrationShell derived = new DefaultAssetAdministrationShell.Builder().id("http://derived.shell")
				.assetInformation(new DefaultAssetInformation.Builder().globalAssetId("http://derived.asset").assetKind(AssetKind.INSTANCE).assetType("test").build())
				.derivedFrom(AasUtils.toReference(shell))
				.extensions(new DefaultExtension.Builder().name("test").value("test").refersTo(ref).build()).build();
		
		PebbleAssetAdministrationShell tmp = (PebbleAssetAdministrationShell) AasIo.jsonMapper().readValue(AasIo.jsonMapper().writeValueAsBytes(derived), AssetAdministrationShell.class);
		System.out.println(tmp.getRefs());
	//	System.out.println(AasIo.jsonMapper().writeValueAsString(tmp));
		new PostShellOperation(derived).execute(access);

		// new DeleteShellOperation(derived.getId()).execute(access);

	}

	private static SubmodelElement newTestSubmodelElement(String id, String value) {
		return new DefaultProperty.Builder().idShort(id).value(value).build();
	}

	private static Submodel newTestSubmodel() throws JsonMappingException, JsonProcessingException {
		String json = """
				{
					"modelType" : "Submodel",
					"id" : "http://test.sm",
					"idShort" : "SM1",
					"submodelElements" : [
						{
							"modelType" : "SubmodelElementCollection",
							"idShort" : "Col1"
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
					"id" : "http://test.shell",
					"idShort" : "AAS_Test",
					"submodels" : [
						{
							"type": "ModelReference",
							"keys" : [
								{
									"type" : "Submodel",
									"value" : "http://test.sm"
								}
							]
						}
					]
				}
				""";

		// "assetInformation" : {
		// "assetType" : "Robot",
		// "assetKind" : "Instance",
		// "globalAssetId" : "http://asset.dfki.de/mir_100/1"
		// },
		return AasIo.jsonMapper().readValue(json, AssetAdministrationShell.class);
	}

}
