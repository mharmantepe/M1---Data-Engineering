package client.models

import scala.util.Random
case class Drone(droneId : Int, latitude : Double, longitude : Double)

object Drones {
  def generateDrone(): Drone = {
    val droneId = 1 + Random.nextInt(5)
    val longitude = Random.nextDouble() * 360 - 180
    val latitude = Random.nextDouble() * 180 - 90

    Drone(droneId, longitude, latitude)
  }
}
