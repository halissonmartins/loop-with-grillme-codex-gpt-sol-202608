package br.com.exemplo.relatorios.processador.poupanca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.exemplo.relatorios.processador.CatalogoDoProduto;
import br.com.exemplo.relatorios.processador.RelatorioDoProduto;
import br.com.exemplo.relatorios.processador.ValidadorDeCatalogo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PoupancaCatalogPublicationTest {
    private static final String INSERT_EXECUCAO =
            "insert into controle.execucao(id,data_referencia,codigo_relatorio,status,origem,"
                    + "tempo_estimado_segundos) values (?::uuid,current_date,'POUPANCA-0101',"
                    + "'em processamento','agendada',90)";
    private static final String INSERT_EXECUCAO_VIGENTE =
            "insert into controle.execucao_vigente(data_referencia,codigo_relatorio,execucao_id) "
                    + "values (current_date,'POUPANCA-0101',?::uuid)";

    @Container
    private static final PostgreSQLContainer DATABASE = new PostgreSQLContainer("postgres:18.1");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("publicaCatalogo")
    private ApplicationRunner publicador;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Test
    void publishesPoupancaAndItsTwoReports() {
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from controle.produto", Integer.class));
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from controle.relatorio", Integer.class));
    }

    @Test
    void databaseRejectsDuplicateCurrentExecutionAndExecutionMutation() {
        String executionId = "00000000-0000-0000-0000-000000000001";
        jdbcTemplate.update(INSERT_EXECUCAO, executionId);
        jdbcTemplate.update(INSERT_EXECUCAO_VIGENTE, executionId);
        assertThrows(
                Exception.class,
                () -> jdbcTemplate.update(
                        "update controle.execucao set status='processado com sucesso' where id=?::uuid", executionId));
        assertThrows(
                Exception.class,
                () -> jdbcTemplate.update("delete from controle.execucao where id=?::uuid", executionId));
        assertThrows(Exception.class, () -> jdbcTemplate.update(INSERT_EXECUCAO_VIGENTE, executionId));
    }

    @Test
    void republishesWithoutOverwritingMutableReportFields() throws Exception {
        jdbcTemplate.update("update controle.relatorio set nome='Nome operacional', descricao='Descrição operacional', "
                + "tempo_estimado_segundos=300 where codigo='POUPANCA-0101'");
        publicador.run(null);
        assertEquals(
                "Nome operacional",
                jdbcTemplate.queryForObject(
                        "select nome from controle.relatorio where codigo='POUPANCA-0101'", String.class));
        assertEquals(
                300,
                jdbcTemplate.queryForObject(
                        "select tempo_estimado_segundos from controle.relatorio where codigo='POUPANCA-0101'",
                        Integer.class));
        assertEquals(
                "Descrição operacional",
                jdbcTemplate.queryForObject(
                        "select descricao from controle.relatorio where codigo='POUPANCA-0101'", String.class));
    }

    @Test
    void rejectsInvalidCatalogDefinitions() {
        assertThrows(
                IllegalStateException.class,
                () -> ValidadorDeCatalogo.valida(new CatalogoDoProduto(
                        "POUPANCA",
                        "Poupança",
                        List.of(
                                new RelatorioDoProduto("POUPANCA-0001", "A", "A", 1),
                                new RelatorioDoProduto("POUPANCA-0001", "B", "B", 1)))));
        assertThrows(IllegalStateException.class, () -> ValidadorDeCatalogo.valida(catalogoComCodigoOuTempoInvalido()));
    }

    private static CatalogoDoProduto catalogoComCodigoOuTempoInvalido() {
        return new CatalogoDoProduto(
                "POUPANCA", "Poupança", List.of(new RelatorioDoProduto("OUTRO-0001", "A", "A", 0)));
    }
}
