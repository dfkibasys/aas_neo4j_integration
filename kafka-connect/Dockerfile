FROM --platform=$BUILDPLATFORM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY ./java/pom.xml /app/pom.xml
RUN mvn dependency:go-offline
COPY ./java/templates/ /app/templates/
COPY ./java/src/ /app/src/
RUN mvn clean install -DskipTests

FROM confluentinc/cp-kafka-connect:7.9.1
COPY --from=build /app/target/knowledgegraph-connect-plugin-*.jar /tmp/connect-plugins/knowledgegraph-connect-plugin.jar
COPY ./java/templates /home/appuser/templates
COPY ./config/http-connector.json /etc/kafka-connect/config/neo4j-connector.json
ENV CONNECT_PLUGIN_PATH "/usr/share/java,/usr/share/confluent-hub-components,/tmp/connect-plugins"
USER root
COPY scripts/entrypoint.sh /etc/basyx/entrypoint.sh
RUN chmod +x /etc/basyx/entrypoint.sh
USER appuser

ENV CONNECT_GROUP_ID "neo4j-connector"

ENV CONNECT_CONFIG_STORAGE_TOPIC "neo4j-connector-config"
ENV CONNECT_OFFSET_STORAGE_TOPIC "neo4j-connector-offsets"
ENV CONNECT_STATUS_STORAGE_TOPIC "neo4j-connector-status"
ENV CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR 1
ENV CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR 1
ENV CONNECT_STATUS_STORAGE_REPLICATION_FACTOR 1
ENV CONNECT_REST_HEALTH_ENABLED "true"
ENV CONNECT_REST_PORT 8083
ENV BASYX_TEMPLATE_LOCATION /home/appuser/templates
ENV BASYX_CREATE_INDEX_PATH /etc/basyx/create-index-body.json

ENV CONNECT_KEY_CONVERTER "org.apache.kafka.connect.storage.StringConverter" 
ENV CONNECT_VALUE_CONVERTER "org.apache.kafka.connect.json.JsonConverter" 
ENV CONNECT_INTERNAL_KEY_CONVERTER "org.apache.kafka.connect.storage.StringConverter" 
ENV CONNECT_INTERNAL_VALUE_CONVERTER "org.apache.kafka.connect.json.JsonConverter" 

HEALTHCHECK --start-period=5m --interval=1m --timeout=10s --retries=5 \
  CMD nc -z localhost 8083 || exit 1

CMD ["/etc/basyx/entrypoint.sh"]