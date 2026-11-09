package br.uel.escalavel

import br.uel.escalavel.model.VectorizationResult
import org.apache.spark.rdd.RDD

/**
 * Interface única que os dois algoritmos do trabalho devem implementar.
 *
 * O enunciado exige que CountVectorizer e TF-IDF rodem sob a mesma
 * interface de execução e produzam o mesmo formato de saída. Manter esse
 * contrato em `shared` garante isso em tempo de compilação: o `runner`
 * consegue tratar os dois algoritmos de forma polimórfica.
 *
 * As implementações ficam em:
 *   - `countvectorizer/.../CountVectorizerAlgorithm.scala`
 *   - `tfidf/.../TfIdfAlgorithm.scala`
 */
trait TextMiningAlgorithm {

  /** Nome curto usado em relatórios, gráficos e logs. */
  def name: String

  /**
   * Ajusta o vocabulário e transforma os documentos em vetores esparsos.
   *
   * A entrada já vem identificada por um `Long`, porque o resultado precisa
   * continuar associado ao documento original. O contrato de saída é o mesmo
   * para os dois algoritmos ([[VectorizationResult]]).
   *
   * @param documents pares `(id do documento, texto)`.
   * @return vocabulário construído e um vetor esparso por documento.
   */
  def fitTransform(documents: RDD[(Long, String)]): VectorizationResult
}
