name := """scalink"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.1"
libraryDependencies += "org.flywaydb" %% "flyway-play" % "4.0.0"

libraryDependencies ++= Seq( 
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "bootstrap" % "3.3.7-1",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41" ,
  "com.github.t3hnar" %% "scala-bcrypt" % "3.0"
)

// Testing Deps
libraryDependencies ++= Seq( 
 specs2 % Test,
 "com.h2database" % "h2" % "1.4.194" % Test,
 "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
)

javaOptions in Test += "-Dconfig.resource=application.test.conf"
javaOptions in Test += "-Denv=test"
