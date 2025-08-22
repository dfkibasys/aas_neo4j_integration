package de.dfki.cos.aas2graph.kafka;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.docker.EnvironmentAccess;
import de.dfki.cos.aas2graph.kafka.docker.Neo4jAccess;
import de.dfki.cos.aas2graph.kafka.docker.PluginDockerStack;
import de.dfki.cos.aas2graph.kafka.docker.events.Neo4jKafkaPluginResultResolver;
import de.dfki.cos.aas2graph.kafka.docker.events.ProcessingEvent;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestDefinition;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResult;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResultNode;
import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResultRelationShips;
import de.dfki.cos.aas2graph.kafka.model.operations.IntegrationTestOperation;
import de.dfki.cos.aas2graph.kafka.util.AasIo;
import de.dfki.cos.aas2graph.kafka.util.UnknownComparing;

public class KnowledgeGraphConnectPluginIT {

	private static final PluginDockerStack stack = new PluginDockerStack();

	private static final Neo4jKafkaPluginResultResolver resultResolver = new Neo4jKafkaPluginResultResolver(stack.connect());

	private EnvironmentAccess envAccess;

	private static Neo4jAccess neo4jAccess;

	public static Stream<Arguments> testData() throws IOException, URISyntaxException {
		URI resource = HttpBodyGeneratorTest.class.getClassLoader().getResource("integrationtests").toURI();
		Path testCasesDir = Paths.get(resource);
		return Files.list(testCasesDir).map(KnowledgeGraphConnectPluginIT::toTestCase);
	}

	private static Arguments toTestCase(Path path) {
		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			IntegrationTestDefinition definition = AasIo.yamlMapper().readValue(reader, IntegrationTestDefinition.class);
			String name = path.getFileName().toString();
			return Arguments.of(name, definition);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	public void startContainers() throws InterruptedException {
		stack.start();
		Thread.sleep(1000);	
		envAccess = new EnvironmentAccess(stack.aasEnv().urlAsString());
		neo4jAccess = new Neo4jAccess(stack.neo4j().getTransactionalHttpUrl());
	}
	
	@AfterEach
	public void stopContainers() {
		stack.stop();
	}
		
	@AfterAll
	public static void dispose() throws Exception {
		resultResolver.close();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("testData")
	public void testConnectMappingIsPerformed(String name, IntegrationTestDefinition def) throws Exception {
		postResources(def);
		String result = neo4jAccess.getData();
		IntegrationTestResult resultAsObject = Neo4jResponses.toTestResult(result);
		if (!resultAsObject.getErrors().isEmpty()) {
			Assert.fail(""+resultAsObject.getErrors());
		}
		IntegrationTestResult expected = def.getExpected();
		assertSame(expected, resultAsObject);
	}

	private void assertSame(IntegrationTestResult expected, IntegrationTestResult result) throws SerializationException, JsonProcessingException {
		sortElements(expected);
		sortElements(result);
		UnknownComparing comparing = new UnknownComparing("registrationTime", 0L);
		comparing.compareAndReset(expected.getNodes(), result.getNodes());

		ObjectMapper mapper = AasIo.yamlMapper();

		String expectedStr = mapper.writeValueAsString(expected);
		String resultStr = mapper.writeValueAsString(result);
		Assert.assertEquals(expectedStr, resultStr);
	}

	private void sortElements(IntegrationTestResult result) {
		if (result == null) {
			return;
		}
		result.getErrors().sort(Comparator.naturalOrder());

		result.getNodes().sort(Comparator.comparing(IntegrationTestResultNode::getId));
		result.getRelationships().sort(Comparator.comparing(IntegrationTestResultRelationShips::getId));
	}

	public void postResources(IntegrationTestDefinition def) throws Exception {
		List<IntegrationTestOperation<?>> input = def.getInput();
		for (IntegrationTestOperation<?> eachInputItem : input) {
			eachInputItem.execute(envAccess);
			ProcessingEvent evt = resultResolver.awaitNext();
			Assert.assertTrue(evt.message(), evt.isSuccess());
		}
	}
}