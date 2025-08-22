#!/bin/bash
echo install quemu
docker run --privileged --rm multiarch/qemu-user-static --reset -p yes
echo login
docker login -u dfkibasys

echo build and publish image
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t dfkibasys/aas-neo4j-kafka-connect-plugin:7.9.1-0.1.14 \
  -t dfkibasys/aas-neo4j-kafka-connect-plugin:latest \
  --push \
  .