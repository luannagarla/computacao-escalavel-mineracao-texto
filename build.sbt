// Build multi-módulo do trabalho de Computação Escalável.
//
// shared          -> contrato comum (trait), tipos de saída, leitura de CSV e SparkSession
// countvectorizer -> implementação do CountVectorizer
// tfidf           -> implementação do TF-IDF
// runner          -> ponto de entrada único usado pelos dois algoritmos
//
// Os dois algoritmos são implementados pelo grupo; aqui só existe a estrutura.

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / organization := "br.uel"
ThisBuild / version := "0.1.0"

// Spark 4.1.x é a linha estável atual e suporta Scala 2.13 e Java 17/21.
val sparkVersion = "4.1.2"
val scalaTestVersion = "3.2.19"

lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

// A partir do Java 17 a JVM é modular e o Spark precisa destas permissões.
// O mesmo conjunto é usado em `run` e em `test` para o ambiente ser idêntico.
lazy val sparkJvmOptions = Seq(
  "-Xmx4g",
  "-Dfile.encoding=UTF-8",
  "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
  "--add-opens=java.base/java.io=ALL-UNNAMED",
  "--add-opens=java.base/java.net=ALL-UNNAMED",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
  "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
  "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"
)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked"
  ),
  libraryDependencies ++= sparkDependencies ++ testDependencies,
  // Rodar a aplicação em uma JVM separada deixa o heap e as flags do Spark
  // explícitos e evita que o processo do sbt interfira nas medições.
  Compile / run / fork := true,
  Compile / run / javaOptions ++= sparkJvmOptions,
  // Testes com Spark precisam de JVM separada; executá-los em paralelo
  // criaria várias SparkSessions disputando a mesma máquina.
  Test / fork := true,
  Test / javaOptions ++= sparkJvmOptions,
  Test / parallelExecution := false
)

lazy val shared = (project in file("shared"))
  .settings(commonSettings)
  .settings(name := "shared")

lazy val countvectorizer = (project in file("countvectorizer"))
  .dependsOn(shared)
  .settings(commonSettings)
  .settings(name := "countvectorizer")

lazy val tfidf = (project in file("tfidf"))
  .dependsOn(shared)
  .settings(commonSettings)
  .settings(name := "tfidf")

lazy val runner = (project in file("runner"))
  .dependsOn(shared, countvectorizer, tfidf)
  .settings(commonSettings)
  .settings(
    name := "runner",
    Compile / mainClass := Some("br.uel.mineracao.runner.BenchmarkApp")
  )

lazy val root = (project in file("."))
  .aggregate(shared, countvectorizer, tfidf, runner)
  .settings(
    name := "computacao-escalavel-mineracao-texto",
    publish / skip := true
  )
