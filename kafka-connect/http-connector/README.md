# Simple HTTP Sink Connector

Ein einfacher Kafka Connect HTTP Sink Connector, der Nachrichten via POST an eine HTTP-URL sendet.

## Konfiguration
Beispiel:

```json
{
  "name": "simple-http-sink",
  "config": {
    "connector.class": "com.example.connect.http.SimpleHttpSinkConnector",
    "tasks.max": "1",
    "topics": "your-topic",
    "target.url": "http://your-target/api",
    "headers": "Authorization:Bearer xyz,Content-Type:application/json",
    "retry.max.retries": "3",
    "retry.backoff.ms": "1000"
  }
}
```