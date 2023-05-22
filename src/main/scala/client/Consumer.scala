package client

import java.io._
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

  val topic = "reports"

  consumer.subscribe(Seq(topic).asJava)
  val writer = new PrintWriter(new File("output.csv"))

  while (true) {
    //Timeout to not hang the application if there is no message
    val records: ConsumerRecords[String, String] = consumer.poll(1000)

    records.asScala.foreach {
      case record =>
        println(record)
        println(record.value)

        val csvLine = s"${record.timestamp()},${record.key()},${record.value()}"
        writer.write(csvLine + "\n")
    }
    writer.flush()

  }
  writer.close()
  consumer.close()
}
