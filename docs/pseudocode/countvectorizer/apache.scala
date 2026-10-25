/*INÍCIO

    // Ler os dados
    dados <- LER_ARQUIVO_CSV("dados.csv")

    // Selecionar os textos
    textos <- SELECIONAR_COLUNA(dados, "texto")

    // Criar um RDD com os documentos
    rdd_textos <- CRIAR_RDD(textos)

    // Criar o CountVectorizer
    vetorizador <- NOVO CountVectorizer

    // Medir o tempo da transformação
    inicio <- TEMPO_ATUAL()

    resultado <- vetorizador.transformar(rdd_textos)

    fim <- TEMPO_ATUAL()

    // Exibir os resultados
    ESCREVER(resultado)
    ESCREVER("Tempo de execução:", fim - inicio)

FIM


----


CLASSE CountVectorizer

    ATRIBUTO vocabulario

    MÉTODO transformar(rdd_textos)

        // Preparar os documentos em paralelo
        rdd_palavras <- rdd_textos.MAP(
            NORMALIZAR_E_TOKENIZAR
        )

        // Construir o vocabulário
        vocabulario <- OBTER_PALAVRAS_UNICAS(
            rdd_palavras
        )

        // Atribuir um índice para cada palavra
        ATRIBUIR_INDICES(vocabulario)

        // Contar as palavras de cada documento
        resultado <- CONTAR_PALAVRAS_POR_DOCUMENTO(
            rdd_palavras,
            vocabulario
        )

        RETORNAR resultado

    FIM MÉTODO

FIM CLASSE
*/