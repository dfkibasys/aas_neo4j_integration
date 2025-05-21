package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.transforms.Transformation;
import org.eclipse.basyx.kafka.connect.neo4j.events.Event;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.HttpRequestBodyGenerator;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.PebbleContext;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.PebbleContextMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CypherTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

	private static final Logger LOG = LoggerFactory.getLogger(CypherTransformation.class);

	private final HttpRequestBodyGenerator generator = new HttpRequestBodyGenerator();

	private final YamlJsonConverter converter = new YamlJsonConverter();

	@Override
	public R apply(R record) {
		LOG.info("Transform record. ");
		PebbleContext context = context(record);
		try {
			LOG.info(context.toString());
			String bodyYaml = generator.generateYamlFromMessage(context);
			LOG.info(bodyYaml);
			String requestBody = converter.yamlToJson(bodyYaml);
			LOG.error(requestBody.toString());
			return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), Schema.STRING_SCHEMA, requestBody, record.timestamp());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private PebbleContext context(R record) {
		Map<String, Object> eventMap = (Map<String, Object>) record.value();

		PebbleContextMeta meta = metaData(record);
		Event event = AasIo.jsonMapper().convertValue(eventMap, Event.class);
		return new PebbleContext(event, meta);
	}

	private PebbleContextMeta metaData(R record) {
		PebbleContextMeta meta = new PebbleContextMeta();
		meta.setSourceUrl(System.getenv("BASYX_AAS_REPO_URL"));
		meta.setRegistrationTime(record.timestamp());
		meta.setTopic(record.topic());
		return meta;
	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public ConfigDef config() {
		return new ConfigDef();
	}

}