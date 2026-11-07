package br.uel.mineracao.countvectorizer

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

// para executar os testes, rode no terminal o trecho -> sbt "testOnly br.uel.mineracao.countvectorizer.CountVectorizerAlgorithmSpec"

class CountVectorizerAlgorithmSpec extends AnyFunSuite with BeforeAndAfterAll {

  private var sparkContext: SparkContext = _

  override def beforeAll(): Unit = {
    val configuration = new SparkConf()
      .setMaster("local[2]")
      .setAppName("CountVectorizerTests")
      .set("spark.ui.enabled", "false")

    sparkContext = new SparkContext(configuration)
  }

  override def afterAll(): Unit = {
    if (sparkContext != null) {
      sparkContext.stop()
    }
  }

  test("teste com número, acentuação e caractere especial") {
    val documents = sparkContext.parallelize(
      Seq(
        (1L, "luanna é 10 !!!")
      )
    )

    val algorithm = new CountVectorizerAlgorithm()
    val result = algorithm.fitTransform(documents)

    println("Vocabulário: " + result.vocabulary.terms.mkString(", "))

    assert(
      result.vocabulary.terms == Vector("10", "luanna", "é")
    )
  }

  test("teste de construir o vocabulário com dois documentos e espaços em branco") {

    val documents = sparkContext.parallelize(
      Seq(
        (1L, "gabi e luanna      fazem a parte 01"),
        (2L, "   keila e gui fazem  a parte 02  !!! ")
      )
    )

    val algorithm = new CountVectorizerAlgorithm()
    val result = algorithm.fitTransform(documents)

    println("Vocabulário: " + result.vocabulary.terms.mkString(", "))

    // a ordem do vocabulário é definida em ordem alfabética crescente
    assert(
      result.vocabulary.terms == Vector(
        "01",
        "02",
        "a",
        "e",
        "fazem",
        "gabi",
        "gui",
        "keila",
        "luanna",
        "parte"
      )
    )
  }

  test("teste com letras maiúsculas") {
    val documents = sparkContext.parallelize(
      Seq(
        (1L, "LUANNA É 10 !!!")
      )
    )

    val algorithm = new CountVectorizerAlgorithm()
    val result = algorithm.fitTransform(documents)

    println("Vocabulário: " + result.vocabulary.terms.mkString(", "))

    assert(
      result.vocabulary.terms == Vector("10", "luanna", "é")
    )
  }

  test("teste de contagem por documento") {
    val documents = sparkContext.parallelize(
      Seq(
        (1L, "luanna gabi luanna"),
        (2L, "keila keila luanna")
      )
    )

    val algorithm = new CountVectorizerAlgorithm()
    val result = algorithm.fitTransform(documents)
    val vectors = result.vectors.collect().toMap

    println("Vocabulário: " + result.vocabulary.terms.mkString(", "))

    assert(
      result.vocabulary.terms == Vector("gabi", "keila", "luanna")
    )

    val indices1L = Vector(0, 2)
    val values1L = Vector(1.0, 2.0)

    val indices2L = Vector(1, 2)
    val values2L = Vector(2.0, 1.0)

    assert(vectors(1L).indices == indices1L)
    assert(vectors(1L).values == values1L)

    assert(vectors(2L).indices == indices2L)
    assert(vectors(2L).values == values2L)
  }

  test("teste de contagem global") {
    val documents = sparkContext.parallelize(
      Seq(
        (1L, "luanna gabi luanna"),
        (2L, "keila keila luanna")
      )
    )

    val algorithm = new CountVectorizerAlgorithm()

    val count = algorithm
      .countGlobalVocabulary(documents)
      .collect()
      .toMap

    println("\nContagem global:")
    count.toSeq.sortBy(_._1).foreach { case (word, quantity) =>
      println(s"$word: $quantity")
    }

    assert(
      count == Map(
        "gabi" -> 1L,
        "keila" -> 2L,
        "luanna" -> 3L
      )
    )
  }
}
