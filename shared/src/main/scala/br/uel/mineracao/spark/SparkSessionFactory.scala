package br.uel.escalavel.spark

import org.apache.spark.sql.SparkSession

/**
 * Criação centralizada da `SparkSession`.
 *
 * Nos experimentos, a sessão deve ser criada **antes** de começar a medir o
 * tempo: a inicialização do Spark não faz parte do algoritmo e não pode
 * entrar no gráfico de escalabilidade.
 *
 * O modo padrão é `local[*]`, que usa todos os núcleos da máquina. Para os
 * testes comparativos, use o mesmo valor de `master` (por exemplo,
 * `local[4]`) e o mesmo `-Xmx` para os dois algoritmos.
 */
object SparkSessionFactory {

  /**
   * Cria (ou reaproveita) uma sessão local.
   *
   * @param appName nome exibido na Spark UI e nos logs.
   * @param master  master do Spark; `local[k]` fixa o número de threads.
   * @param config  configurações adicionais, por exemplo
   *                `Map("spark.sql.shuffle.partitions" -> "8")`.
   */
  def local(
      appName: String = "mineracao-texto",
      master: String = "local[*]",
      config: Map[String, String] = Map.empty
  ): SparkSession = {
    val builder = SparkSession
      .builder()
      .appName(appName)
      .master(master)

    config
      .foldLeft(builder) { case (current, (key, value)) => current.config(key, value) }
      .getOrCreate()
  }
}
