name := "crawler"

version := "1.0"

lazy val `crawler` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc, cache, ws,
  specs2 % Test,
  "org.scala-lang" % "scala-reflect" % "2.11.2",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.42.2",
  "org.jsoup" % "jsoup" % "1.7.3",
  "us.codecraft" % "xsoup" % "0.3.0",
  "org.scalaz" %% "scalaz-core" % "7.1.1")

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  