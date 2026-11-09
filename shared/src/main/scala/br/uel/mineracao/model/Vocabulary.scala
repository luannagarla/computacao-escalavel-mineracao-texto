package br.uel.escalavel.model

/**
 * Vocabulário produzido por um algoritmo.
 *
 * A posição de cada termo em `terms` é o índice usado nos vetores esparsos.
 * A construção do vocabulário (tokenização, remoção de duplicatas, ordenação
 * e atribuição de índices) é responsabilidade de cada algoritmo; este tipo
 * apenas transporta o resultado.
 *
 * @param terms termos na ordem em que os índices foram atribuídos.
 */
final case class Vocabulary(terms: IndexedSeq[String]) {

  /** Quantidade de termos distintos no vocabulário. */
  def size: Int = terms.size
}
