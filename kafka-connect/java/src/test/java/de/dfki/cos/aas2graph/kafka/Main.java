package de.dfki.cos.aas2graph.kafka;

import java.util.List;

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
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;
import de.dfki.cos.aas2graph.kafka.model.operations.DeleteSubmodelRef;
import de.dfki.cos.aas2graph.kafka.model.operations.PostShellOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostSubmodelElementOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostSubmodelOperation;
import de.dfki.cos.aas2graph.kafka.model.operations.PostSubmodelRef;
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
		Submodel sm = new DefaultSubmodel.Builder().id("http://test.sm").kind(ModellingKind.INSTANCE).build();
		new PostSubmodelOperation(sm).execute(access);
		AssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id("http://test.shell").submodels(AasUtils.toReference(sm)).build();
		new PostShellOperation(shell).execute(access);
		new DeleteSubmodelRef(shell.getId(), sm.getId()).execute(access);
		
		
//
//		Submodel sm = newTestSubmodel();
	//	new PostSubmodelOperation(sm).execute(access);
	//	AssetAdministrationShell shell = newTestShell();
//		new PostShellOperation(shell).execute(access);
//		Reference ref = new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE)
//				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value("http://sm.addesref.de").build()).build();
//		new PostSubmodelRef(shell.getId(), ref).execute(access);
//		new DeleteSubmodelRef(shell.getId(), 	"http://sm.addesref.de").execute(access);;
//		AssetInformation info = newTestAssetInfo();
//		new PutAssetInformationOperation(shell.getId(), info).execute(access);
//		SubmodelElement elem = newTestSubmodelElement("Prop1", "1");
//		new PostSubmodelElementOperation(sm.getId(), "Col1", elem).execute(access);
//		elem = newTestSubmodelElement("Prop2", "2");
//		new PostSubmodelElementOperation(sm.getId(), null, elem).execute(access);
		
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

//		"assetInformation" : {
//			"assetType" : "Robot",
//			"assetKind" : "Instance",
//			"globalAssetId" : "http://asset.dfki.de/mir_100/1"
//		},
		return AasIo.jsonMapper().readValue(json, AssetAdministrationShell.class);
	}

}
