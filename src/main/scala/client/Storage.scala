package client

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._

import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.streaming.OutputMode

object Storage extends App {

  // The schema (data structure) of a Report sent as JSON on Kafka
  val reportSchema = new StructType()
    .add("time", LongType)
    .add("droneId", StringType)
    .add("latitude", DoubleType)
    .add("longitude", DoubleType)
    .add("citizens", new ArrayType(
      new StructType()
        .add("name", StringType)
        .add("peaceScore", IntegerType),
      false
    ))
    .add("words", new ArrayType(StringType, false))

  // Create a Spark session
  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("Storage")
    .getOrCreate()

  import spark.implicits._

  // Connect to Kafka as continuous stream on both topics "reports" and "alerts"
  // We use a group.id so that if we start several instances (scale up), messages are not treated twice
  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("group.id", "storage")
    .option("subscribe", "reports,alerts") //topics to subscribe to
    .load()

  // We parse the JSON string in the "value" column for the topic "reports"
  val reports = df.filter($"topic" === "reports")
    .select(from_json($"value".cast(StringType), reportSchema).as("report"))
    .select($"report.*")

  // We write the Reports as JSON in local files
  // For better scalability, this should be replaced with a distributed data lake (ex. HDFS/S3)
  // We use a processing time trigger of 10s to reduce the number of small files
  val finalDF = reports.writeStream
    .format("json")
    .option("path", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/dataFiles")
    .option("checkpointLocation", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/checkPoints")
    .outputMode(OutputMode.Append())
    .trigger(Trigger.ProcessingTime(10000))
    .start()
    .awaitTermination()

  spark.close()
}
