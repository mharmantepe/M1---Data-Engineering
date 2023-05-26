ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Producer",
    libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.5",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.5",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.1",
    libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.1",
    libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.2.1",
    libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.1.2",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.14",
    libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.2.4",
    libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "3.2.4"

  )
