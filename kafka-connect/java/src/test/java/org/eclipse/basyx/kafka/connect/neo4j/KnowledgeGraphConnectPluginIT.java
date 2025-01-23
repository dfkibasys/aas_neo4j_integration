package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.basyx.kafka.connect.neo4j.docker.EnvironmentAccess;
import org.eclipse.basyx.kafka.connect.neo4j.docker.Neo4jAccess;
import org.eclipse.basyx.kafka.connect.neo4j.docker.PluginDockerStack;
import org.eclipse.basyx.kafka.connect.neo4j.docker.events.Neo4jKafkaPluginResultResolver;
import org.eclipse.basyx.kafka.connect.neo4j.docker.events.ProcessingEvent;
import org.eclipse.basyx.kafka.connect.neo4j.model.IntegrationTestDefinition;
import org.eclipse.basyx.kafka.connect.neo4j.model.IntegrationTestResult;
import org.eclipse.basyx.kafka.connect.neo4j.model.IntegrationTestResultNode;
import org.eclipse.basyx.kafka.connect.neo4j.model.IntegrationTestResultRelationShips;
import org.eclipse.basyx.kafka.connect.neo4j.model.operations.IntegrationTestOperation;
import org.eclipse.basyx.kafka.connect.neo4j.util.SerializationTools;
import org.eclipse.basyx.kafka.connect.neo4j.util.UnknownComparing;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class KnowledgeGraphConnectPluginIT {

	private static final SerializationTools ioTools = new SerializationTools();

	private static final PluginDockerStack stack = new PluginDockerStack();

	private static final Neo4jKafkaPluginResultResolver resultResolver = new Neo4jKafkaPluginResultResolver(
			stack.connect());

	private static EnvironmentAccess envAccess;

	private static Neo4jAccess neo4jAccess;

	public static Stream<Arguments> testData() throws IOException, URISyntaxException {
		URI resource = HttpBodyGeneratorTest.class.getClassLoader().getResource("integrationtests").toURI();
		Path testCasesDir = Paths.get(resource);
		return Files.list(testCasesDir).map(KnowledgeGraphConnectPluginIT::toTestCase);
	}

	private static Arguments toTestCase(Path path) {
		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			IntegrationTestDefinition definition = ioTools.yamlMapper().readValue(reader,
					IntegrationTestDefinition.class);
			String name = path.getFileName().toString();
			return Arguments.of(name, definition);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@BeforeAll
	public static void startContainers() {
		stack.start();
		envAccess = new EnvironmentAccess(ioTools, stack.aasEnv().urlAsString());
		neo4jAccess = new Neo4jAccess(stack.neo4j().getTransactionalHttpUrl());
		resultResolver.start(stack.kafka().externalBrokerAddress());
	}

	@AfterAll
	public static void stopContainers() throws Exception {
		stack.stop();
		resultResolver.close();
	}

	@AfterEach
	@BeforeEach
	private void cleanUp() throws IOException, InterruptedException, DeserializationException {
		envAccess.shells().cleanUp();
		envAccess.submodels().cleanUp();
		neo4jAccess.cleanUp();
		resultResolver.clear();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("testData")
	public void testConnectMappingIsPerformed(String name, IntegrationTestDefinition def) throws Exception {
		postResources(def);
		String result = neo4jAccess.getData();
		IntegrationTestResult resultAsObject = mapToObject(result);
		IntegrationTestResult expected = def.getExpected();
		assertSame(expected, resultAsObject);
	}



	private void assertSame(IntegrationTestResult expected, IntegrationTestResult result)
			throws SerializationException, JsonProcessingException {
		sortElements(expected);
		sortElements(result);
		UnknownComparing comparing = new UnknownComparing("registrationTime", 0L);
		comparing.compareAndReset(expected.getNodes(), result.getNodes());
		
		ObjectMapper mapper = ioTools.yamlMapper();
		
		String expectedStr = mapper.writeValueAsString(expected);
		String resultStr = mapper.writeValueAsString(result);
		Assert.assertEquals(expectedStr, resultStr);
	}

	private void sortElements(IntegrationTestResult result) {
		result.getErrors().sort(null);
		result.getNodes().sort(Comparator.comparing(IntegrationTestResultNode::getId));
		result.getRelationships().sort(Comparator.comparing(IntegrationTestResultRelationShips::getId));
	}

	public void postResources(IntegrationTestDefinition def) throws Exception {
		List<IntegrationTestOperation> input = def.getInput();
		for (IntegrationTestOperation eachInputItem : input) {
			eachInputItem.execute(envAccess, ioTools);
			ProcessingEvent evt = resultResolver.awaitNext();
			Assert.assertTrue(evt.message(), evt.isSuccess());
		}
	}

	@SuppressWarnings("unchecked")
	private IntegrationTestResult mapToObject(String result) throws IOException, DeserializationException {
		PebbleEngine engine = new PebbleEngine.Builder().loader(new ClasspathLoader()).build();
		PebbleTemplate compiledTemplate = engine.getTemplate("testresult-mapping.peb");
		StringWriter writer = new StringWriter();
		JsonDeserializer deserializer = ioTools.jsonDeserializer();
		Map<String, Object> resultAsMap = deserializer.read(result, Map.class);
		compiledTemplate.evaluate(writer, resultAsMap);
		return deserializer.read(writer.toString(), IntegrationTestResult.class);
	}
}