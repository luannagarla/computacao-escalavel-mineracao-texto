# Sobre
O CountVectorizer é uma técnica de processamento de linguagem natural (PLN) que transforma textos em representações numéricas, contando quantas vezes cada palavra aparece em cada documento.

Computadores não trabalham diretamente com palavras da mesma maneira que trabalham com números. Por isso, precisamos transformar os textos em vetores numéricos que possam ser utilizados por algoritmos de aprendizado de máquina.

---

# Organização do projeto

- `runner/.../BenchmarkApp.scala`: execução principal e medição de tempo
- `countvectorizer/.../CountVectorizerAlgorithm.scala`: implementação da contagem de palavras
- `shared/`: contrato comum, leitura do CSV e criação da `SparkSession`
- `build.sbt`: dependências e versões

---
# Ambiente

As versões e o passo a passo de instalação estão no `README.md` da raiz do
repositório (JDK 21, Scala 2.13.18, Spark 4.1.2 e sbt 1.11.7).

---
# Planejamento

## 1. Leitura da base de dados
Ler o arquivo CSV, identificar a coluna que contém os textos e atribuir um identificador a cada documento.

## 2. Pré-processamento
Separar os textos em palavras (tokenização), normalizar maiúsculas e minúsculas e remover pontuação.

## 3. Implementação do CountVectorizer
Construir o vocabulário, atribuir um índice a cada palavra e contar quantas vezes cada termo aparece em cada documento.

## 4. Validação dos resultados
Conferir se o vocabulário e os vetores gerados estão corretos usando textos pequenos, cujo resultado conseguimos calcular manualmente.

## 5. Experimentos de escalabilidade
Comparar tempo de execução, quantidade de documentos processados por segundo e comportamento com diferentes quantidades de partições.

### Experimentos planejados
Base pequena: Avaliar o tempo inicial 
Base média:	Crescimento do tempo em comparação com inicial
Base completa: Desempenho no conjunto real
Diferentes partições: Efeito do paralelismo


