package client

import java.io._
import java.util.Properties
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import play.api.libs.json.Format.GenericFormat
import scala.collection.JavaConverters._
import play.api.libs.json._

object Consumer extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer")

  val consumer = new KafkaConsumer[String, Array[Byte]](props)

  val topic = "reports"

  consumer.subscribe(Seq(topic).asJava)
  val writer = new PrintWriter(new File("output.csv"))

  while (true) {
    //Timeout to not hang the application if there is no message
    val records: ConsumerRecords[String, Array[Byte]] = consumer.poll(1000)

    records.asScala.foreach {
      case record =>
        println(record)
        println(record.value)
        val jsonStr = record.value()
        println(jsonStr)
        // Parse the JSON string back into a JsValue
        val json = Json.parse(jsonStr)
        val timestamp = (json \ "timestamp").as[String]
        val droneId = (json \ "droneId").as[Int]
        println(droneId)
        val latitude = (json \ "latitude").as[String]
        val citizenScore = (json \ "citizenScore")
        println(citizenScore)
        val citizenName = (json \ "citizenName")
        println(citizenName)
        val citizenSurname = (json \ "citizenSurname").as[List[String]]
        // Access the fields based on their names
        /*
        val timestamp = (json \ "timestamp").as[String]
        val droneId = (json \ "droneId").as[String]
        val latitude = (json \ "latitude").as[Double]
        val longitude = (json \ "longitude").as[Double]
        */
        /*
                // Extract the citizens field as an array of Citizen objects
                val citizensArray = (json \ "citizens").asOpt[JsArray].getOrElse(Json.arr())

                // Extract the names, surnames, and scores from the Citizen objects
                val citizenNames = citizensArray.value.flatMap { citizen =>
                  (citizen \ "name").asOpt[String]
                }
                val citizenSurnames = citizensArray.value.flatMap { citizen =>
                  (citizen \ "surname").asOpt[String]
                }
                val citizenScores = citizensArray.value.flatMap { citizen =>
                  (citizen \ "score").asOpt[String].map { scoreString =>
                    // Convert the string to an integer or handle any other necessary logic
                    Try(scoreString.toInt).toOption
                  }
                }

        /*
         */
                // Access the citizens field as an array
                val citizensArray = (json \ "citizens").as[JsArray]

                // Initialize arrays for name and score values
                var citizenNames: Array[String] = Array.empty
                var citizenSurnames: Array[String] = Array.empty
                var citizenScores: Array[Int] = Array.empty

                // Iterate over the elements in the citizens array
                citizensArray.value.foreach { citizen =>
                  // Access the fields of each citizen object
                  val name = (citizen \ "name").as[String]
                  val surname = (citizen \ "surname").as[String]
                  val score = (citizen \ "score").as[Int]

                  // Add values to respective arrays
                  citizenNames = citizenNames :+ name
                  citizenSurnames = citizenSurnames :+ surname
                  citizenScores = citizenScores :+ score
                }
        */
        //val words = (json \ "words").as[List[String]]

        val csvLine = s"${record.timestamp()},${record.key()}, ${record.value()}"
          /*+
          s"${timestamp}," +
          s"${droneId}" +
          s"${latitude}" +
          s"${longitude}"*/
        //s"${citizenNames}" +
        //s"${citizenSurnames}" +
        //s"${citizenScores}"
        //s"${words}"
        writer.write(csvLine + "\n")
    }
    writer.flush()

  }
  writer.close()
  consumer.close()
}
