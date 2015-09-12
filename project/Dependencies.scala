import sbt._

object Version {
  val akka = "2.4.0-RC2"
  val akkaHttp = "1.0"
  val akkaPersistence = "2.4.0-RC2"

  val scalaTest = "2.2.4"
}

object Library {
  val akka =  "com.typesafe.akka" %% "akka-actor" % Version.akka
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % Version.akkaHttp
  val akkaPersistence =  "com.typesafe.akka" %% "akka-persistence" % Version.akkaPersistence

  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Version.akka % "test"
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit-experimental" % Version.akkaHttp
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"

}

object Dependencies {
  import Library._

  val core = Seq(akka, akkaHttp, akkaPersistence, akkaTestKit, scalaTest)
}
