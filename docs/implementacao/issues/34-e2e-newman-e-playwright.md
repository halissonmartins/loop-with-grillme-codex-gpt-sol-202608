# 34 — E2E: Newman + psql e fluxos críticos no navegador

**O que construir:** a verificação de fumaça sobre o sistema montado. Ao fim deste ticket existe um
percurso automatizado que sobe o ambiente, roda um ciclo, exporta, baixa e confere o estado no banco
— e um punhado de fluxos de navegador que provam que a aplicação funciona para uma pessoa, não só
para uma requisição.

**Bloqueado por:** 31, 33.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RA-48** — E2E de backend com Newman + psql, **complementando** a costura S1, não a
      substituindo. A regra de negócio já foi afirmada em S1; aqui se verifica a montagem.
- [ ] **RA-48** — de **1 a 3** fluxos críticos no navegador com Playwright. O navegador **não é uma
      quarta costura de regra** (especificação §5.2): é fumaça.
- [ ] Os fluxos escolhidos são os do ticket 05: encontrar e baixar um relatório; entrar como RELATOR
      pendente de vínculo; conceder acesso como GERENTE.
- [ ] O percurso de backend cobre a costura inteira: ciclo dispara → artefatos aparecem → exportação
      entrega → download é registrado → o banco confirma.
- [ ] Os cenários rodam no CI (RA-53) e são bloqueantes.
- [ ] Nenhum cenário aqui duplica regra já afirmada em S1, S2 ou S3 — se algo só é verificado aqui,
      está na costura errada.
