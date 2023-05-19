package client

import java.io._
import java.util.Properties
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import scala.util.matching.Regex
import scala.collection.JavaConverters._

object Consumer extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)

  val topic = "reports"

  consumer.subscribe(Seq(topic).asJava)
  val writer = new PrintWriter(new File("output.csv"))

  while (true) {
    //Timeout to not hang the application if there is no message
    val records: ConsumerRecords[String, String] = consumer.poll(1000)
    //println(records)
    /*
    for (record <- records.asScala) {
      println("foreach" + record)
      println(record.value())
    }
    */

    records.asScala.foreach {
      case record =>
        println(record)
        println(record.value)
        //val pattern: Regex = "(?<=\\().*?(?=\\))".r
        //val matches = pattern.findAllIn(record.value)
        //println(matches)
        //val values = record.value.split(",").toList
        //matches.foreach(println)
        //val attributes = record.value.split(",")
        val csvLine = s"${record.timestamp()},${record.key()},${record.value()}"
        writer.write(csvLine + "\n")
    }
    writer.flush()

  }
  writer.close()
  consumer.close()
}
