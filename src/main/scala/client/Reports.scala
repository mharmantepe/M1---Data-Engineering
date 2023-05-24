package client

import client.Producer.words
import play.api.libs.json.{JsValue, Json}
import java.util.Date
import scala.util.Random

case class Report(droneId: String, longitude: Double, latitude: Double, timestamp: String, citizens : List[Citizen] , words: List[String])
object Reports {
  def generateReport(): Report = {
    val droneId = 1 + Random.nextInt(5)
    val longitude = Random.nextDouble() * 360 - 180
    val latitude = Random.nextDouble() * 180 - 90
    val timestamp = new Date().toString

    // Generate a random count between 1 and 10
    val randomNbCitizen = Random.nextInt(10) + 1
    val citizens = List.fill(randomNbCitizen)(client.Citizen.generateCitizen())

    // Choose a number of words to include in the report between 2 and 10
    val randomNbWords = 2 + Random.nextInt(9)
    // Generate the list of words by selecting them at random
    val randWords = List.fill(randomNbWords)(words(Random.nextInt(words.length)))

    /*
        // Create a JsObject with the columns in the desired order
        val json = Json.obj(
          "timestamp" -> timestamp.toString,
          "latitude" -> latitude.toString,
          "droneId" -> droneId.toString,
          "citizens" -> citizens.toString,
          "longitude" -> longitude.toString,
          "words" -> randWords.mkString(",")
        )
        json*/
/*
    Json.toJson(Map("droneId" -> droneId.toString, "longitude" -> longitude.toString, "latitude" -> latitude.toString,
      "timestamp" -> timestamp.toString, "citizens" -> citizens.toString, "words" -> randWords.mkString(",")))
*/
    Report(droneId.toString, longitude, latitude, timestamp, citizens, randWords)

  }
}
