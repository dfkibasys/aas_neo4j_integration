package de.dfki.cos.aas2graph.kafka;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Main {

	private static final String SM_ID = "http://sm.dfki.de/mir_100/1/test";
	private static final String ASSET_ID = "http://asset.dfki.de/mir_100/1";
	private static final String AAS_IDSHORT = "MiR100_1";
	private static final String SM_IDSHORT = "Test";
	private static final String AAS_ID = "http://aas.dfki.de/mir_100/1";

	public static void main(String[] args) throws Exception {

	
		EnvironmentAccess access = new EnvironmentAccess("http://localhost:8081");
		String yamlSubmodel = """
id: http://test.sm
modelType: Submodel
kind : Instance
submodelElements: 
  - modelType: SubmodelElementCollection
    idShort: Col
    value:
    - modelType: Property
      idShort: Prop1
      value: "1"
    - modelType: ReferenceElement
      idShort: RE
      value: 
        type: ModelReference
        keys: 
        - type: Submodel
          value: http://test.sm
        - type: SubmodelElementCollection
          value: Col
  - modelType: SubmodelElementList
    idShort: List
    value:
    - modelType: Property
      idShort: Prop1
      value: "1"
		
				""";
		Submodel sm = AasIo.yamlMapper().readValue(yamlSubmodel, Submodel.class);
		
		access.smRepo().createSubmodel(sm);
		access.smRepo().deleteSubmodelElement(sm.getId(), "Col.RE");
		access.smRepo().deleteSubmodelElement(sm.getId(), "List");
		
		
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
					"modelType" : "AssetAdministrationShell",
					"id" : "http://test4.shell",
					"idShort" : "AAS2_Test",
					"assetInformation" : {
					  "globalAssetId" : "http://asset.test",
					  "assetType" : "abc"
					},
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
