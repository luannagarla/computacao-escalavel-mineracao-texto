package br.uel.mineracao.model

import org.apache.spark.rdd.RDD

/**
 * Saída comum aos dois algoritmos.
 *
 * O vocabulário é único para o corpus e cada vetor é identificado pelo mesmo
 * `Long` recebido na entrada. Esse é o formato que o `runner` usa para
 * escrever os resultados e para comparar os algoritmos de forma justa.
 *
 * @param vocabulary vocabulário construído pelo algoritmo.
 * @param vectors    um vetor esparso por documento, indexado pelo id do documento.
 */
final case class VectorizationResult(
    vocabulary: Vocabulary,
    vectors: RDD[(Long, SparseVector)]
)
