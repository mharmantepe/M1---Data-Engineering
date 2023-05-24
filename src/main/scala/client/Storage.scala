package client

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.streaming.OutputMode

object Storage extends App {

  // Create a Spark session
  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("Storage")
    .config("spark.streaming.stopGracefullyOnShutdown", "true")
    .getOrCreate()

  import spark.implicits._

  // Connect to Kafka as continuous stream on both topics "reports" and "alerts"
  // We use a group.id so that if we start several instances (scale up), messages are not treated twice
  //Create an initial DataFrame from the Kafka data source using Spark's read API. This df reads each line from Kafka.
  val initDf = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("group.id", "storage")
    .option("subscribe", "reports") //topics to subscribe to add "alerts" next to "reports" lateron
    .load()

  initDf.printSchema()
  println(initDf)
  // The schema (data structure) of a Report (value of the Kafka message) sent as JSON on Kafka
  val reportSchema = new StructType()
    .add("timestamp", StringType)
    .add("droneId", IntegerType)
    .add("latitude", DoubleType)
    .add("longitude", DoubleType)

    .add("citizens", new ArrayType(
      new StructType()
        .add("citizenName", StringType)
        .add("citizenSurname", StringType)
        .add("citizenScore", IntegerType),
      false
    ))
    .add("citizenName", StringType)
    .add("citizenSurname", StringType)
    .add("citizenScore", StringType)
    .add("words", new ArrayType(StringType, true))




  //Since the value is in binary, first we need to convert the binary value to String using selectExpr()
  //Then extract the value which is in JSON String to DataFrame and convert to DataFrame columns using custom schema.
  val parsedDf = initDf.selectExpr("CAST(value AS STRING) as value")
    .select(from_json($"value", reportSchema).as("report"))
    .select("report.*")
  parsedDf.printSchema()

  println(parsedDf)
  /*
  val parsedDf = initDf.select(from_json(col("value"), reportSchema).as("data"))
    .select("data.*")*/
  /*
   // We parse the JSON string in the "value" column for the topic "reports"
  val reports = df.filter($"topic" === "reports")
    .select(from_json($"value".cast(StringType), reportSchema).as("report"))
    .select($"report.*")
   */

  // We write the Reports as JSON in local files
  // For better scalability, this should be replaced with a distributed data lake (ex. HDFS/S3)
  // We use a processing time trigger of 10s to reduce the number of small files
  val finalDF = parsedDf.writeStream
    .format("console")
    //.option("path", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/dataFiles")
    //.option("checkpointLocation", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/checkPoints")
    .outputMode("append")
    .trigger(Trigger.ProcessingTime(10000))
    .start()

  finalDF.awaitTermination()



  spark.close()


}
