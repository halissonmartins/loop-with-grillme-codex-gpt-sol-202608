package br.com.exemplo.relatorios.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
class TratadorDeErrosApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(TratadorDeErrosApi.class);

    @ExceptionHandler(NoResourceFoundException.class)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroApi.class))))
    ResponseEntity<ErroApi> recursoNaoEncontrado(NoResourceFoundException exception, HttpServletRequest request) {
        return erro(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                "RECURSO_NAO_ENCONTRADO",
                request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErroApi> erroInesperado(Exception exception, HttpServletRequest request) {
        return erro(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Falha inesperada",
                exception.getMessage(),
                "FALHA_INESPERADA",
                request);
    }

    private static ResponseEntity<ErroApi> erro(
            HttpStatus status, String descricao, String detalhe, String codigoInterno, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        LOGGER.warn("{}; traceId={}", descricao, traceId);
        return ResponseEntity.status(status)
                .body(new ErroApi(
                        OffsetDateTime.now(ZoneOffset.UTC),
                        descricao,
                        traceId,
                        status.value(),
                        status.getReasonPhrase(),
                        detalhe,
                        codigoInterno,
                        request.getRequestURI()));
    }
}
