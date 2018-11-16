name := "sclsp-samples"

version := "0.1"

scalaVersion := "2.12.7"

resolvers += "Sclsp Maven Repository" at "http://yukihito.github.io/sclsp"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.yukihitoho" % "sclsp_2.12" % "0.1"
libraryDependencies += "org.jline" % "jline" % "3.9.0"
scalacOptions ++= Seq("-deprecation", "-feature")
