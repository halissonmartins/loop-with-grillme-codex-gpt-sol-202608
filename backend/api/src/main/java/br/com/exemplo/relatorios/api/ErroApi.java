package br.com.exemplo.relatorios.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "Erro", description = "Resposta de erro da API")
public record ErroApi(
        @Schema(description = "Momento do erro", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime momento,

        @Schema(description = "Descrição do erro", requiredMode = Schema.RequiredMode.REQUIRED)
        String descricao,

        @Schema(description = "Correlation ID da ocorrência", requiredMode = Schema.RequiredMode.REQUIRED)
        String correlationId,

        @Schema(description = "Status HTTP") int status,
        @Schema(description = "Título do erro") String titulo,
        @Schema(description = "Detalhe do erro") String detalhe,
        @Schema(description = "Código interno do erro") String codigoInterno,
        @Schema(description = "Rota da requisição") String rota) {}
