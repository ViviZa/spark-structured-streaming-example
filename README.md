# Spark Structured Stream Example wit Kafka
This is an example of a spark structured stream receiving records from kafka, extracting its values, timestamps and keys
and encoding them to java objects.

## Run application
1. Start the zookeeper and kafka via: ` docker-compose up -d `
2. Run SparkStructuredStream
3. Run KafkaProducerExecutor