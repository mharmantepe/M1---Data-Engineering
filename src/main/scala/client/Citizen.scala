package client

import scala.util.Random
case class Citizen(name: String, score: Int)

object Citizen {
  val names = List("John", "Tom", "Sophia", "Jeremy", "Hannah", "Clara", "Dennis", "Chloe")
  val surnames = List("SMITH", "JONES", "WILLIAMS", "DAVIES", "BROWN", "FITZGERALD", "WRIGHT", "LEWIS")

  def generateCitizen(): Citizen = {
    // Generate a first and last name by choosing them at random
    val randName = names(Random.nextInt(names.length))
    val randSurname = surnames(Random.nextInt(surnames.length))
    val person = s"${randName} ${randSurname}"
    val peacescore = Random.nextInt(6)

    Citizen(person, peacescore)
  }

}
