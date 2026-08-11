package br.com.exemplo.relatorios.common.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@EnabledIfEnvironmentVariable(named = "RUN_INFRASTRUCTURE_TESTS", matches = "true")
class ComposeInfrastructureTest {

    @Test
    void startsTheRealLocalDependenciesFromTheSharedComposeFile() {
        try (ComposeContainer environment = new ComposeContainer(composeFile())
                .withExposedService("postgres-1", 5432, healthcheck())
                .withExposedService("keycloak-1", 8080, healthcheck())
                .withExposedService("minio-1", 9000, healthcheck())
                .withExposedService("airflow-1", 8080, healthcheck())
                .withExposedService("traefik-1", 8080, healthcheck())
                .withExposedService("mailpit-1", 8025, healthcheck())
                .withExposedService("otel-collector-1", 4317, healthcheck())
                .withExposedService("mongodb-1", 27017, healthcheck())
                .withExposedService("opensearch-1", 9200, healthcheck())
                .withExposedService("graylog-1", 9000, healthcheck())
                .withExposedService("prometheus-1", 9090, healthcheck())
                .withExposedService("grafana-1", 3000, healthcheck())
                .withExposedService("jaeger-1", 16686, healthcheck())) {
            environment.start();

            assertFalse(environment.getServiceHost("prometheus-1", 9090).isBlank());
        }
    }

    private static WaitStrategy healthcheck() {
        return Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5));
    }

    private static File composeFile() {
        Path directory = Path.of("").toAbsolutePath();

        while (directory != null) {
            Path candidate = directory.resolve("docker-compose.yml");
            if (Files.isRegularFile(candidate)) {
                return candidate.toFile();
            }
            directory = directory.getParent();
        }

        throw new IllegalStateException("docker-compose.yml não encontrado a partir do diretório atual.");
    }
}
