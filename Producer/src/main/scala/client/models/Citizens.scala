package client.models

import scala.util.Random
case class Citizen(name: String, surname: String, score: Int)
object Citizens {
  val names = List("John", "Tom", "Sophia", "Jeremy", "Hannah", "Clara", "Dennis", "Chloe")
  val surnames = List("SMITH", "JONES", "WILLIAMS", "DAVIES", "BROWN", "FITZGERALD", "WRIGHT", "LEWIS")

  def generateCitizen(): Citizen = {
    // Generate a first and last name by choosing them at random
    val randName = names(Random.nextInt(names.length))
    val randSurname = surnames(Random.nextInt(surnames.length))
    //val person = s"${randName} ${randSurname}"
    val peaceScore = Random.nextInt(10)

    Citizen(randName, randSurname, peaceScore)
  }
}
