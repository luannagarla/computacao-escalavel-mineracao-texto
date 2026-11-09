# Estruturação do projeto

Este documento explica **como** e **por que** o repositório foi organizado
dessa forma. Ele complementa o [`README.md`](../README.md): o README ensina a
compilar e rodar; aqui estão as decisões de estrutura, as responsabilidades de
cada módulo e as convenções que o grupo deve seguir durante a implementação.

> Importante: este repositório contém apenas o *scaffolding*. A lógica do
> CountVectorizer, a lógica do TF-IDF, os testes dos algoritmos e os scripts
> de experimentos **não foram implementados**. Os pontos pendentes estão
> marcados com `TODO` e/ou `NotImplementedError`.

---

## 1. Objetivo da estrutura

O enunciado exige que os dois algoritmos:

1. sejam implementados pelo grupo sobre o Apache Spark;
2. rodem sob a **mesma interface de execução**;
3. produzam o **mesmo formato de saída**;
4. sejam comparados de forma justa, com o mesmo ambiente e a mesma forma de
   medição de tempo.

A estrutura foi desenhada para que essas exigências sejam garantidas **pelo
compilador**, e não apenas por disciplina do grupo. Se um algoritmo mudar a
assinatura ou o formato de saída, o outro lado deixa de compilar.

---

## 2. Visão geral dos módulos

O build tem quatro submódulos, além do projeto raiz que agrega tudo:

```text
                    ┌──────────────────────┐
                    │        runner        │
                    │ BenchmarkApp (main)  │
                    └──────────┬───────────┘
                               │ depende de
                ┌──────────────┴──────────────┐
                ▼                             ▼
      ┌──────────────────┐          ┌──────────────────┐
      │  countvectorizer │          │       tfidf      │
      │  algoritmo 1     │          │   algoritmo 2    │
      └────────┬─────────┘          └────────┬─────────┘
               │ depende de                  │ depende de
               └──────────────┬──────────────┘
                              ▼
                    ┌──────────────────────┐
                    │        shared        │
                    │ contrato, tipos, I/O │
                    └──────────────────────┘
```

| Módulo | Pasta | Responsabilidade | Status |
|---|---|---|---|
| `shared` | `shared/` | Contrato `TextMiningAlgorithm`, tipos de saída, leitura de CSV e criação da `SparkSession` | pronto |
| `countvectorizer` | `countvectorizer/` | Implementação do CountVectorizer | stub |
| `tfidf` | `tfidf/` | Implementação do TF-IDF | stub |
| `runner` | `runner/` | Ponto de entrada único: argumentos, medição de tempo e escrita da saída | stub |
| `root` | `.` | Agrega os módulos; não contém código | pronto |

### Por que separar em módulos?

- **Contrato imposto pelo compilador.** `runner` depende dos dois algoritmos e
  só consegue usá-los pela interface de `shared`. Não é possível um algoritmo
  "fugir" do formato sem quebrar o build.
- **Divisão interna do grupo.** Cada dupla pode trabalhar no seu módulo com
  pouca interferência no código da outra.
- **Testes isolados.** Cada módulo tem seu próprio `src/test`; é possível
  rodar `sbt "countvectorizer/test"` sem executar os testes do TF-IDF.
- **Reaproveitamento de infraestrutura.** Leitura de CSV, sessão do Spark e
  tipos de saída existem uma vez só.
- **Evolução independente.** Se um algoritmo precisar de uma dependência
  específica, ela fica no build daquele módulo, não no build inteiro.

---

## 3. Layout de diretórios

Cada módulo segue o padrão do sbt/Maven:

```text
<módulo>/
├── src/
│   ├── main/
│   │   ├── scala/<pacote>/     # código de produção
│   │   └── resources/          # configurações (ex.: log4j2)
│   └── test/
│       ├── scala/<pacote>/     # testes
│       └── resources/          # arquivos usados pelos testes
└── target/                     # gerado pelo sbt (ignorado pelo git)
```

Consequências práticas:

