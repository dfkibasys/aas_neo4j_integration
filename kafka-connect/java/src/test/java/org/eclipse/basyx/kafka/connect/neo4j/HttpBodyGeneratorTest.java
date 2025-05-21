package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.HttpRequestBodyGenerator;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.PebbleContext;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpBodyGeneratorTest {


	public static Stream<Arguments> testData() throws IOException, URISyntaxException {
		URI resource = HttpBodyGeneratorTest.class.getClassLoader().getResource("testcases").toURI();
		Path testCasesDir = Paths.get(resource);
		return Files.list(testCasesDir).map(HttpBodyGeneratorTest::toTestCase);
	}

	private static Arguments toTestCase(Path path) {
		ObjectMapper yamlMapper = AasIo.yamlMapper();
		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			TestDefinition definition = yamlMapper.readValue(reader, TestDefinition.class);
			String name = path.getFileName().toString();
			return Arguments.of(name, definition, yamlMapper);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("testData")
	public void testYamlGeneration(String name, TestDefinition def, ObjectMapper yamlMapper) throws IOException {
		PebbleContext context = def.getInput();
		HttpRequestBodyGenerator generator = new HttpRequestBodyGenerator();
		String yaml = generator.generateYamlFromMessage(context);
		String expected = def.getExpected();
		assertEquals(expected, yaml, yamlMapper);
	}

	private void assertEquals(String expectedYaml, String resultYaml, ObjectMapper yamlMapper)
			throws JsonMappingException, JsonProcessingException {
		resultYaml = normalizeYaml(resultYaml);
		expectedYaml = normalizeYaml(expectedYaml);
		Assert.assertEquals(expectedYaml, resultYaml);
	}

	private String normalizeYaml(String expected) {
		String normalized = expected.replaceAll("\r\n|\r", "\n");
		StringBuilder normalizedBuilder = new StringBuilder();
		String[] lines = normalized.split("\n");
		for (String line : lines) {
			String trimmedLine = line.replaceAll("\\s+$", "");
			if (!"".equals(trimmedLine)) {
				normalizedBuilder.append(trimmedLine).append("\n");
			}
		}
		if (normalizedBuilder.length() > 0) {
			normalizedBuilder.setLength(normalizedBuilder.length() - 1);
		}
		return normalizedBuilder.toString();
	}

	private static final class TestDefinition {

		private PebbleContext input;

		private String expected;

		public PebbleContext getInput() {
			return input;
		}

		@SuppressWarnings("unused")
		public void setInput(PebbleContext input) {
			this.input = input;
		}

		public String getExpected() {
			return expected;
		}

		@SuppressWarnings("unused")
		public void setExpected(String expected) {
			this.expected = expected;
		}
	}
}
