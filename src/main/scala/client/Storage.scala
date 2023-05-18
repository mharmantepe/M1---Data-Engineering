package client

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._
/*
object Storage extends App {
  // Create a Spark session
  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("Storage")
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
*/