- **Só o que está em `src/main/scala` é compilado.** Por isso o pseudocódigo
  do grupo fica em `docs/pseudocode/`, fora dos módulos. Um arquivo `.scala`
  na raiz de `countvectorizer/` não seria compilado e poderia confundir.
- **Testes ficam em `src/test/scala`.** As pastas já existem com `.gitkeep`;
  basta criar os arquivos de teste.
- **Recursos ficam em `src/main/resources`.** O `log4j2.properties` que reduz
  o ruído de logs do Spark está em `shared/src/main/resources/` e vale para
  todos os módulos que dependem de `shared`.

### Estrutura completa

```text
.
├── build.sbt
├── project/build.properties
├── README.md
├── shared/
│   └── src/main/scala/br/uel/mineracao/
│       ├── TextMiningAlgorithm.scala
│       ├── model/
│       │   ├── Vocabulary.scala
│       │   ├── SparseVector.scala
│       │   └── VectorizationResult.scala
│       ├── io/CsvDocumentReader.scala
│       └── spark/SparkSessionFactory.scala
├── countvectorizer/
│   ├── src/main/scala/br/uel/mineracao/countvectorizer/
│   │   └── CountVectorizerAlgorithm.scala
│   └── src/test/scala/br/uel/mineracao/countvectorizer/
├── tfidf/
│   ├── src/main/scala/br/uel/mineracao/tfidf/
│   │   └── TfIdfAlgorithm.scala
│   └── src/test/scala/br/uel/mineracao/tfidf/
├── runner/
│   ├── src/main/scala/br/uel/mineracao/runner/
│   │   └── BenchmarkApp.scala
│   └── src/test/scala/br/uel/mineracao/runner/
├── docs/
│   └── pseudocode/
│       ├── README.md
│       ├── countvectorizer/    # pseudocódigo já escrito pelo grupo
│       └── tfidf/              # espaço para o pseudocódigo do TF-IDF
├── data/                       # datasets (conteúdo ignorado pelo git)
├── scripts/                    # automação dos experimentos (a implementar)
└── results/                    # tempos e gráficos (conteúdo ignorado)
```

Os diretórios dos módulos foram padronizados em minúsculas
(`countvectorizer/`, `tfidf/`) para seguir a convenção de pacotes
Java/Scala. O antigo `CountVectorizer/build.sbt` foi removido: o build
multi-módulo da raiz é a única fonte de configuração.

---

## 4. O contrato comum

### 4.1 Interface

Definida em `shared/src/main/scala/br/uel/mineracao/TextMiningAlgorithm.scala`:

```scala
trait TextMiningAlgorithm {
  def name: String
  def fitTransform(documents: RDD[(Long, String)]): VectorizationResult
}
```

- **`name`** é usado em logs, tabelas e gráficos.
- **`documents`** chega como `(id, texto)`. O `id` evita que o resultado
  perca a associação com o documento original.
- **`fitTransform`** faz o ajuste do vocabulário e a transformação em uma
  única chamada, que é exatamente o que os experimentos medem. Não há
  separação entre `fit` e `transform` porque o enunciado compara o custo de
  executar o algoritmo, não de reutilizar um modelo já ajustado.

### 4.2 Formato de saída

Definido em `shared/src/main/scala/br/uel/mineracao/model/`:

```scala
final case class Vocabulary(terms: IndexedSeq[String])

final case class SparseVector(
    indices: IndexedSeq[Int],
    values: IndexedSeq[Double]
)

final case class VectorizationResult(
    vocabulary: Vocabulary,
    vectors: RDD[(Long, SparseVector)]
)
```

Decisões e justificativas:

- **Vocabulário único por corpus.** O índice de um termo é a posição em
  `Vocabulary.terms`. Ambos os algoritmos usam esse mesmo esquema, então as
  saídas são comparáveis.
- **Vetor esparso.** A grande maioria dos documentos usa poucas palavras do
  vocabulário; representar todos os zeros seria inviável para 150 mil
  documentos.
- **`Double` para os valores.** O CountVectorizer produz contagens inteiras e
  o TF-IDF produz pesos fracionários. Usar `Double` nos dois mantém o mesmo
  formato sem converter um dos resultados.
