import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class KafkaProducerExecutor {


    public static void main(final String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(20);
        final BlockingQueue<Trade> tradeBlockingQueue = new LinkedBlockingQueue<>();

        final KafkaProducer<String, Trade> kafkaProducer = new KafkaProducer<>(getProducingJsonProperties());

        final Producer producer = new Producer(kafkaProducer, tradeBlockingQueue, "trade-input");
        final Client client = new Client(tradeBlockingQueue);

        executorService.execute(producer);
        executorService.execute(client);
    }

    public static Properties getProducingJsonProperties(){
        final Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class.getName());
        return properties;
    }
}
