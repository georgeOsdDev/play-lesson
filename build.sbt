name := """play-lesson"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

lazy val customResovers = Seq(
)

libraryDependencies += filters

libraryDependencies += cache

libraryDependencies += "org.webjars" %% "webjars-play" % "2.3.0-2"

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.2-2"

libraryDependencies += "org.webjars" % "bootswatch-cerulean" % "3.3.1+2"

libraryDependencies += "org.webjars" % "jquery" % "2.1.3"

libraryDependencies += "org.slf4s" %% "slf4s-api" % "1.7.7"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.6"

libraryDependencies += "org.javassist" % "javassist" % "3.18.2-GA"

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "2.12.4"

libraryDependencies += "org.mongodb.morphia" % "morphia" % "0.110"

libraryDependencies += "com.hazelcast" % "hazelcast" % "3.3.3"

libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "3.1.0"
