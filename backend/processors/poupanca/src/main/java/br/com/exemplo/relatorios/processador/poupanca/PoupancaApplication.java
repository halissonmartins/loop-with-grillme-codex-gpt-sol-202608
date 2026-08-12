package br.com.exemplo.relatorios.processador.poupanca;

import br.com.exemplo.relatorios.processador.CatalogoDoProduto;
import br.com.exemplo.relatorios.processador.RelatorioDoProduto;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PoupancaApplication {
    @Bean
    CatalogoDoProduto catalogoDoProduto() {
        return new CatalogoDoProduto(
                "POUPANCA",
                "Poupança",
                List.of(
                        new RelatorioDoProduto(
                                "POUPANCA-0101",
                                "Saldos por agência",
                                "Consolida contas e saldo atual por agência.",
                                90),
                        new RelatorioDoProduto(
                                "POUPANCA-0102",
                                "Movimentações diárias",
                                "Agrupa créditos e débitos fechados por dia.",
                                210)));
    }

    public static void main(String[] args) {
        SpringApplication.run(PoupancaApplication.class, args);
    }
}