- **`Long` preservado na saída.** O id recebido na entrada é o id devolvido,
  permitindo cruzar vetor e documento.
- **A construção do vocabulário é do algoritmo.** `Vocabulary` apenas
  transporta a lista de termos; não há tokenização, remoção de duplicatas nem
  atribuição de índices em `shared`. Isso é intencional: essas etapas fazem
  parte do método que o grupo deve implementar.

### 4.3 O que não foi colocado no contrato

Para não induzir uma implementação específica, `shared` **não** contém:

- função de tokenização/normalização;
- função de contagem de termos;
- cálculo de TF, IDF ou similaridade;
- escolha de partição, broadcast ou `reduceByKey`;
- lógica de medição de tempo.

Essas decisões pertencem a cada algoritmo e devem ser explicadas no
relatório.

### 4.4 Regra prática

> Não altere `TextMiningAlgorithm`, `Vocabulary`, `SparseVector` ou
> `VectorizationResult` sem alinhar com o grupo inteiro.

Qualquer mudança no contrato afeta os dois algoritmos e o runner. Se for
realmente necessária, faça-a no início da implementação, antes de os dois
lados avançarem.

---

## 5. Decisões de build

Arquivo: [`build.sbt`](../build.sbt).

| Item | Versão | Motivo |
|---|---|---|
| Scala | 2.13.18 | Linha suportada pelo Spark 4.x; a 2.13.17 conflitava com a `scala-library` 2.13.18 trazida pelas dependências |
| Spark | 4.1.2 | Linha estável atual; a versão anterior do build (`4.0.4`) não existe no Maven Central |
| sbt | 1.11.7 | Versão declarada em `project/build.properties` |
| JDK | 17 ou 21 (recomendado 21) | Spark 4.x exige 17+; o ambiente do grupo usa 21 |
| ScalaTest | 3.2.19 | Testes dos algoritmos |

### 5.1 Dependências

Cada módulo recebe `spark-core` e `spark-sql` (via `commonSettings`). Não há
dependência de `spark-mllib`: o enunciado pede a implementação dos algoritmos,
não o uso dos estimadores prontos do Spark.

### 5.2 Execução em JVM separada

```scala
Compile / run / fork := true
Test / fork := true
```

Rodar o `main` e os testes em uma JVM separada:

- isola o heap e as flags do Spark do processo do sbt;
- evita que o próprio sbt consuma memória durante a medição;
- permite que cada experimento seja uma JVM nova, reduzindo interferência
  entre execuções.

### 5.3 Flags de JVM

O Java 17+ é modular e o Spark precisa de permissões de reflexão. O build
define `--add-opens` e `--add-exports` para `run` e para `test`, além de
`-Xmx4g`. O mesmo conjunto é usado nos dois contextos para que o ambiente de
teste seja o mais parecido possível com o dos experimentos.

O heap de 4 GB está centralizado em `sparkJvmOptions`. Se o grupo precisar
mudar, **mude uma vez e use o mesmo valor para os dois algoritmos**.

### 5.4 Logs

`shared/src/main/resources/log4j2.properties` deixa o Spark em `WARN` e o
código do projeto (`br.uel.escalavel`) em `INFO`. Isso evita que os logs de
INFO do Spark poluam a saída dos experimentos sem esconder mensagens do grupo.

### 5.5 Formatação

`.scalafmt.conf` (scalafmt 3.9.10, dialeto Scala 2.13, 100 colunas) permite
que o VS Code/Metals formate o código ao salvar. Não é obrigatório, mas evita
diffs grandes por causa de espaçamento.

---

## 6. Leitura de dados e sessão do Spark

### 6.1 `CsvDocumentReader`

Arquivo: `shared/src/main/scala/br/uel/mineracao/io/CsvDocumentReader.scala`.

Responsabilidades:

- ler CSV com cabeçalho;
- localizar a coluna de texto (padrão `texto`);
- tratar vírgulas e quebras de linha dentro de campos entre aspas;
- devolver `RDD[String]` ou `RDD[(Long, String)]` com ids por posição.

