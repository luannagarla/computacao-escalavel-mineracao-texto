# Mineração de Texto de Modo Escalável

Projeto da disciplina de **Computação Escalável** do curso de **Ciência de Dados e
Inteligência Artificial** da Universidade Estadual de Londrina (UEL).

O trabalho consiste em implementar **dois algoritmos de mineração de texto** —
**CountVectorizer** e **TF-IDF** — sobre o **Apache Spark**, e analisar a
escalabilidade de cada um conforme o volume de documentos aumenta.

> **Regra do enunciado:** os algoritmos devem ser implementados pelo grupo.
> Não use o `CountVectorizer`/`IDF` do Spark MLlib; o objetivo é implementar a
> lógica com RDDs. O que existe neste repositório é apenas o *scaffolding*
> (estrutura, contrato comum e infraestrutura de leitura/execução).

---

## Estado atual

- [x] Estrutura multi-módulo do sbt
- [x] Contrato de execução e de saída comum aos dois algoritmos
- [x] Leitura de CSV e criação da `SparkSession`
- [x] Dependências de teste (ScalaTest) e flags de JVM para o Spark
- [ ] Implementação do CountVectorizer (`countvectorizer/...`)
- [ ] Implementação do TF-IDF (`tfidf/...`)
- [ ] Runner de experimentos (`runner/...`)
- [ ] Testes dos algoritmos
- [ ] Scripts de geração de datasets, execução e gráficos

Os pontos pendentes estão marcados com `NotImplementedError` e/ou `TODO`.
Os arquivos de pseudocódigo ficam em [`docs/pseudocode/`](docs/pseudocode/).

---

## Stack

| Item | Versão |
|---|---|
| Scala | 2.13.18 |
| Apache Spark | 4.1.2 (`spark-core` + `spark-sql`) |
| sbt | 1.11.7 |
| JDK | 17 ou 21 (recomendado: **21**) |
| Testes | ScalaTest 3.2.19 |

O Spark roda em modo **local** (`local[*]` por padrão), sem necessidade de
cluster ou Docker. Caso o grupo queira montar master/worker com containers,
isso pode ser adicionado depois; o modo local é suficiente para os
experimentos exigidos.

### Pré-requisitos

```bash
java -version   # precisa ser 17 ou 21
sbt --version   # 1.11.7
```

O sbt usa o `JAVA_HOME` para escolher a JVM. Se houver mais de um JDK
instalado, aponte para o 21 antes de rodar:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

---

## Estrutura do repositório

```text
.
├── build.sbt                     # módulos, dependências e flags de JVM
├── project/build.properties      # versão do sbt
├── shared/                       # código comum aos dois algoritmos
│   └── src/main/scala/br/uel/mineracao/
│       ├── TextMiningAlgorithm.scala     # interface única
│       ├── model/
│       │   ├── Vocabulary.scala
│       │   ├── SparseVector.scala
│       │   └── VectorizationResult.scala # mesmo formato de saída
│       ├── io/CsvDocumentReader.scala    # leitura inicial do CSV
│       └── spark/SparkSessionFactory.scala
├── countvectorizer/              # algoritmo 1 (implementar)
│   ├── src/main/scala/br/uel/mineracao/countvectorizer/
│   │   └── CountVectorizerAlgorithm.scala
│   └── src/test/scala/...        # testes do algoritmo 1
├── tfidf/                        # algoritmo 2 (implementar)
│   ├── src/main/scala/br/uel/mineracao/tfidf/
│   │   └── TfIdfAlgorithm.scala
│   └── src/test/scala/...        # testes do algoritmo 2
├── runner/                       # execução/medição comum (implementar)
│   └── src/main/scala/br/uel/mineracao/runner/
│       └── BenchmarkApp.scala
├── docs/pseudocode/              # pseudocódigo do grupo (não compilado)
├── data/                         # datasets (não versionados)
├── scripts/                      # automação dos experimentos (a implementar)
└── results/                      # tempos e gráficos gerados (não versionados)
```

Cada módulo segue o padrão do sbt/Maven:

```text
<módulo>/src/main/scala/<pacote>/   # código de produção
<módulo>/src/test/scala/<pacote>/   # testes
```

Arquivos fora de `src/main/scala` **não são compilados**. Por isso o
pseudocódigo fica em `docs/` e não dentro do módulo.

As decisões de estruturação (responsabilidade de cada módulo, contrato,
escolhas de build, convenções e o que pode ser revisto) estão detalhadas em
[`docs/estrutura-projeto.md`](docs/estrutura-projeto.md).

---

## Como compilar e testar

```bash
# compila todos os módulos
sbt compile

# compila os testes (ainda não há testes implementados)
sbt Test/compile

# roda os testes
sbt test
```

O primeiro `sbt compile` baixa o Spark e as dependências; pode demorar alguns
minutos. Depois disso, o build incremental é rápido.

---

## Contrato comum aos dois algoritmos

O enunciado exige **a mesma interface de execução** e **o mesmo formato de
saída** para os dois algoritmos. Isso é garantido pelo tipo
[`TextMiningAlgorithm`](shared/src/main/scala/br/uel/mineracao/TextMiningAlgorithm.scala):

```scala
def fitTransform(documents: RDD[(Long, String)]): VectorizationResult
```

- **Entrada:** `(id do documento, texto)`.
- **Saída:** `VectorizationResult`, com:
  - um `Vocabulary` único para o corpus (a posição do termo é o índice usado
    nos vetores);
  - um `SparseVector` por documento, com índices e valores `Double`.

