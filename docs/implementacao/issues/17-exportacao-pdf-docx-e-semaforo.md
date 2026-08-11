# 17 — Exportação PDF e DOCX, com semáforo de simultaneidade

**O que construir:** os dois formatos paginados, saindo do `JasperPrint` serializado — e o teto que
impede que três pessoas exportando ao mesmo tempo derrubem a API. Ao fim deste ticket o usuário
recebe o PDF como o relatório foi desenhado, o DOCX editável com a mesma paginação, e a terceira
requisição simultânea recebe uma recusa imediata em vez de esperar.

**Bloqueado por:** 16.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-19, RN-32** — PDF e DOCX produzidos a partir do `JasperPrint` (RA-27), ambos preservando
      a paginação, na mesma requisição.
- [ ] **RF-48** — exportação acima do teto de RNF-10 é **recusada de imediato**, com indicação de
      repetir mais tarde (RN-53, RA-60). **Não há fila**: RN-30 exige resposta na mesma requisição.
- [ ] O semáforo existe porque cada `JasperPrint` desserializado ocupa múltiplos do seu tamanho em
      disco. Ele, o teto de artefato (RNF-05) e o teto de linhas (RNF-06) são **um único teto de
      memória** — afrouxar qualquer um isoladamente o quebra (especificação §7).
- [ ] As *font extensions* usadas nos JRXML estão no classpath da API que exporta. Sem elas o PDF
      sai com substituição de fonte ou estoura, dependendo da configuração de fonte ausente — é o
      trade-off que o mono repositório de RA-01 sustenta.
- [ ] Um cenário afirma os **bytes que saem**, não que um exportador foi instanciado.
- [ ] A recusa por semáforo é distinguível, na resposta, da recusa por retenção e da recusa por
      status — três mensagens, três causas.
