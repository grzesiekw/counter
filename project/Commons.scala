import sbt._
import Keys._

object Commons {
  val commonSettings = Seq(
    organization := "counter",
    version := "1.0",

    scalaVersion := "2.11.7",
    scalacOptions := Seq(
      "-feature",
      "-language:postfixOps"
    ),

    fork in Test := true
  )
}
