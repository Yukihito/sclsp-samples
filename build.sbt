name := "sclsp-samples"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "default" % "sclsp_2.12" % "0.1-SNAPSHOT"
libraryDependencies += "org.jline" % "jline" % "3.9.0"
scalacOptions ++= Seq("-deprecation", "-feature")
