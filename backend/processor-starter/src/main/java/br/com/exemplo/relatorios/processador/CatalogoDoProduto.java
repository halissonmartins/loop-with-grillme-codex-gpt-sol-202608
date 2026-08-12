package br.com.exemplo.relatorios.processador;

import java.util.List;

public record CatalogoDoProduto(String sigla, String nome, List<RelatorioDoProduto> relatorios) {}
