import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable {

    private final BlockingQueue<Trade> tradeBlockingQueue;

    public Client(final BlockingQueue<Trade> tradeBlockingQueue) {
        this.tradeBlockingQueue = tradeBlockingQueue;
    }

    @Override
    public void run() {

        final Trade[] tradeArray = getTradeArrayFromFile();
        for (int i = 0; i < tradeArray.length; i++) {
            waitSeconds(3000); // wait until sending the next record
            this.tradeBlockingQueue.add(tradeArray[i]);
        }
    }

    private Trade[] getTradeArrayFromFile() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("all-trades.json");
        try {
            final String jsonString = IOUtils.toString(inputStream);
            final Trade[] tradeArray = objectMapper.readValue(jsonString, Trade[].class);

            return tradeArray;

        } catch (final IOException e) {
            System.out.println("Could not parse inputstream ");
        }

        return new Trade[0];
    }

    private void waitSeconds(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            System.out.println("Could not wait");
        }
    }
}
