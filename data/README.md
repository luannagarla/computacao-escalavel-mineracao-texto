# Dados

Os arquivos de dados **não são versionados** (são grandes). Apenas este
README fica no git.

## Formato esperado

CSV com cabeçalho e uma coluna de texto chamada `texto`:

```csv
id,texto
1,"primeiro documento de exemplo"
2,"segundo documento, com vírgulas dentro do campo"
```

- Outras colunas são ignoradas.
- Campos com vírgulas ou quebras de linha devem estar entre aspas (padrão
  RFC 4180).
- O leitor padrão (`shared/.../io/CsvDocumentReader.scala`) usa
  `header = true` e a coluna `texto`.
- Os identificadores usados pelos algoritmos são atribuídos por posição; a
  coluna `id` é apenas informativa.

## O que precisa existir para os experimentos

- **2 datasets distintos**, com distribuições de dados diferentes (por
  exemplo, um real e um sintético, ou dois sintéticos gerados com
  distribuições distintas).
- Entre **10.000 e 150.000 documentos**.
- Pelo menos **5 tamanhos** para medir a escalabilidade (ideal: de 10k em
  10k, até 150k).

Sugestão de organização:

```text
data/
├── dataset-a/            # dataset 1
│   ├── dataset-a-010k.csv
│   ├── dataset-a-020k.csv
│   └── ...
└── dataset-b/            # dataset 2
    ├── dataset-b-010k.csv
    └── ...
```

Os scripts de geração/recorte dos datasets ficam em `scripts/` e serão
implementados pelo grupo.
