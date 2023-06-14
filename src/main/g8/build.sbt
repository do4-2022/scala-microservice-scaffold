// give the user a nice default project!
ThisBuild / organization := "$package$"
ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file(".")).settings(
  name := "$name$"
)

$if(add_message_queue.truthy)$
libraryDependencies += "nl.vroste" %% "zio-amqp" % "0.4.0"
$endif$