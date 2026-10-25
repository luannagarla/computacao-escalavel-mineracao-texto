# Scripts de experimentos

Pasta reservada para os scripts exigidos no enunciado. **Nada foi implementado
neste scaffolding.**

Sugestão de organização:

```text
scripts/
├── generate_datasets.py   # gera/recorta os datasets sintéticos
├── run_scalability.sh     # executa cada tamanho e salva os tempos
└── plot_results.py        # gera os gráficos (tamanho x tempo)
```

Boas práticas para os scripts:

- Salvar os resultados brutos em `results/` (ignorado pelo git), em CSV, com
  colunas como `dataset,algoritmo,tamanho,execucao,tempo_ms` e o ambiente
  usado (threads, heap, máquina).
- Rodar cada ponto em uma JVM separada (`sbt "runner/run ..."`), para que a
  inicialização do Spark não contamine a medição.
- Repetir cada medição e registrar a mediana (ou todas as execuções) para
  reduzir ruído.
- Manter o mesmo `master` (`local[k]`) e o mesmo heap nos dois algoritmos.
- Não incluir leitura do CSV nem escrita da saída no tempo medido.
