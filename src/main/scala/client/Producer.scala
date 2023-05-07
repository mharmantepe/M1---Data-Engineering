package client

import java.util.{Date, Properties}
import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.io.Source

object Producer extends App{
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val topic = "my-topic"

  val producer = new KafkaProducer[String, String](props)
  val names = List("John", "Tom", "Sophia", "Jeremy", "Hannah", "Clara", "Dennis", "Chloe")
  val surnames = List("SMITH", "JONES", "WILLIAMS", "DAVIES", "BROWN", "FITZGERALD", "WRIGHT", "LEWIS")
  val wrds = List("hello", "depressed", "good", "happy", "tired", "sad", "exhausted", "joyous", "dancing", "crying")

  def readCSV(filename: String): List[String] = {
    val source = Source.fromFile(filename)
    val lines = source.getLines.toList
    source.close()
    lines
    // Split each line into fields and convert to a List[List[String]]
    //lines.map(line => line.split(",").map(_.trim).toList)
  }

  def generateRandomData(): List[String] = {
    val id = Random.nextInt(1000000)
    val longitude = Random.nextDouble() * 360 - 180
    val latitude = Random.nextDouble() * 180 - 90
    val timestamp = new Date().getTime
    val rating = Random.nextInt(6)
    val rand_name = names(Random.nextInt(names.length))
    val rand_surname = surnames(Random.nextInt(surnames.length))
    val person = s"${rand_name} ${rand_surname}"
    val rand_word = wrds(Random.nextInt(wrds.length))
    //val rand_people = randomPeople(names, surnames)
    //val people = Seq.fill(Random.nextInt(10))(Random.shuffle(rand_people).take(Random.nextInt(10)))
    val words = Seq.fill(Random.nextInt(10))(Random.shuffle(wrds).take(Random.nextInt(10)))
    val drn =  List(id.toString, longitude.toString, latitude.toString, timestamp.toString, person, rating.toString, words.mkString(", "))
    drn
  }

  while (true) {
    val data = generateRandomData()
    val record = new ProducerRecord[String, String](topic, data.toString())
    producer.send(record)
    Thread.sleep(60000) // sleep for 1 minute
  }

  //val namesList = readCSV("")
  producer.close()

}


/*
  def randomPeople(name : List[String], surname: List[String]): List[String] = {
    var i = 0
    val rand_list = List[String]()
    while(i <= 100){
      val rand_name = name(Random.nextInt(name.length))
      val rand_surname = surname(Random.nextInt(surname.length))
      val person = s"${rand_name} ${rand_surname}"
      val rand_list = person :: rand_list
      i += 1
    }
  */