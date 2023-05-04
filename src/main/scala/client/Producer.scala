package client

import java.util.{Date, Properties}
import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer extends App{

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val topic = "my-topic"

  val producer = new KafkaProducer[String, String](props)

  def generateRandomData(): Map[String, String] = {
    val id = Random.nextInt(1000000)
    val longitude = Random.nextDouble() * 360 - 180
    val latitude = Random.nextDouble() * 180 - 90
    val timestamp = new Date().getTime
    val rating = Random.nextInt(6)
    val words = Seq.fill(Random.nextInt(10))(Random.alphanumeric.take(5).mkString)
    Map("id" -> id.toString, "longitude" -> longitude.toString, "latitude" -> latitude.toString,
      "timestamp" -> timestamp.toString, "rating" -> rating.toString, "words" -> words.mkString(","))
  }


  val data = generateRandomData()
  val record = new ProducerRecord[String, Map[String, String]](topic,data)
  producer.send(record)
    //Thread.sleep(60000) // sleep for 1 minute

  producer.close()
}
