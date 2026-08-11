# 01 — Fundações do mono repositório e CI verde

**O que construir:** os trilhos, antes de qualquer funcionalidade. Ao fim deste ticket, um clone
limpo numa máquina nova instala, compila, testa e passa no CI com três comandos, e todo módulo
declarado em RA-02 a RA-06 existe como esqueleto que compila — biblioteca comum, starter do
processador, os cinco processadores, a API e o frontend. Nada faz nada ainda; o que este ticket
entrega é o chão em que os outros 34 pisam.

**Bloqueado por:** nada — pode começar imediatamente.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] Mono repositório com **versão única** (RA-01). Todos os módulos compartilham o mesmo ciclo de
      versão e as mesmas dependências — é a premissa de que dependem os trade-offs da serialização
      do artefato renderizado.
- [ ] Todos os módulos de RA-02 a RA-06 compilam, inclusive os cinco processadores, cada um já
      nomeado pela sigla do seu produto (RA-04).
- [ ] Scripts padronizados de `setup`, `dev`, `test`, `lint`, `build` e `migrate`, e um `README.md`
      que roda o projeto em três comandos.
- [ ] Lint, formatter e tipagem estrita configurados e **bloqueantes**; `.editorconfig` no
      repositório.
- [ ] CI no GitHub Actions bloqueante por PR (RA-53): lint + tipos + testes + build.
- [ ] `.gitignore` e scanner de segredos no CI; `.env.example` com todas as variáveis e **nenhum**
      segredo real.
- [ ] `ARCHITECTURE.md` esquelético na raiz: visão geral, estrutura pretendida e os invariantes que
      já saíram dos ADRs — `catchup=False`, `queryTimeout`, a convenção de autoria do JRXML, quem
      escreve no schema de controle, o teto por produto e o semáforo de exportação.
- [ ] **`CLAUDE.md` da raiz com as sete seções do guia** — stack, comandos, onde as coisas ficam,
      convenções, design, regras invioláveis e fora de escopo. O arquivo de hoje tem só referências
      e diretrizes de comportamento; ganha o resto **sem perder** os ponteiros que já carrega.
- [ ] **As regras invioláveis escritas como proibição.** É a forma que ninguém infere a partir da
      ausência de exemplo, e por isso a que mais falta: a Coleta é a única fronteira de leitura dos
      schemas transacionais (RA-10, RN-31); a API jamais os acessa (RA-29); migration aplicada não
      se altera (RA-24); toda rota nova exige teste de autorização; nenhum lint ou teste é
      desabilitado para o build passar; nenhum segredo entra no repositório.
- [ ] **Um `CLAUDE.md` por módulo**, com o que é só daquele módulo: no starter do processador, o
      `queryTimeout` em todo statement de leitura (RA-57) e a convenção de autoria do JRXML (RA-59);
      na API, o semáforo de exportação (RA-60) e o contrato de erro (RA-41). Módulo sem
      particularidade fica com um arquivo curto apontando para a raiz, e não com uma cópia dela.
- [ ] O `CLAUDE.md` **referencia** o `ARCHITECTURE.md` e não o duplica — um é prescritivo, o outro
      descritivo. Duas fontes para a mesma informação divergem em uma semana, e este repositório já
      tem a prova: a lista de tickets iniciais que vivia na arquitetura em paralelo ao tracker
      perdeu dois itens pelo caminho.
- [ ] Cobertura com JaCoCo ligada (RA-49), ainda que a suíte esteja quase vazia.
- [ ] O CI roda num clone limpo, sem estado da máquina de quem escreveu.
