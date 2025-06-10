package de.dfki.cos.aas2graph.kafka.pebble;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.pebble.model.SubmodelElementInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElementCollection;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class CollectSubmodelElementsTest {

	@Test
	public void testCollectWithList() throws JsonMappingException, JsonProcessingException {		
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();		
		Submodel submodel = constructTestSubmodel();		
		List<SubmodelElementInfo> elems = func.execute(Map.of(CollectSubmodelElementsFunction.VALUE_ARG, submodel), null, null, 0);
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
		
	}
	
	@Test
	public void testCollectSubmodelElementsUnderRoot() throws StreamReadException, DatabindException, IOException {
		Submodel submodel = constructTestSubmodel();		
		
		SubmodelElement elemList = submodel.getSubmodelElements().get(1);
		
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();
		List<SubmodelElementInfo> elems = func.execute(Map.of(CollectSubmodelElementsFunction.VALUE_ARG, elemList,
				CollectSubmodelElementsFunction.ID_ARG, submodel.getId(),
				CollectSubmodelElementsFunction.IDSHORTPATH_ARG, "L"
				
				), null, null, 0);
		Assertions.assertEquals(8, elems.size());
		Set<String> idPaths = elems.stream().map(SubmodelElementInfo::getIdShortPath).collect(Collectors.toSet());
		System.out.println(idPaths);
		Assertions.assertTrue(idPaths.contains("L"));
		Assertions.assertTrue(idPaths.contains("L[0]"));
		Assertions.assertTrue(idPaths.contains("L[1]"));
		Assertions.assertTrue(idPaths.contains("L[2]"));
		Assertions.assertTrue(idPaths.contains("L[3]"));
		Assertions.assertTrue(idPaths.contains("L[4]"));
		Assertions.assertTrue(idPaths.contains("L[2][0]"));
		Assertions.assertTrue(idPaths.contains("L[3].p7"));
	}
	
	
	
	@Test
	public void testCollectSubmodelElementsUnderCollection() throws StreamReadException, DatabindException, IOException {
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();		
		
		PebbleSubmodelElementCollection toUpdate = toUpate();
		List<SubmodelElementInfo> elems = func.execute(Map.of(CollectSubmodelElementsFunction.VALUE_ARG, toUpdate, 
				CollectSubmodelElementsFunction.ID_ARG, "http://aas.example.com/shell/1",
				CollectSubmodelElementsFunction.IDSHORTPATH_ARG, "C.C2"), null, null, 0);
		Assertions.assertEquals(3, elems.size());
		
		Set<String> idPaths = elems.stream().map(SubmodelElementInfo::getIdShortPath).collect(Collectors.toSet());
		System.out.println(idPaths);
		Assertions.assertTrue(idPaths.contains("C.C2"));
		Assertions.assertTrue(idPaths.contains("C.C2.Prop1"));
		Assertions.assertTrue(idPaths.contains("C.C2.Prop2"));
		SubmodelElementInfo c = elems.stream().filter(e->e.getIdShort().equals(toUpdate.getIdShort())).findFirst().get();
		Assert.assertNotNull(c.getParent());
		Assert.assertEquals("C2", c.getIdShort());
		Assert.assertEquals("C.C2", c.getIdShortPath());
		Assert.assertEquals("C", c.getParent().getIdShortPath());
	}
	
	private PebbleSubmodelElementCollection toUpate() throws JsonMappingException, JsonProcessingException {
		return AasIo.jsonMapper().readValue("""
				{
					"modelType" : "SubmodelElementCollection",
					"idShort" : "C2",
					"value": [
						{
							"modelType": "Property",
							"idShort": "Prop1",
							"value" : "1"
						},
						{
							"modelType": "Property",
							"idShort": "Prop2",
							"value" : "2"
						}
					]
				}
				""", PebbleSubmodelElementCollection.class);
	}

	@Test
	public void testCollectSubmodelElementsUnderList() throws StreamReadException, DatabindException, IOException {
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();		
		
		PebbleSubmodelElementCollection collection = toUpate();
		collection.setIdShort(null); // no idshort under list 

		List<SubmodelElementInfo> elems = func.execute(Map.of(CollectSubmodelElementsFunction.VALUE_ARG, collection, 
				CollectSubmodelElementsFunction.ID_ARG, "http://aas.example.com/shell/1",
				CollectSubmodelElementsFunction.IDSHORTPATH_ARG, "L[3]"), null, null, 0);
		Assertions.assertEquals(3, elems.size());
		
		Set<String> idPaths = elems.stream().map(SubmodelElementInfo::getIdShortPath).collect(Collectors.toSet());
		System.out.println(idPaths);
		Assertions.assertTrue(idPaths.contains("L[3]"));
		Assertions.assertTrue(idPaths.contains("L[3].Prop1"));
		Assertions.assertTrue(idPaths.contains("L[3].Prop2"));
		SubmodelElementInfo c = elems.stream().filter(e->e.getIdShortPath().equals("L[3]")).findFirst().get();
		Assert.assertNotNull(c.getParent());
		Assert.assertNull(c.getIdShort());
		Assert.assertEquals("L[3]", c.getIdShortPath());
		Assert.assertEquals("L", c.getParent().getIdShortPath());
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
