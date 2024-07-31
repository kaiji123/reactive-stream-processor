name := "ReactiveStreamProcessing"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.20",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  "org.apache.kafka" % "kafka-clients" % "2.7.0",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
  "org.influxdb" % "influxdb-java" % "2.21",
  "com.influxdb" % "influxdb-client-scala_2.13" % "3.4.0"

)
