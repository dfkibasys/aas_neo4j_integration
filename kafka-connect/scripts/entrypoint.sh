#!/bin/bash

/etc/confluent/docker/run &

until curl -s http://localhost:8083/connectors; do
  echo "Waiting for Kafka Connect to start..."
  sleep 3
done

# remove /tx/commit to just have the database URL
TARGET_NEO4J_DB_URL=${BASYX_NEO4J_TARGET_URL%/tx/commit}
until curl -s $TARGET_NEO4J_DB_URL; do
  echo "Waiting for Neo4j to start..."
  sleep 3
done

echo "Kafka Connect started, deploying connectors..."

CONNECTOR_NAME="Neo4jSinkConnectorAasEvents"
CONNECTOR_CONFIG_PATH="/etc/kafka-connect/config/neo4j-connector.json"
CONNECTOR_URL="http://localhost:8083/connectors/$CONNECTOR_NAME/config"

if curl -s -o /dev/null -w "%{http_code}" "$CONNECTOR_URL" | grep -q "200"; then
  echo "Updating existing connector: $CONNECTOR_NAME"
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT -H "Content-Type: application/json" --data @"$CONNECTOR_CONFIG_PATH" "$CONNECTOR_URL")
else
  echo "Creating new connector: $CONNECTOR_NAME"
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST -H "Content-Type: application/json" --data @"$CONNECTOR_CONFIG_PATH" http://localhost:8083/connectors)
fi

if [ "$RESPONSE_CODE" -lt 200 ] || [ "$RESPONSE_CODE" -ge 300 ]; then
  echo "Connector deployment failed with status code $RESPONSE_CODE."
  exit 1
else
  echo "Connector deployment successful."
fi

echo "Creating NEO4J Indices..."


JAR_FILE=/tmp/connect-plugins/knowledgegraph-connect-plugin.jar
JAVA_CLASS="org.eclipse.basyx.kafka.connect.neo4j.Neo4jIndicesHttpBodyGenerator"

BODY=$(java -cp "$JAR_FILE" "$JAVA_CLASS")
if [ -z "$BODY" ]; then
  echo "Failed to crate index body for neo4j-kafka-connect plugin."
  exit 1
fi

retries=10
delay=3
success=0

for ((i=1; i<=retries; i++)); do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASYX_NEO4J_TARGET_URL" \
        -H "Content-Type: application/json" \
        -d "$BODY")

    if [ $HTTP_STATUS -eq 200 ]; then
        success=1
        break
    else
        sleep $delay
    fi
done

if [ $success -ne 1 ]; then
echo "Failed to initialize neo4j-kafka-connect plugin."
    exit 1
fi

echo "The neo4j-kafka-connect plugin is ready to use. 🎉"


# block to keep async kafka-connect process running
wait

