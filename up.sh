#!/bin/bash

# echo "Building kafka-connect plugin with maven ..."
# mvn -f kafka-connect/java clean install
# echo "Starting stack..."
docker-compose  --project-name knowledgegraph-v3 up --detach --remove-orphans --force-recreate --renew-anon-volumes
#docker-compose  --project-name knowledgegraph-v3  --file compose.yaml up --detach --remove-orphans --force-recreate --renew-anon-volumes
# 
source .env

# Colors
GREEN="\e[32m"
CYAN="\e[36m"
RESET="\e[0m"

echo -e "${GREEN}========================= Basyx Services =========================${RESET}"
echo -e "${CYAN}AAS Environment${RESET}"
echo -e "  Swagger UI:            → http://localhost:8081/swagger-ui/index.html"
echo -e "  Shells:                → http://localhost:8081/shells"
echo -e "  Submodels:             → http://localhost:8081/submodels"

echo -e "\n${CYAN}AAS Registry${RESET}"
echo -e "  Swagger UI:            → http://localhost:8083/swagger-ui/index.html"
echo -e "  Shell Descriptors:     → http://localhost:8083/shell-descriptors"

echo -e "\n${CYAN}Submodel Registry${RESET}"
echo -e "  Swagger UI:            → http://localhost:8082/swagger-ui/index.html"
echo -e "  Submodel Descriptors:  → http://localhost:8082/submodel-descriptors"

echo -e "\n${CYAN}AAS GUI${RESET}"
echo -e "  Web Interface:         → http://localhost:8099/"

echo -e "\n${GREEN}========================= Kafka =========================${RESET}"
echo -e "AKHQ UI:                 → http://localhost:8086"
echo -e "Kafka Connect UI:        → http://localhost:8094"
echo -e "Kafka Connect:           → http://localhost:8085"
echo -e "Kafka Broker:            → http://localhost:9092"

echo -e "\n${GREEN}========================= Neo4j =========================${RESET}"
echo -e "Neo4j UI:                → http://localhost:7474"
echo -e "Neo4j (Bolt):            → http://localhost:7687"

echo -e "\n${GREEN}========================= Portainer =========================${RESET}"
echo -e "Portainer UI:            → http://localhost:8084 (admin:$PORTAINER_ADMIN_PASSWORD)"
