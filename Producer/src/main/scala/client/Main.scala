package client

import client.models.Drones
import client.models.Citizens

import java.util.{Date, Properties}
import scala.io.Source
import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.libs.json.Json
import play.api.libs.json._


object Main extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val topic = "reports"

  val producer = new KafkaProducer[String, String](props)

  val words = List("hello", "depressed", "good", "happy", "tired", "sad", "exhausted", "joyous", "dancing", "crying")

  def generateReport(drone: client.models.Drone): JsValue = {
    val timestamp = new Date()

    // Generate a random count between 1 and 10
    val randomNbCitizen = 1 + Random.nextInt(10)
    //Create a list of random citizens
    val citizens = List.fill(randomNbCitizen)(client.models.Citizens.generateCitizen())
    //println(citizens)

    // Choose a number of words to include in the report between 2 and 10
    val randomNbWords = 2 + Random.nextInt(9)
    // Generate the list of words by selecting them at random
    val randWords = List.fill(randomNbWords)(words(Random.nextInt(words.length)))
    //println(randWords)

    // Create a JsObject with the columns in the desired order
    val json = Json.obj(
      "timestamp" -> timestamp.toString,
      "droneId" -> drone.droneId,
      "latitude" -> drone.latitude,
      "longitude" -> drone.longitude,
      //Convert the json object to json into json format with .toJson() to allow transformation or manipulation of the data
      "citizens" -> Json.toJson(citizens.map { citizen =>
        Json.obj(
          "name" -> citizen.name,
          "surname" -> citizen.surname,
          "score" -> citizen.score,
          //Concatenate each word of the list into a single string with .mkString(",")
          "words" -> Json.toJson(randWords.mkString(","))
        )
      }),
    )
    json

  }


  while (true) {
    val drone = Drones.generateDrone()
    val data = generateReport(drone)
    println(data)
    val report = new ProducerRecord[String, String](topic, drone.droneId.toString, data.toString)
    producer.send(report)
    //Pause for 60000 milisecs (=1 min) to control the timing between different threads.
    Thread.sleep(60000)
  }
  producer.close()
}
