@echo off
setlocal

:: Start the Docker Compose stack
docker-compose --project-name knowledgegraph-v3 up --detach --remove-orphans --force-recreate --renew-anon-volumes

echo.
echo ========================= Basyx Services =========================
echo AAS Environment:
echo   Swagger UI:            http://localhost:8081/swagger-ui/index.html
echo   Shells:                http://localhost:8081/shells
echo   Submodels:             http://localhost:8081/submodels

echo.
echo AAS Registry:
echo   Swagger UI:            http://localhost:8083/swagger-ui/index.html
echo   Shell Descriptors:     http://localhost:8083/shell-descriptors

echo.
echo Submodel Registry:
echo   Swagger UI:            http://localhost:8082/swagger-ui/index.html
echo   Submodel Descriptors:  http://localhost:8082/submodel-descriptors

echo.
echo AAS GUI:
echo   Web Interface:         http://localhost:8099/

echo.
echo ========================= Kafka =========================
echo AKHQ UI:                 http://localhost:8086
echo Kafka Connect UI:        http://localhost:8094
echo Kafka Connect:           http://localhost:8085
echo Kafka Broker:            http://localhost:9092

echo.
echo ========================= Neo4j =========================
echo Neo4j UI:                http://localhost:7474
echo Neo4j (Bolt):            http://localhost:7687

echo.
echo ========================= Portainer =========================
echo Portainer UI:            http://localhost:8084

endlocal
