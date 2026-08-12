package br.com.exemplo.relatorios.processador;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@AutoConfiguration
public class ProcessorAutoConfiguration {
    @Bean
    ApplicationRunner publicaCatalogo(CatalogoDoProduto catalogo, JdbcTemplate jdbcTemplate, DataSource dataSource) {
        ValidadorDeCatalogo.valida(catalogo);
        return args -> {
            Flyway.configure().dataSource(dataSource).schemas("controle").load().migrate();
            jdbcTemplate.update(
                    "insert into controle.produto(sigla,nome) values (?,?) on conflict do nothing",
                    catalogo.sigla(),
                    catalogo.nome());
            for (RelatorioDoProduto relatorio : catalogo.relatorios()) {
                jdbcTemplate.update(
                        "insert into controle.relatorio(codigo,sigla_produto,nome,descricao,tempo_estimado_segundos) "
                                + "values (?,?,?,?,?) on conflict do nothing",
                        relatorio.codigo(),
                        catalogo.sigla(),
                        relatorio.nome(),
                        relatorio.descricao(),
                        relatorio.tempoEstimadoSegundos());
            }
        };
    }
}
