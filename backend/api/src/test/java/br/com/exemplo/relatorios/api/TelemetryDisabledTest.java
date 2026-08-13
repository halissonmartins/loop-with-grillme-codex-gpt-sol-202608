package br.com.exemplo.relatorios.api;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TelemetryDisabledTest {
    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void disablesTheOpenTelemetrySdkInTheTestProfile() {
        assertFalse(environment.getRequiredProperty("management.opentelemetry.enabled", Boolean.class));
    }

    @Test
    void doesNotCreateTheSharedTelemetryRunnerInTheTestProfile() {
        assertFalse(applicationContext.containsBean("registraInicializacaoDaTelemetria"));
    }
}
