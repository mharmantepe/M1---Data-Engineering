import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ArrayType, DoubleType, IntegerType, StringType, StructType}
import org.apache.spark.sql.functions._

object Analytics extends App {
  // Create a Spark session
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Storage")
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

  val df = spark.readStream
    .schema(reportSchema)
    .json("/Users/silaharmantepe/Documents/GitHub/DataEngineering/src/resources/localStorage/dataFiles")

  df.printSchema()

  val explodedDF = df.select(explode(col("citizens")).as("citizen"))
  val df2 = explodedDF.select("citizen.name")

  df2.printSchema()

  explodedDF.writeStream
    .format("console")
    .outputMode("append")
    .start()
    .awaitTermination()

  spark.close()
}
