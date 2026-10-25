package br.uel.mineracao.runner

/**
 * Ponto de entrada único dos experimentos.
 *
 * TODO (grupo): implementar o fluxo descrito abaixo. Este arquivo é apenas o
 * esqueleto do runner; ele não contém a medição nem a lógica dos algoritmos.
 *
 * Fluxo esperado:
 *  1. Ler os argumentos: algoritmo (`countvectorizer` ou `tfidf`), caminho do
 *     CSV, `master` do Spark e, opcionalmente, arquivo de saída.
 *  2. Criar a `SparkSession` com [[br.uel.mineracao.spark.SparkSessionFactory]]
 *     ANTES de qualquer medição.
 *  3. Ler os documentos com [[br.uel.mineracao.io.CsvDocumentReader]] e
 *     materializar a entrada (`cache()` + `count()`) antes do cronômetro.
 *  4. Iniciar o cronômetro, chamar `fitTransform` e forçar uma ação sobre os
 *     vetores (por exemplo, `count()`), pois RDDs são preguiçosos.
 *  5. Parar o cronômetro, escrever o tempo e o resultado no formato comum aos
 *     dois algoritmos e encerrar a sessão.
 *
 * A comparação justa exige que os dois algoritmos usem a mesma `master`
 * (mesmo número de threads) e o mesmo heap.
 */
object BenchmarkApp {

  def main(args: Array[String]): Unit =
    throw new NotImplementedError(
      "Implemente BenchmarkApp.main: leitura de argumentos, medição e escrita da saída."
    )
}
