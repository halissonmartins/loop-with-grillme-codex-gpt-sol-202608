package br.com.exemplo.relatorios.api.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class ApiSteps {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final GenericContainer<?> API = new GenericContainer<>(new ImageFromDockerfile()
                    .withFileFromPath("app.jar", Path.of("target", "api-0.1.0-SNAPSHOT.jar"))
                    .withDockerfileFromBuilder(builder -> builder.from("eclipse-temurin:25-jre")
                            .copy("app.jar", "/app.jar")
                            .entryPoint("java", "-jar", "/app.jar")
                            .build()))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/actuator/health/readiness").forStatusCode(200));

    private HttpResponse<String> liveness;
    private HttpResponse<String> readiness;
    private HttpResponse<String> errorResponse;
    private String correlationId;

    @BeforeAll
    public static void iniciaApi() {
        API.start();
    }

    @AfterAll
    public static void encerraApi() {
        API.stop();
    }

    @Quando("as sondas de saúde da API são consultadas")
    public void consultaSondasDeSaude() throws IOException, InterruptedException {
        liveness = get("/actuator/health/liveness");
        readiness = get("/actuator/health/readiness");
    }

    @Entao("ambas indicam que a API está disponível")
    public void ambasIndicamQueAApiEstaDisponivel() throws IOException {
        assertEquals(200, liveness.statusCode());
        assertEquals("UP", json(liveness).path("status").asText());
        assertEquals(200, readiness.statusCode());
        assertEquals("UP", json(readiness).path("status").asText());
    }

    @Quando("uma solicitação não encontra um recurso da API")
    public void solicitaRecursoInexistente() throws IOException, InterruptedException {
        errorResponse = get("/recurso-inexistente");
    }

    @Entao("a resposta de erro informa momento, descrição e Correlation ID")
    public void respostaDeErroInformaDiagnostico() throws IOException {
        assertEquals(404, errorResponse.statusCode());
        JsonNode error = json(errorResponse);
        OffsetDateTime.parse(error.path("momento").asText());
        assertFalse(error.path("descricao").asText().isBlank());
        correlationId = error.path("correlationId").asText();
        assertFalse(correlationId.isBlank());
    }

    @Entao("o Correlation ID da resposta localiza a mesma ocorrência nos registros da operação")
    public void correlationIdLocalizaOcorrenciaNosRegistros() {
        assertTrue(API.getLogs().contains("traceId=" + correlationId));
    }

    @Entao("o contrato OpenAPI documenta a resposta de erro")
    public void contratoOpenApiDocumentaRespostaDeErro() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs");
        assertEquals(200, response.statusCode());
        JsonNode errorSchema = json(response).path("components").path("schemas").path("Erro");
        assertTrue(errorSchema.path("required").toString().contains("momento"));
        assertTrue(errorSchema.path("required").toString().contains("descricao"));
        assertTrue(errorSchema.path("required").toString().contains("correlationId"));
        assertEquals(
                "date-time",
                errorSchema.path("properties").path("momento").path("format").asText());
    }

    private static HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder(URI.create(
                                        "http://%s:%d%s".formatted(API.getHost(), API.getMappedPort(8080), path)))
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString());
    }

    private static JsonNode json(HttpResponse<String> response) throws IOException {
        return OBJECT_MAPPER.readTree(response.body());
    }
}
