#!/bin/bash

/etc/confluent/docker/run &


echo "Creating NEO4J Indices..."

query='{
  "statements": [
    { "statement": "MATCH (n) RETURN n LIMIT 1" }
  ]
}'

check_neo4j() {
  local response
  response=$(curl -s -X POST "$BASYX_NEO4J_TARGET_URL" \
    -H "Content-Type: application/json" -d "$query")

  if echo "$response" | grep -q '"errors":\s*\[\s*\]'; then
    echo "Neo4j query succeeded."
    return 0
  else
    echo "Neo4j query failed. Response:"
    echo "$response"
    return 1
  fi
}

until check_neo4j; do
  echo "Waiting for Neo4j to be ready..."
  sleep 3
done

echo "Neo4j is ready to accept queries!"

create_and_post() {
  local jar_file="$1"
  local java_class="$2"
  local target_url="$3"
  local retries="${4:-10}"
  local delay="${5:-3}"
  local success=0

  # Generate the HTTP body using the specified Java class
  local body
  local response
  body=$(java -cp "$jar_file" "$java_class")
  if [ -z "$body" ]; then
    echo "Error: Failed to generate HTTP body using Java class '$java_class'."
    return 1
  fi

  echo "Sending:  $body"
  # Attempt to send the POST request
  for ((i=1; i<=retries; i++)); do
    response=$(curl -s -X POST "$target_url" \
      -H "Content-Type: application/json" \
      -d "$body")

    if echo "$response" | grep -q '"errors":\s*\[\s*\]'; then
      echo "Neo4j query succeeded."
      echo $response
      success=1
      break
    else
      echo "Neo4j query failed. Response:"
      echo $response
      echo "Attempt $i failed with HTTP status $http_status. Retrying in $delay seconds..."
      sleep "$delay"
    fi
    
  done

  if [ "$success" -eq 1 ]; then
    echo "Operation completed successfully."
    return 0
  else
    echo "Error: Operation failed after $retries attempts."
    return 1
  fi
}

JAR_FILE=/tmp/connect-plugins/knowledgegraph-connect-plugin.jar
JAVA_CLASS="de.dfki.cos.aas2graph.kafka.init.Neo4jInitializingHttpBodyGenerator"
create_and_post "$JAR_FILE" "$JAVA_CLASS" "$BASYX_NEO4J_TARGET_URL"
init_status=$?


if [ $init_status -eq 0 ]; then
  echo "The neo4j-kafka-connect plugin is ready to use. 🎉"
else
  echo "Initialization operations failed."
  exit 1
fi


until curl -sf http://localhost:8083/connectors > /dev/null; do
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

CONNECTOR_NAME="Kafka2Neo4jConnector"
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


# block to keep async kafka-connect process running
wait

