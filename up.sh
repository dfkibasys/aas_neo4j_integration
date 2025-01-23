#!/bin/bash

# echo "Building kafka-connect plugin with maven ..."
# mvn -f kafka-connect/java clean install
# echo "Starting stack..."
docker-compose --project-name knowledgegraph-v3 up --detach --remove-orphans --force-recreate --renew-anon-volumes --build


source .env

echo "portainer:        http://localhost:8084 (admin:$PORTAINER_ADMIN_PASSWORD"
echo "akhq:             http://localhost:8086"
echo "aas-registry:     http://localhost:8083/shell-descriptors"
echo "aas-repo:         http://localhost:8081/shells"
echo "sm-registry:      http://localhost:8082/submodel-descriptors"
echo "sm-repo:          http://localhost:8081/submodels"
echo "neo4j:            http://localhost:7474"
echo "kafka-connect-ui: http://localhost:8094"
echo "kafka-cc:         http://localhost:9021"