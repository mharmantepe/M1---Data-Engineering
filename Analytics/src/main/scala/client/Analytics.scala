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

  dfReports.printSchema()
  println(dfReports)
  dfReports.show()

  //Explode the citizen field in order to access its subfields : name, surname, words
  val explodedCitizensDF = dfReports.select(explode(col("citizens")).as("citizen"))
  (explodedCitizensDF.select(col("citizen"))).show()

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
  val mostHeardWords = explodedCitizensDF.select(col("citizen.words").as("words"))
    .groupBy(col("words"))
    .count()
  mostHeardWords.show()

  //The list of citizens with most alerts
  val citizensWithMostAlerts = explodedCitizensDF.select(col("name"), col("surname"))
    .groupBy("name", "surname")
    .count().as("alertsCount")
    .orderBy(col("alertsCount").desc)
  citizensWithMostAlerts.show()

  //The distribution of alerts depending on the day of the week
  //Citizens who have never created a riot (never had a score < 5)
  spark.close()
}
