# Sobre
O CountVectorizer é uma técnica de processamento de linguagem natural (PLN) que transforma textos em representações numéricas, contando quantas vezes cada palavra aparece em cada documento.

Computadores não trabalham diretamente com palavras da mesma maneira que trabalham com números. Por isso, precisamos transformar os textos em vetores numéricos que possam ser utilizados por algoritmos de aprendizado de máquina.

---

# Organização do projeto

- Main: Execução principal 
- CountVectorizer: Implementação da contagem de palavras de fato
- Arquivo build: Dependências

---
# O que precisa estar instalado (eu acho)
* Java JDK 21	
* Apache Spark 4.0.4	
* Scala 
* SBT	

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
Base pequena: AVvliar o tempo inicial 
Base média:	Crescimento do tempo em comparação com inicial
Base completa: Desempenho no conjunto real
Diferentes partições: Efeito do paralelismo


