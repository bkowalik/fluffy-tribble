scalaVersion := "2.13.8"

name := "interview"

organization := "example.com"

val akkaHttp = {
  val version = "10.2.8"
  Seq(
    "com.typesafe.akka" %% "akka-http" % version,
    "com.typesafe.akka" %% "akka-http-testkit" % version % "test,it"
  )
}

val akkaStream = {
  val version = "2.6.18"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % version,
    "com.typesafe.akka" %% "akka-stream-testkit" % version % "test,it"
  )
}

val circe = {
  val version = "0.14.1"
  Seq(
    "io.circe" %% "circe-core" % version,
    "io.circe" %% "circe-generic" % version,
    "io.circe" %% "circe-parser" % version,
    "io.circe" %% "circe-generic-extras" % version
  )
}

val enumeratum = {
  val version = "1.7.0"
  Seq(
    "com.beachape" %% "enumeratum" % version,
    "com.beachape" %% "enumeratum-circe" % version
  )
}

configs(IntegrationTest)

Defaults.itSettings

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "org.scalatest" %% "scalatest" % "3.2.11" % "test,it"
) ++ akkaHttp ++ akkaStream ++ circe ++ enumeratum
