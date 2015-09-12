import Commons._
import Dependencies._

name := "counter"

lazy val counterRoot = (project in file(".")).settings(commonSettings).aggregate(counterCore, counterApp)

lazy val counterCore = (project in file("counter-core")).settings(commonSettings).settings(libraryDependencies ++= core)

lazy val counterApp = (project in file("counter-app")).settings(commonSettings).settings(libraryDependencies ++= app).
  dependsOn(counterCore)

triggeredMessage := Watched.clearWhenTriggered