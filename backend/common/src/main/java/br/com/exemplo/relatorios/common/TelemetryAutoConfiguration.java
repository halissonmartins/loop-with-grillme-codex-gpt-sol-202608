package br.com.exemplo.relatorios.common;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "management.opentelemetry.enabled", havingValue = "true", matchIfMissing = true)
public class TelemetryAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryAutoConfiguration.class);

    @Bean
    ApplicationRunner registraInicializacaoDaTelemetria(
            OpenTelemetry openTelemetry, Tracer tracer, MeterRegistry meterRegistry) {
        OpenTelemetryAppender.install(openTelemetry);
        return args -> {
            Span span = tracer.nextSpan()
                    .name("relatorios.telemetria.inicializacao")
                    .start();
            Tracer.SpanInScope scope = tracer.withSpan(span);
            try {
                meterRegistry
                        .counter(
                                "relatorios.telemetria.inicializacao", TelemetryLabels.ORIGEM_EXECUCAO, "inicializacao")
                        .increment();
                LOGGER.info("Telemetria inicializada");
            } finally {
                scope.close();
                span.end();
            }
        };
    }
}
