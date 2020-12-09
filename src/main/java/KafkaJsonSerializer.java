import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;


public class KafkaJsonSerializer implements Serializer<Trade> {
    @Override
    public byte[] serialize(final String s, final Trade o) {
        byte[] retVal = null;
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
        return retVal;
    }

    @Override
    public void close() {

    }
}
