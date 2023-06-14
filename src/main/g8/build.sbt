// give the user a nice default project!
ThisBuild / organization := "$package$"
ThisBuild / scalaVersion := "3.3.0"

$if(add_http_server.truthy) $
libraryDependencies += "dev.zio" %% "zio" % "2.0.15"
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC2"
$endif$

lazy val root = (project in file(".")).settings(
  name := "$name$"
)

libraryDependencies += "nl.vroste" %% "zio-amqp" % "0.4.0"
