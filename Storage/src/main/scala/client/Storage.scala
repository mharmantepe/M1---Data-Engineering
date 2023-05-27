package client

import org.apache.spark.sql.SparkSession
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
    .option("subscribe", "reports, alerts") //topics to subscribe to add "alerts" next to "reports" lateron
    .load()

  initDf.printSchema()
  println(initDf)

  // The schema (data structure) of a Report (value of the Kafka message) sent as JSON on Kafka
  val schema = new StructType()
    .add("timestamp", StringType)
    .add("droneId", IntegerType)
    .add("latitude", DoubleType)
    .add("longitude", DoubleType)
    .add("citizens", ArrayType(new StructType()
      .add("name", StringType)
      .add("surname", StringType)
      .add("score", IntegerType)
      .add("words", StringType)))
  println(schema)


  //First we filter the topic field of the initial dataframe to only get the reports
  //Since the value is in binary, we need to convert the binary value to String using selectExpr()
  //Then extract the value which is in JSON String to DataFrame and convert to DataFrame columns using custom schema.
  val parsedDfReports = initDf.filter($"topic" === "reports")
    .selectExpr("CAST(value AS STRING) as value")
    .select(from_json($"value", schema).as("report"))
    .select("report.*")
  parsedDfReports.printSchema()
  println(parsedDfReports)

  //Filter the topic this time on the alerts and apply the data schema
  val parsedDfAlerts = initDf.filter($"topic" === "alerts")
    .selectExpr("CAST(value AS STRING) as value")
    .select(from_json($"value", schema).as("alert"))
    .select("alert.*")
  parsedDfAlerts.printSchema()
  println(parsedDfAlerts)


  //Write the Reports as JSON in local files /reports
  //Use a processing time trigger of 10s to reduce the number of small files
  val finalDFReports = parsedDfReports.writeStream
    .format("json")
    .option("path", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/Storage/src/resources/localStorage/dataFiles")
    .option("checkpointLocation", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/Storage/src/resources/localStorage/checkPoints")
    .outputMode("append")
    .trigger(Trigger.ProcessingTime(10000))
    .start()
/*
  //Write the Alerts as JSON in local files /alerts
  val finalDFAlerts = parsedDfAlerts.writeStream
    .format("json")
    .option("path", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/dataFiles/alerts")
    .option("checkpointLocation", "/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/checkPoints")
    .outputMode("append")
    .trigger(Trigger.ProcessingTime(10000))
    .start()
*/
  finalDFReports.awaitTermination()
  //finalDFAlerts.awaitTermination()
  spark.close()
}