`Double` foi escolhido para que o mesmo formato sirva para contagens
(CountVectorizer) e pesos (TF-IDF). O `Long` de entrada deve ser preservado na
saída para que cada vetor continue associado ao documento original.

O `runner` depende dos dois módulos de algoritmo e deve tratá-los apenas por
essa interface. Assim, qualquer mudança de assinatura em `shared` afeta os
dois lados e aparece imediatamente na compilação.

---

## Dados

Coloque os CSVs em `data/` (o conteúdo é ignorado pelo git; veja
[`data/README.md`](data/README.md)). Formato esperado:

```csv
id,texto
1,"primeiro documento de exemplo"
2,"segundo documento, com vírgulas dentro do campo"
```

- A coluna de texto deve se chamar `texto` (configurável em
  `CsvDocumentReader.readTexts`/`readDocuments`).
- Outras colunas são ignoradas.
- O leitor usa o cabeçalho por padrão (`hasHeader = true`).
- Os ids usados pelos algoritmos são atribuídos por posição
  (`zipWithIndex`), não pela coluna `id`.

Requisitos do enunciado:

- **2 datasets distintos** (um real e um sintético, ou dois sintéticos com
  distribuições diferentes);
- **10.000 a 150.000 documentos**;
- pelo menos **5 pontos** de medição, variando o tamanho (ideal: de 10k em
  10k, até 150k).

---

## Protocolo de medição (importante para o relatório)

Para que a comparação entre os algoritmos seja justa:

1. **Mesma máquina, mesmos recursos.** Use o mesmo `master` (por exemplo,
   `local[4]`) e o mesmo heap (`-Xmx4g`, definido em `build.sbt`) nos dois
   algoritmos.
2. **Inicialização fora do cronômetro.** Crie a `SparkSession` antes de
   começar a medir. O mesmo vale para a leitura do CSV: leia, faça
   `cache()` e force com `count()` antes de iniciar o cronômetro.
3. **RDDs são preguiçosos.** O tempo do algoritmo só acontece quando uma
   *action* é executada. O cronômetro deve envolver o `fitTransform` **e** uma
   ação que materialize o resultado (por exemplo, `result.vectors.count()`).
4. **Não meça a escrita da saída.** Grave o tempo antes de escrever arquivos.
5. Use `System.nanoTime()` e converta para milissegundos/segundos.
6. **Isole os processos.** Rode cada ponto em uma JVM nova e não use o
   computador durante os testes.
7. **Fixe o paralelismo do shuffle.** O padrão do Spark é
   `spark.sql.shuffle.partitions = 200`; para datasets pequenos isso pode
   dominar o tempo. Defina explicitamente o mesmo valor nos dois algoritmos
   via `SparkSessionFactory.local(..., config = Map(...))`.
8. Se fizer aquecimento (*warm-up*), descarte a primeira execução e meça as
   seguintes.

---

## Onde implementar

| O que | Arquivo |
|---|---|
| CountVectorizer | `countvectorizer/src/main/scala/br/uel/mineracao/countvectorizer/CountVectorizerAlgorithm.scala` |
| TF-IDF | `tfidf/src/main/scala/br/uel/mineracao/tfidf/TfIdfAlgorithm.scala` |
| Execução e medição comuns | `runner/src/main/scala/br/uel/mineracao/runner/BenchmarkApp.scala` |
| Testes | `*/src/test/scala/...` |
| Scripts de experimentos | `scripts/` (a criar) |

Depois de implementar, o runner pode ser executado com:

```bash
sbt "runner/run countvectorizer data/dataset-a.csv local[4]"
```

O formato exato dos argumentos é definido pelo grupo ao implementar o
`BenchmarkApp`; o comando acima é apenas uma sugestão.

---

## Scripts de escalabilidade

A pasta [`scripts/`](scripts/README.md) está reservada para a automação
exigida no enunciado (geração de datasets, execução dos pontos de medição e
geração dos gráficos). **Nenhum script foi implementado neste scaffolding.**

---

## Uso de IA generativa

Conforme a seção 5.1 do enunciado, declaramos explicitamente:

- **Foi utilizada IA apenas para o scaffolding e o setup do projeto**, a saber:
  - `build.sbt`, `project/build.properties`, `.gitignore`, `.scalafmt.conf`;
  - este `README.md`, `data/README.md`, `scripts/README.md` e
    `docs/pseudocode/README.md`;
  - a estrutura de diretórios e os *stubs* com as assinaturas dos algoritmos
    (sem lógica de algoritmo);
  - `shared/.../io/CsvDocumentReader.scala` (leitura inicial do arquivo) e
    `shared/.../spark/SparkSessionFactory.scala` (setup do Spark), que o
    enunciado permite expressamente;
  - os tipos de dados em `shared/.../model/` e a interface
    `TextMiningAlgorithm` (contrato, não implementação).
- **Não foi utilizada IA** para implementar a lógica do CountVectorizer, do
  TF-IDF, os testes dos algoritmos ou os scripts de experimentos.
- O pseudocódigo em `docs/pseudocode/` foi produzido pelo grupo.

Se o grupo usar IA em qualquer outra parte, **atualize esta seção** e o
relatório, indicando o que foi usado e em quais arquivos/funções.

---

## Próximos passos sugeridos

1. Implementar o CountVectorizer e o TF-IDF (uma dupla por algoritmo).
2. Criar testes pequenos com resultados calculáveis à mão.
3. Gerar os dois datasets e colocá-los em `data/`.
4. Implementar o `BenchmarkApp` e os scripts de execução.
5. Rodar os experimentos na mesma máquina e gerar os gráficos.
6. Escrever o relatório e a apresentação.
