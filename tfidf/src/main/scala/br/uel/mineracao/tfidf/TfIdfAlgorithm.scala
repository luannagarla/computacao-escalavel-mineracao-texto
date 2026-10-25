package br.uel.mineracao.tfidf

import br.uel.mineracao.TextMiningAlgorithm
import br.uel.mineracao.model.VectorizationResult
import org.apache.spark.rdd.RDD

/**
 * Implementação do TF-IDF sobre RDDs.
 *
 * TODO (grupo): implementar `fitTransform` de acordo com o pseudocódigo que
 * será colocado em `docs/pseudocode/tfidf/`. Nenhuma lógica do algoritmo foi
 * gerada por IA: este arquivo contém apenas o contrato e o ponto de entrada.
 *
 * Requisitos do contrato:
 *  - calcular a frequência do termo e o inverso da frequência nos documentos
 *    usando transformações/actions de RDD;
 *  - construir um único vocabulário para o corpus;
 *  - devolver um [[br.uel.mineracao.model.SparseVector]] por documento,
 *    com os índices do vocabulário e os pesos como `Double`.
 */
final class TfIdfAlgorithm extends TextMiningAlgorithm {

  override val name: String = "TF-IDF"

  override def fitTransform(documents: RDD[(Long, String)]): VectorizationResult =
    throw new NotImplementedError(
      "Implemente TfIdfAlgorithm.fitTransform. Veja docs/pseudocode/tfidf/."
    )
}
