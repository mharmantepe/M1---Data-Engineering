import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ArrayType, DoubleType, IntegerType, StringType, StructType}
import org.apache.spark.sql.functions._

object Analytics extends App {

  // Create a Spark session
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Analytics")
    .config("spark.streaming.stopGracefullyOnShutdown", "true")
    .getOrCreate()

  import spark.implicits._

  //Create a data schema for the reports that we will be reading from the storage
  val reportSchema = new StructType()
    .add("timestamp", StringType)
    .add("droneId", IntegerType)
    .add("latitude", DoubleType)
    .add("longitude", DoubleType)
    .add("citizens", ArrayType(new StructType()
      .add("name", StringType)
      .add("surname", StringType)
      .add("score", IntegerType)
      .add("words", StringType)))

  //Read reports data from the local storage
  val dfReports = spark.read
    .schema(reportSchema)
    .json("/Users/silaharmantepe/Documents/GitHub/DataEngineering/Storage/src/resources/localStorage/dataFiles")

  //dfReports.printSchema()
  //println(dfReports)
  //dfReports.show()

  //Explode the citizen field in order to access its subfields : name, surname, score, words
  val explodedCitizensDF = dfReports.select(explode(col("citizens")).as("citizen"))
  //(explodedCitizensDF.select(col("citizen"))).show()

  //10 most peaceful citizens (mean of score)
  println("10 most peaceful citizens")
  val peacefulCitizens = explodedCitizensDF.select(col("citizen.name").as("name"),
    col("citizen.surname").as("surname"), col("citizen.score").as("score"))
    .groupBy("name", "surname")
    .agg(avg("score").as("avgPeaceScore"))
    .orderBy(col("avgPeaceScore").desc)
    .limit(10)
  peacefulCitizens.show()

  //Top 10 most heard words
  println("Top 10 most heard words")
  val explodedWordsDF = explodedCitizensDF.select(split(col("citizen.words"), ",").as("wordsExp"))
  val mostHeardWords = explodedWordsDF.select(explode(col("wordsExp")).as("words"))
    .groupBy(col("words"))
    .count()
    .orderBy(col("count").desc)
    .limit(10)
  mostHeardWords.show()

  //The list of citizens with most alerts
  println("The list of citizens with most alerts")
  val citizensWithMostAlerts = explodedCitizensDF.select(col("citizen.name"), col("citizen.surname"), col("citizen.score"))
    .filter(col("citizen.score") < 5)
    .groupBy("name", "surname")
    .count()
    .orderBy(col("count").desc)
  citizensWithMostAlerts.show()

  //Citizens who have never created a riot (never had a score < 5)
  println("Citizens who have never created a riot")
  val citizensNeverRiot = explodedCitizensDF.select(col("citizen.name"), col("citizen.surname"), col("citizen.score"))
    .filter(col("citizen.score") >= 5)
  citizensNeverRiot.show()

  spark.close()
}
