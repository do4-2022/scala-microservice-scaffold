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

$if(add_message_queue.truthy)$
libraryDependencies += "nl.vroste" %% "zio-amqp" % "0.4.0"
$endif$

$if(add_sql_orm.truthy)$
libraryDependencies ++= Seq(
    "io.getquill"          %% "quill-jdbc-zio" % "4.6.0.1",
    "org.postgresql"       %  "postgresql"     % "42.5.4"
)
$endif$
