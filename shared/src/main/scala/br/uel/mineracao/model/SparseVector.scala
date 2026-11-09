package br.uel.escalavel.model

/**
 * Vetor esparso de um documento.
 *
 * `indices(i)` é o índice de um termo no vocabulário e `values(i)` é o valor
 * associado a esse termo. Os dois vetores devem ter o mesmo tamanho. A ordem
 * dos índices é escolhida pela implementação, mas recomenda-se mantê-los
 * crescentes para que a saída seja estável e fácil de comparar.
 *
 * O valor é `Double` para que o mesmo tipo sirva ao CountVectorizer
 * (contagens) e ao TF-IDF (pesos), mantendo o formato de saída idêntico.
 *
 * @param indices índices dos termos presentes no documento.
 * @param values  valores correspondentes a cada índice.
 */
final case class SparseVector(indices: IndexedSeq[Int], values: IndexedSeq[Double]) {
  require(
    indices.length == values.length,
    s"indices e values devem ter o mesmo tamanho (recebido: ${indices.length} e ${values.length})."
  )
  require(
    indices.forall(_ >= 0),
    "Os índices do vetor esparso não podem ser negativos."
  )

  /** Quantidade de termos não nulos do documento. */
  def nnz: Int = indices.length
}
