package br.com.exemplo.relatorios.processador.poupanca;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

class PoupancaTelemetryIT {
    @Test
    void exportsTheSharedTelemetryStartupSignalToTheCollector() throws InterruptedException {
        try (Network network = Network.newNetwork();
                PostgreSQLContainer database = database(network);
                GenericContainer<?> collector = collector(network);
                GenericContainer<?> processor = processor(network)) {
            database.start();
            collector.start();
            processor.start();

            awaitCollectorSignal(collector);
        }
    }

    private static PostgreSQLContainer database(Network network) {
        return new PostgreSQLContainer("postgres:18.1")
                .withNetwork(network)
                .withNetworkAliases("database")
                .withDatabaseName("relatorios")
                .withUsername("relatorios")
                .withPassword("relatorios");
    }

    private static GenericContainer<?> collector(Network network) {
        return new GenericContainer<>("otel/opentelemetry-collector-contrib:0.116.1")
                .withNetwork(network)
                .withNetworkAliases("otel-collector")
                .withCopyToContainer(
                        MountableFile.forHostPath(Path.of("src/test/resources/otel/collector.yml")),
                        "/etc/otelcol/config.yaml")
                .withCommand("--config=/etc/otelcol/config.yaml")
                .waitingFor(Wait.forLogMessage(".*Everything is ready.*", 1));
    }

    private static GenericContainer<?> processor(Network network) {
        return new GenericContainer<>(new ImageFromDockerfile()
                        .withFileFromPath("app.jar", Path.of("target", "processor-poupanca-0.1.0-SNAPSHOT.jar"))
                        .withDockerfileFromBuilder(builder -> builder.from("eclipse-temurin:25-jre")
                                .copy("app.jar", "/app.jar")
                                .entryPoint("java", "-jar", "/app.jar")
                                .build()))
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://database:5432/relatorios")
                .withEnv("SPRING_DATASOURCE_USERNAME", "relatorios")
                .withEnv("SPRING_DATASOURCE_PASSWORD", "relatorios")
                .waitingFor(Wait.forLogMessage(".*Telemetria inicializada.*", 1));
    }

    private static void awaitCollectorSignal(GenericContainer<?> collector) throws InterruptedException {
        long timeout = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (System.nanoTime() < timeout) {
            if (collector.getLogs().contains("relatorios.telemetria.inicializacao")) {
                return;
            }
            Thread.sleep(250);
        }
        assertTrue(collector.getLogs().contains("relatorios.telemetria.inicializacao"), collector::getLogs);
    }
}
