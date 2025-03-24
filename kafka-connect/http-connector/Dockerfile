FROM confluentinc/cp-kafka-connect:7.7.1

USER root
COPY target/simple-http-sink-connector-1.0.0.jar /usr/share/java/simple-http-sink-connector/

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components,/usr/share/java/simple-http-sink-connector"