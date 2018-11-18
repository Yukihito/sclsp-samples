name := "sclsp-samples"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.7"

resolvers += "Sclsp Maven Repository" at "http://yukihito.github.io/sclsp"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.yukihitoho" % "sclsp_2.12" % "0.2"
libraryDependencies += "org.jline" % "jline" % "3.9.0"
libraryDependencies += "io.lettuce" % "lettuce-core" % "5.1.2.RELEASE"
val circeVersion = "0.10.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

scalacOptions ++= Seq("-deprecation", "-feature")
