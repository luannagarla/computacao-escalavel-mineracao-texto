ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "edu.countvectorizer"

lazy val root = (project in file("."))
  .settings(
    name := "countvectorizer-spark",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "4.0.4",
      "org.apache.spark" %% "spark-sql"  % "4.0.4"
    ),

    Compile / run / fork := true
  )