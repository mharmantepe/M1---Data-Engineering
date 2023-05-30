package client

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import play.api.libs.json.Json
import play.api.libs.json._
import java.util.Properties
import scala.collection.JavaConverters._

object Alerts extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "alerts")

  val topic = "reports"

  //Create a Kafka Consumer
  //Consumer k,v types must be respectively the same as the class configurations.
  val consumer = new KafkaConsumer[String, String](props)
  //Seq(topic) creates an immutable sequence of scala with a single element (which is the topic defined above)
  //for subscribing the consumer to a single topic. I then convert it into a Java list using the .asJava method.
  consumer.subscribe(Seq(topic).asJava)

  def findAgitatedCitizens(json: JsValue): List[String] = {
    //Citizens field of the report is a List of JsObject (seen in the producer)
    val citizens = (json \ "citizens").as[List[JsObject]]

    //Access each field of the citizens json array
    citizens.flatMap { citizen =>
      val score = (citizen \ "score").as[Int]
      if (score < 5) {
        val name = (citizen \ "name").as[String]
        val surname = (citizen \ "surname").as[String]
        Some(s"Name: $name, Surname: $surname, Score: $score")
      } else {
        None
      }
    }
  }

  while (true) {
    //Attention! the k,v types of the consumer record must respectively be the same as in the class configurations
    //of the serializer and deserializers.
    val reports : ConsumerRecords[String, String] = consumer.poll(1000)
    //Convert the Java collection into a Scala collection with .asScala
    reports.asScala.foreach {
      case report =>
        //Value component of the message is deseralized as String
        val value = report.value()
        // Parse the value which is a JSON string to extract citizens with score less than 5
        val json = Json.parse(value)
        val citizens = findAgitatedCitizens(json)
        if (citizens.nonEmpty) {
          val latitude = (json \ "latitude").as[Double]
          val longitude = (json \ "longitude").as[Double]
          val droneId = (json \ "droneId").as[Int]
          val timestamp = (json \ "timestamp").as[String]
          println(s"Alert!! Agitated citizens detected with score less than 5 at latitude $latitude and longitude $longitude")
          citizens.foreach(println)
          println("Information :\n" +
            s"Drone ID :  ${droneId} "+
            s"Timestamp : ${timestamp}")
          println("--------------------")
        }
    }
  }

  consumer.close()
}