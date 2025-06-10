package de.dfki.cos.aas2graph.kafka.pebble;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import de.dfki.cos.aas2graph.kafka.pebble.model.SubmodelElementInfo;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class CollectSubmodelTest {

	@Test
	public void testCollectSubmodelElementsFromSubmodel() throws StreamReadException, DatabindException, IOException {
		Submodel sm = AasIo.jsonMapper().readValue("""
			{
				"modelType": "Submodel",
				"id" : "http://sm.example.org/ID1",
				"idShort" : "ID1",
				"submodelElements": 
				[
					{
						"modelType" :  "SubmodelElementCollection",
						"idShort" : "Col",
						"value" : [
							{
								"modelType": "Property",
								"idShort" : "Prop",
								"value": "12"
							}
						]
					}
				]
			}
			""", Submodel.class);
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();
		List<SubmodelElementInfo> infos = func.execute(Map.of(CollectSubmodelElementsFunction.VALUE_ARG, sm), null, null, 0);
		Assert.assertEquals(2, infos.size());
		Map<String, SubmodelElementInfo> infoMap = infos.stream().collect(Collectors.toMap(SubmodelElementInfo::getIdShort, Function.identity()));
		
		SubmodelElementInfo colInfo = infoMap.get("Col");
		Assert.assertTrue(colInfo.getIsRootElement());
		SubmodelElementInfo propInfo = infoMap.get("Prop");
		Assert.assertEquals(List.of(propInfo), colInfo.getChildren());
		Assert.assertFalse(propInfo.getIsRootElement());
		
		Assert.assertNull(colInfo.getValue());
		Assert.assertEquals("12",propInfo.getValue());
		
	}
	
	@Test
	public void testCollectSubmodelElementsUnderRoot() throws StreamReadException, DatabindException, IOException {
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();
		func.execute(null, null, null, 0);
	}
	
	@Test
	public void testCollectSubmodelElementsUnderCollection() throws StreamReadException, DatabindException, IOException {
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();
		func.execute(null, null, null, 0);
	}
	
	@Test
	public void testCollectSubmodelElementsUnderList() throws StreamReadException, DatabindException, IOException {
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();
		func.execute(null, null, null, 0);
	}
	

}
