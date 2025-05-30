package de.dfki.cos.aas2graph.kafka.pebble;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.pebble.model.SubmodelElementInfo;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class CollectSubmodelElementsTest {

	@Test
	public void testCollectWithList() throws JsonMappingException, JsonProcessingException {		
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();		
		Submodel submodel = constructTestSubmodel();		
		List<SubmodelElementInfo> elems = func.execute(Map.of("sm", submodel), null, null, 0);
		Assertions.assertEquals(12, elems.size());
		Set<String> idPaths = elems.stream().map(SubmodelElementInfo::getIdShortPath).collect(Collectors.toSet());
		Assertions.assertTrue(idPaths.contains("C"));
		Assertions.assertTrue(idPaths.contains("L"));
		Assertions.assertTrue(idPaths.contains("C.p0"));
		Assertions.assertTrue(idPaths.contains("C.p1"));
		Assertions.assertTrue(idPaths.contains("C.p2"));
		Assertions.assertTrue(idPaths.contains("L[0]"));
		Assertions.assertTrue(idPaths.contains("L[1]"));
		Assertions.assertTrue(idPaths.contains("L[2]"));
		Assertions.assertTrue(idPaths.contains("L[3]"));
		Assertions.assertTrue(idPaths.contains("L[4]"));
		Assertions.assertTrue(idPaths.contains("L[2][0]"));
		Assertions.assertTrue(idPaths.contains("L[3].p7"));
		
		for (SubmodelElementInfo eachInfo : elems) {
			System.out.println(eachInfo.getRefs());
		}
		
	}

	private Submodel constructTestSubmodel() throws JsonMappingException, JsonProcessingException {
		String content = """
			{			
				"modelType" : "Submodel",
				"id": "http://aas.example.com/shell/1",
				"idShort": "1",
				"submodelElements" : [
					{
						"modelType" : "SubmodelElementCollection",
						"idShort": "C",
						"value" : [
							{
								"modelType" : "Property",
								"idShort" : "p0",
								"value" : "0"
							}, 
							{
								"modelType" : "Property",
								"idShort" : "p1",
								"value" : "1"
							}, 
							{
								"modelType" : "Property",
								"idShort" : "p2",
								"value" : "2"
							}
						]
					},
					{
						"modelType" : "SubmodelElementList",
						"idShort": "L",
						"value" : [
							{
								"modelType" : "Property",
								"value" : "3"
							}, 
							{
								"modelType" : "Property",
								"value" : "4"
							},
							{
								"modelType" : "SubmodelElementList",
								"value" : [
									{
										"modelType" : "Property",
										"value" : "6"
									}
								]
							},
							{
								"modelType" : "SubmodelElementCollection",
								"value" : [
									{
										"modelType" : "Property",
										"idShort" : "p7",
										"value" : "7"
									}
								]
							},
							{
								"modelType" : "Property",
								"value" : "5"
							}
							
						]
					}
				]
			}
				""";
		ObjectMapper mapper = AasIo.jsonMapper();
		return  mapper.readValue(content, Submodel.class);
	}
	

}
