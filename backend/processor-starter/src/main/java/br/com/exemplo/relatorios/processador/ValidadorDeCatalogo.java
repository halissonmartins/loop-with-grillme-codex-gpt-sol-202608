package br.com.exemplo.relatorios.processador;

public final class ValidadorDeCatalogo {
    private ValidadorDeCatalogo() {}

    public static void valida(CatalogoDoProduto catalogo) {
        boolean codigosDuplicados = catalogo.relatorios().stream()
                        .map(RelatorioDoProduto::codigo)
                        .distinct()
                        .count()
                != catalogo.relatorios().size();
        boolean tempoInvalido =
                catalogo.relatorios().stream().anyMatch(relatorio -> relatorio.tempoEstimadoSegundos() <= 0);
        boolean excedeTeto = catalogo.relatorios().stream()
                        .mapToInt(RelatorioDoProduto::tempoEstimadoSegundos)
                        .sum()
                > 600;
        boolean siglaInvalida = !catalogo.sigla().matches("[A-Z]{1,20}");
        boolean codigoInvalido = catalogo.relatorios().stream()
                .anyMatch(relatorio -> !relatorio.codigo().matches("[A-Z]{1,20}-[0-9]{4}")
                        || !relatorio.codigo().startsWith(catalogo.sigla() + "-"));
        if (siglaInvalida || codigosDuplicados || tempoInvalido || excedeTeto || codigoInvalido) {
            throw new IllegalStateException("Catálogo inválido");
        }
    }
}
