import sbt._

object Version {
  val akka = "2.4.0-RC2"
  val akkaHttp = "1.0"
  val akkaPersistence = "2.4.0-RC2"
  val levelDB = "0.7"
  val levelDBjni = "1.8"

  val scalaTest = "2.2.4"
}

object Library {
  val akka =  "com.typesafe.akka" %% "akka-actor" % Version.akka
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % Version.akkaHttp
  val akkaHttpJson = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % Version.akkaHttp
  val akkaPersistence =  "com.typesafe.akka" %% "akka-persistence" % Version.akkaPersistence
  val levelDB = "org.iq80.leveldb" % "leveldb" % Version.levelDB
  val levelDBjni = "org.fusesource.leveldbjni" % "leveldbjni-all" % Version.levelDBjni

  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Version.akka % "test"
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit-experimental" % Version.akkaHttp
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"

}

object Dependencies {
  import Library._

  val app = Seq(akkaHttp, akkaHttpJson, akkaHttpTestKit, scalaTest)
  val core = Seq(akka, akkaPersistence, levelDB, levelDBjni, akkaTestKit, scalaTest)
}