Por que os ids vêm da posição e não da coluna `id`: isso garante que os dois
algoritmos recebam exatamente a mesma sequência de documentos, sem depender de
como o dataset foi gerado. A coluna `id`, se existir, é ignorada.

Essa é uma função de **leitura inicial**, permitida pela política de IA do
enunciado. A lógica dos algoritmos começa depois dela.

**Cuidado com a preguiça:** o RDD devolvido não é materializado. O runner deve
fazer `cache()` + `count()` antes de iniciar o cronômetro; caso contrário, a
leitura do CSV entra no tempo do algoritmo.

### 6.2 `SparkSessionFactory`

Arquivo: `shared/src/main/scala/br/uel/mineracao/spark/SparkSessionFactory.scala`.

```scala
SparkSessionFactory.local(
  appName = "mineracao-texto",
  master = "local[4]",
  config = Map("spark.sql.shuffle.partitions" -> "4")
)
```

- O padrão é `local[*]`.
- Para os experimentos, fixe `local[k]` e use o mesmo `k` nos dois algoritmos.
- A sessão deve ser criada **antes** do cronômetro.
- `getOrCreate()` reaproveita uma sessão já existente na mesma JVM. Por isso,
  cada ponto de medição deve rodar em uma JVM nova.

---

## 7. O runner

Arquivo: `runner/src/main/scala/br/uel/mineracao/runner/BenchmarkApp.scala`.

O runner é o **único ponto de entrada** dos experimentos. Ele deve:

1. ler os argumentos (algoritmo, dataset, `master`, saída);
2. criar a `SparkSession`;
3. ler e materializar a entrada;
4. medir apenas `fitTransform` + uma ação que force o resultado;
5. escrever tempo e saída no formato comum;
6. encerrar a sessão.

O `build.sbt` já define:

```scala
Compile / mainClass := Some("br.uel.escalavel.runner.BenchmarkApp")
```

portanto, depois de implementado, o comando será algo como:

```bash
sbt "runner/run countvectorizer data/dataset-a.csv local[4]"
```

O formato exato dos argumentos é uma decisão do grupo e deve ser documentado
no README quando o runner for implementado.

### Por que a medição não está no próprio algoritmo

Se cada algoritmo medisse seu tempo internamente, seria difícil garantir que
os dois usassem o mesmo critério (início, fim, ações forçadas, aquecimento).
Concentrar isso no runner mantém a medição idêntica para os dois e deixa os
módulos de algoritmo focados apenas no método.

---

## 8. Dados, resultados e scripts

| Pasta | Conteúdo | Git |
|---|---|---|
| `data/` | CSVs usados nos experimentos | conteúdo ignorado; `README.md` versionado |
| `results/` | tempos brutos, gráficos e tabelas | conteúdo ignorado; `.gitkeep` versionado |
| `scripts/` | geração de datasets, execução e plots | `README.md` versionado; scripts ainda não implementados |

Os datasets não entram no git porque podem chegar a 150 mil documentos e
seriam pesados demais. O formato e os requisitos estão em
[`data/README.md`](../data/README.md).

A pasta `scripts/` existe para cumprir a exigência de automação dos testes de
escalabilidade, mas **nenhum script foi escrito neste scaffolding**.

---

## 9. Convenções do projeto

- **Pacotes em minúsculas**, prefixados por `br.uel.escalavel`.
- **Um módulo por algoritmo**, com a classe principal em:
  - `br.uel.escalavel.countvectorizer.CountVectorizerAlgorithm`
  - `br.uel.escalavel.tfidf.TfIdfAlgorithm`
- **Testes** em `src/test/scala`, no mesmo pacote do código testado. A
  dependência do ScalaTest já está configurada; recomenda-se `AnyFunSuite`.
- **Comentários e documentação em português**, acompanhando o enunciado.
- **Não usar MLlib** para os algoritmos avaliados.
- **Não colocar lógica de algoritmo em `shared`**. Se uma função é usada
  pelos dois algoritmos, discuta se ela realmente é infraestrutura (leitura,
  sessão, tipos) ou se é parte do método (tokenização, contagem, pesos).
