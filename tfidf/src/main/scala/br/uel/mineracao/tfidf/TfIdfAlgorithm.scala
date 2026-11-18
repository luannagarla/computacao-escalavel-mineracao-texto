package br.uel.mineracao.tfidf

import br.uel.mineracao.TextMiningAlgorithm
import br.uel.mineracao.model.{Vocabulary, SparseVector, VectorizationResult}
import org.apache.spark.rdd.RDD

/** Implementação do TF-IDF sobre RDDs.
  *
  * TODO (grupo): implementar `fitTransform` de acordo com o pseudocódigo que será colocado em
  * `docs/pseudocode/tfidf/`. Nenhuma lógica do algoritmo foi gerada por IA: este arquivo contém
  * apenas o contrato e o ponto de entrada.
  *
  * Requisitos do contrato:
  *   - calcular a frequência do termo e o inverso da frequência nos documentos usando
  *     transformações/actions de RDD;
  *   - construir um único vocabulário para o corpus;
  *   - devolver um [[br.uel.escalavel.model.SparseVector]] por documento, com os índices do
  *     vocabulário e os pesos como `Double`.
  */
final class TfIdfAlgorithm extends TextMiningAlgorithm with Serializable {

  override val name: String = "TF-IDF"

  // Mesma tokenizacao do Count Vectorizer
  def tokenize(text: String): Seq[String] = {
    Option(text)
      .getOrElse("")
      .toLowerCase
      .split("[^\\p{L}\\p{N}]+")
      .filter(_.nonEmpty)
      .toSeq
  }

  override def fitTransform(documents: RDD[(Long, String)]): VectorizationResult = {
    val tokens: RDD[((Long, String), Int)] = documents.flatMap { case (id, text) =>
      tokenize(text).map(word => ((id, word), 1))
    }

    val tf: RDD[((Long, String), Int)] = tokens.reduceByKey(_ + _).cache()

    val df = tf.map { case ((id, term), count) => (term, 1) }.reduceByKey(_ + _)

    val n = documents.count()

    val dfMap = df.collectAsMap()

    val vocab = dfMap.keys.toVector.sorted

    println(s"n=$n")
    println(s"dfMap=$dfMap")
    println(s"vocab=$vocab")

    throw new NotImplementedError(
      "Implemente TfIdfAlgorithm.fitTransform. Veja docs/pseudocode/tfidf/."
    )
  }
}
