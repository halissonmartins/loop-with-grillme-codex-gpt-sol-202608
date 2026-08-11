# 16 — Exportação CSV e registro de Download

**O que construir:** o primeiro arquivo saindo pela porta. O CSV vem primeiro porque é o único
formato que **não** passa pelo motor de relatório — entrega o caminho de exportação inteiro
(permissão, estado da execução vigente, leitura do artefato, resposta síncrona, registro do
download) sem depender de nada do Jasper.

**Bloqueado por:** 15.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **RF-19** — a exportação é **síncrona**: a mesma requisição devolve o arquivo ou devolve erro
      (RN-30, RA-26). Não cria registro de status — o ciclo de vida de status pertence
      exclusivamente à Execução.
- [ ] **RF-22** — o CSV é o **dataset bruto** da consulta principal, servido a partir do arquivo
      comprimido, sem subrelatórios, sem imagens e sem formatação (RN-34, RA-17, RA-27).
- [ ] **RA-17** — separador `;`, compatível com o Excel pt-BR, para abrir sem passo de importação.
- [ ] **RF-20** — só exporta quando a execução vigente daquela data estiver em sucesso ou alerta
      (RN-42). Nos demais status a exportação é recusada com mensagem própria, não genérica.
- [ ] **RN-31, RA-29** — a exportação **nunca** consulta a base transacional. É a razão de existir
      do sistema e vale como invariante arquitetural.
- [ ] **RF-23** — todo download é registrado com usuário, relatório, data de referência, formato e
      momento (RN-35).
- [ ] **RA-66** — o registro guarda **cópia** do código, do nome do relatório, da sigla e da data de
      referência — não apenas as chaves. Sem a cópia, um histórico que sobrevive indefinidamente
      passaria a exibir o download de 2026 com o nome que o relatório ganhou em 2027.
