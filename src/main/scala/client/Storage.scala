package client

import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
object Storage extends App {
  val spark = SparkSession
    .builder
    .appName("Spark-Storage")
    .getOrCreate()


  // Subscribe to 1 topic defaults to the earliest and latest offsets
  val df = spark
    .read
    .format("kafka")
    .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
    .option("subscribe", "topic1")
    .load()
  df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    .as[(String, String)]
}
