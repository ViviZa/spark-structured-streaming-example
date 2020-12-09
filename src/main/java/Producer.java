import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    private final org.apache.kafka.clients.producer.Producer<String, Trade> kafkaProducer;
    private final BlockingQueue<Trade> tradeBlockingQueue;
    private final String tradeInputTopic;

    public Producer(final org.apache.kafka.clients.producer.Producer<String, Trade> kafkaProducer, final BlockingQueue<Trade> tradeBlockingQueue, final String tradeInputTopic) {
        this.kafkaProducer = kafkaProducer;
        this.tradeBlockingQueue = tradeBlockingQueue;
        this.tradeInputTopic = tradeInputTopic;
    }

    @Override
    public void run() {
        while (true) {
            final Trade polledTrade = this.tradeBlockingQueue.poll();
            if (polledTrade != null) {
                sendTradeToKafka(polledTrade);
            }
        }
    }

    public void sendTradeToKafka(final Trade trade) {
        try {
            final String recordKey = "key-" + trade.getCONTRACT();
            final ProducerRecord<String, Trade> producerRecord = new ProducerRecord<>(this.tradeInputTopic, recordKey, trade);
            this.kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                if (e == null) {
                    System.out.println("Successfully sent record");
                } else {
                    System.out.println("Failed to sent record: " + e);
                }
            });
        } catch (final Exception e) {
            System.out.println("Failed to process record: " + e);
            this.kafkaProducer.close();
        }
    }
}
