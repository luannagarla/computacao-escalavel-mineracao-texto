package br.uel.mineracao.io

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Responsável apenas pela leitura inicial da base de textos em CSV.
 *
 * O arquivo é interpretado pelo leitor de CSV do Spark, que trata
 * corretamente vírgulas e quebras de linha dentro de campos entre aspas.
 * Campos entre aspas seguem a convenção RFC 4180, inclusive aspas duplicadas
 * (`""`). A coluna de texto é então exposta como um RDD, que é o formato
 * consumido pelos algoritmos (CountVectorizer e TF-IDF).
 *
 * A `SparkSession` é recebida do chamador para que a criação da sessão não
 * seja contabilizada no tempo de execução medido nos experimentos.
 *
 * Dica para os experimentos: um RDD é preguiçoso. Para que a leitura não
 * entre no tempo do algoritmo, materialize a entrada antes de ligar o
 * cronômetro (por exemplo, `documents.cache()` seguido de
 * `documents.count()`).
 */
object CsvDocumentReader {

  /** Nome padrão da coluna que contém os documentos. */
  val DefaultTextColumn: String = "texto"

  /**
   * Lê o CSV indicado e retorna os valores da coluna de texto como RDD.
   *
   * @param spark      sessão usada para ler o arquivo; quem chama é dono do
   *                   seu ciclo de vida
   * @param path       arquivo CSV, diretório com vários CSVs ou glob
   * @param textColumn nome da coluna que contém os documentos
   * @param hasHeader  se a primeira linha contém os nomes das colunas
   * @param multiLine  se os documentos podem conter quebras de linha dentro de
   *                   campos entre aspas (padrão). Desabilitado, o Spark pode
   *                   dividir o arquivo entre as partições, o que ajuda a
   *                   paralelizar a leitura de arquivos com um documento por
   *                   linha.
   */
  def readTexts(
      spark: SparkSession,
      path: String,
      textColumn: String = DefaultTextColumn,
      hasHeader: Boolean = true,
      multiLine: Boolean = true
  ): RDD[String] = {
    val frame = readCsv(spark, path, hasHeader, multiLine)
    val textIndex = columnIndex(frame, path, textColumn)
    // Campos vazios viram null no leitor do Spark; para o algoritmo um
    // documento sem texto é simplesmente uma string vazia.
    frame.rdd.map(row => Option(row.getString(textIndex)).getOrElse(""))
  }

  /**
   * Igual a [[readTexts]], mas mantém a posição de cada documento.
   *
   * O identificador é a posição do documento no RDD, atribuída com
   * `zipWithIndex` (contígua e começando em zero). Isso permite associar
   * cada vetor gerado ao documento que o originou.
   */
  def readDocuments(
      spark: SparkSession,
      path: String,
      textColumn: String = DefaultTextColumn,
      hasHeader: Boolean = true,
      multiLine: Boolean = true
  ): RDD[(Long, String)] =
    readTexts(spark, path, textColumn, hasHeader, multiLine)
      .zipWithIndex()
      .map { case (text, index) => (index, text) }

  private def readCsv(
      spark: SparkSession,
      path: String,
      hasHeader: Boolean,
      multiLine: Boolean
  ): DataFrame = {
    require(spark != null, "A SparkSession não pode ser nula.")
    require(path != null && path.trim.nonEmpty, "O caminho do arquivo CSV não pode ser vazio.")

    spark.read
      .option("header", hasHeader.toString)
      .option("multiLine", multiLine.toString)
      .option("escape", "\"")
      .option("encoding", "UTF-8")
      .csv(path)
  }

  private def columnIndex(frame: DataFrame, path: String, textColumn: String): Int = {
    require(
      textColumn != null && textColumn.trim.nonEmpty,
      "O nome da coluna de texto não pode ser vazio."
    )

    val columns = frame.schema.fieldNames
    if (!columns.contains(textColumn)) {
      throw new IllegalArgumentException(
        s"Coluna de texto '$textColumn' não encontrada em '$path'. " +
          s"Colunas disponíveis: ${columns.mkString(", ")}."
      )
    }

    frame.schema.fieldIndex(textColumn)
  }
}
