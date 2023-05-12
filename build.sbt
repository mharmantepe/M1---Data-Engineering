ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.8"

lazy val root = (project in file("."))
  .settings(
    name := "test",
    libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.5",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.5",
    libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.2.1",
    libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.1",
    libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.2.1"
  )


