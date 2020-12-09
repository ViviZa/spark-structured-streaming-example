import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.RelationalGroupedDataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.window;
import static org.apache.spark.sql.types.DataTypes.createStructField;

public class SparkStructuredStream {

    public static void main(final String[] args) throws TimeoutException, StreamingQueryException {
        final SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaStructuredNetworkWordCount")
                .getOrCreate();

        final StructType structType = DataTypes.createStructType(
                new StructField[]{
                        createStructField("ID", DataTypes.LongType, false),
                        createStructField("CONTRACT", DataTypes.StringType, false),
                        createStructField("PRICE", DataTypes.DoubleType, false),
                });

        final Dataset dataset = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "trade-input")
                .load();

        dataset.printSchema();

        final Dataset selectedDataset = dataset
                .selectExpr("CAST(value AS STRING)", "CAST(timestamp AS TIMESTAMP) as KAFKA_TIMESTAMP", "CAST(key AS STRING) as TOPIC_KEY")
                .as(Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP(), Encoders.STRING()))
                .select(functions.from_json(col("value"), structType).as("data"), col("KAFKA_TIMESTAMP"), col("TOPIC_KEY"))
                .select("data.*", "KAFKA_TIMESTAMP", "TOPIC_KEY")
                .as(Encoders.bean(Trade.class));

        final RelationalGroupedDataset groupedByDataset =
                selectedDataset.withWatermark("KAFKA_TIMESTAMP", "1 minutes")
                        .groupBy(selectedDataset.col("TOPIC_KEY"),
                                window(selectedDataset.col("KAFKA_TIMESTAMP"), "1 minute")); //TODO: make windowing and grouping work


        groupedByDataset.df().writeStream()
                .format("console")
                .option("truncate", "false")
                .start()
                .awaitTermination();
    }

}
