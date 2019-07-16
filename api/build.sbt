name := """fringe81-intern"""
organization := "yoshiki"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "yoshiki.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "yoshiki.binders._"


libraryDependencies += jdbc
libraryDependencies += evolutions

// anrom - https://playframework.github.io/anorm/
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2"

// h2 database - https://www.h2database.com/html/main.html
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