- **Não medir I/O nem inicialização** do Spark no tempo do algoritmo.
- **Não commitar `target/`, `.metals/`, `.bsp/`, `.bloop/`, datasets ou
  resultados**.

---

## 10. Como adicionar código novo

### Implementar um algoritmo

1. Abra o stub do seu módulo
   (`CountVectorizerAlgorithm.scala` ou `TfIdfAlgorithm.scala`).
2. Substitua o `throw new NotImplementedError(...)` pela implementação,
   mantendo a assinatura `fitTransform(RDD[(Long, String)]): VectorizationResult`.
3. Use apenas transformações/ações de RDD (ou DataFrames, se o grupo decidir
   e justificar no relatório).
4. Devolva o vocabulário e os vetores no formato de `shared`.
5. Compile apenas o seu módulo:

   ```bash
   sbt "countvectorizer/compile"
   # ou
   sbt "tfidf/compile"
   ```

### Escrever testes

1. Crie o arquivo em
   `<módulo>/src/test/scala/br/uel/mineracao/<pacote>/`.
2. Use textos pequenos com resultado calculável à mão.
3. Rode:

   ```bash
   sbt "countvectorizer/test"
   # ou
   sbt "tfidf/test"
   ```

4. Lembre-se de que `Test / fork := true` e `Test / parallelExecution := false`
   já estão configurados; não é preciso mexer no build para testes com Spark.

### Implementar o runner

1. Edite `BenchmarkApp.scala`.
2. Crie a sessão antes do cronômetro.
3. Materialize a entrada antes do cronômetro.
4. Force uma ação sobre `result.vectors` dentro do intervalo medido.
5. Escreva a saída e o tempo fora do intervalo medido.

---

## 11. O que foi gerado com IA

Conforme a seção 5.1 do enunciado, o uso de IA está declarado no
[`README.md`](../README.md#uso-de-ia-generativa). Resumindo:

- **Com IA:** scaffolding, `build.sbt`, estrutura de pastas, README,
  `.gitignore`, `.scalafmt.conf`, `SparkSessionFactory`, `CsvDocumentReader`
  (leitura inicial), tipos de `shared` e stubs com assinaturas.
- **Sem IA:** lógica do CountVectorizer, lógica do TF-IDF, testes dos
  algoritmos e scripts de experimentos. O pseudocódigo em
  `docs/pseudocode/` foi produzido pelo grupo.

Se o grupo usar IA em qualquer outra parte, atualize o README e o relatório.

---

## 12. Decisões que podem ser revistas

A estrutura atual é uma proposta, não uma imposição. Estas decisões podem ser
alteradas se o grupo tiver um motivo claro — mas devem ser alteradas **antes**
de os dois algoritmos avançarem:

| Decisão | Alternativa | Custo da mudança |
|---|---|---|
| Um módulo por algoritmo | Um único módulo com pacotes separados | Baixo, mas perde o isolamento de build/testes |
| `Double` no `SparseVector` | Tipos separados para contagem e peso | Médio; quebra o formato comum |
| Ids por posição | Usar a coluna `id` do CSV | Baixo; exige mudar o leitor e os dois algoritmos |
| `fitTransform` único | `fit` + `transform` separados | Médio; muda a interface e o que é medido |
| Runner separado | `main` em cada algoritmo | Alto; dificulta garantir a mesma medição |
| Logs em `WARN` | Deixar o padrão do Spark | Baixo; apenas ruído no console |

---

## 13. Checklist de validação da estrutura

```bash
# 1. o build carrega e todos os módulos compilam
sbt compile

# 2. os testes compilam (ainda sem testes implementados)
sbt Test/compile

# 3. a suíte roda (atualmente sem testes)
sbt test

# 4. a estrutura do Spark funciona em modo local
#    (após implementar o runner, ou com um main temporário)
sbt "runner/run ..."
```

Enquanto os algoritmos não forem implementados, os comandos 1 a 3 devem
passar e o comando 4 deve falhar com `NotImplementedError` — o que é o
comportamento esperado do scaffolding.
