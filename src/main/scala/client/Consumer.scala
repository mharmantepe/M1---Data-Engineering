package  client

import java.util.Properties
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConverters._

object Consumer extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)

  val topic = "my-topic"

  consumer.subscribe(Seq(topic).asJava)

  while (true) {
    //Timeout to not hang the application if there is no message
    val records: ConsumerRecords[String, String] = consumer.poll(1000)
    println("hey")
    println(records)
    /*
    for (record <- records.asScala) {
      println("foreach" + record)
      println(record.value())
    }
    */

    records.asScala.foreach {
      case record => println(record.value + (", "))
    }

  }
  consumer.close()
}
