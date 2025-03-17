package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.SubmodelElementInfo;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;

public class CollectSubmodelElementsTest {

	@Test
	public void testCollectWithList() {		
		CollectSubmodelElementsFunction func = new CollectSubmodelElementsFunction();		
		Map<String, Object> submodelAsTree = constructTestSubmodelTree();		
		Map<String, Object> args = Map.of(CollectSubmodelElementsFunction.SUBMODEL_ARG, submodelAsTree);
		List<SubmodelElementInfo> elems = (List<SubmodelElementInfo>) func.execute(args, null, null, 0);
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

	private Map<String, Object> constructTestSubmodelTree() {
		SubmodelElement p0 = new DefaultProperty.Builder().idShort("p0").value("0").build();
		SubmodelElement p1 = new DefaultProperty.Builder().idShort("p1").value("1").build();
		SubmodelElement p2 = new DefaultProperty.Builder().idShort("p2").value("2").build();
		
		SubmodelElement p3 = new DefaultProperty.Builder().value("3").build();
		SubmodelElement p4 = new DefaultProperty.Builder().value("4").build();
		SubmodelElement p5 = new DefaultProperty.Builder().value("5").build();
		
		SubmodelElement p6 = new DefaultProperty.Builder().value("6").build();
		SubmodelElementList listInner = new DefaultSubmodelElementList.Builder().value(p6).build();
		
		SubmodelElement p7 = new DefaultProperty.Builder().idShort("p7").value("7").build();
		SubmodelElementCollection colInner = new DefaultSubmodelElementCollection.Builder().value(p7).build();
		
		SubmodelElementCollection col = new DefaultSubmodelElementCollection.Builder().idShort("C").value(p0).value(p1).value(p2).build();
		SubmodelElementList list = new DefaultSubmodelElementList.Builder().idShort("L").value(p3).value(p4).value(listInner).value(colInner).value(p5).build();
		
		Submodel sm = new DefaultSubmodel.Builder().id("http://aas.example.com/shell/1").idShort("1")
				.submodelElements(col).submodelElements(list).build();
		SimpleAbstractTypeResolver typeResolver = new SimpleAbstractTypeResolverFactory().create();
		JsonMapper mapper = new JsonMapperFactory().create(typeResolver);
		return mapper.convertValue(sm, new TypeReference<Map<String, Object>>(){});
	}
	

}
