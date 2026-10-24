/*
CLASSE CountVectorizer

    ATRIBUTO vocabulario

    MÉTODO transformar(textos)

        // 1. Preparar os textos
        PARA CADA texto EM textos FAÇA

            texto <- CONVERTER_PARA_MINUSCULAS(texto)
            texto <- REMOVER_PONTUACAO(texto)
            palavras <- SEPARAR_POR_ESPACOS(texto)

            ARMAZENAR palavras

        FIM PARA

        // 2. Construir o vocabulário
        vocabulario <- JUNTAR_TODAS_AS_PALAVRAS()
        vocabulario <- REMOVER_PALAVRAS_REPETIDAS(vocabulario)
        vocabulario <- ORDENAR(vocabulario)

        // 3. Atribuir um índice para cada palavra
        PARA CADA palavra EM vocabulario FAÇA

            ATRIBUIR_INDICE(palavra)

        FIM PARA

        // 4. Contar as palavras em cada documento
        PARA CADA documento EM textos_preparados FAÇA

            vetor <- CRIAR_VETOR_DE_ZEROS(vocabulario)

            PARA CADA palavra EM documento FAÇA

                indice <- BUSCAR_INDICE(palavra)
                vetor[indice] <- vetor[indice] + 1

            FIM PARA

            ADICIONAR vetor AO resultado

        FIM PARA

        RETORNAR resultado

    FIM MÉTODO

FIM CLASSE
*/