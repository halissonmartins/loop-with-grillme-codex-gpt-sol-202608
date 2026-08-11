# 33 — Telas do ADMINISTRADOR

**O que construir:** o painel de quem responde pela plataforma. Ao fim deste ticket o ADMINISTRADOR
ajusta o catálogo, monta o time administrativo, responde a uma pergunta de auditoria sobre quem
baixou o quê, e refaz uma apuração que saiu errada — informando o motivo.

**Bloqueado por:** 19, 21, 22, 24, 30.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **F10, F11** — editar nome do produto e nome, descrição e tempo estimado do relatório; inativar
      produto e relatório. Sigla e código aparecem como **somente leitura** (RF-41).
- [ ] A recusa de RF-47 (soma acima do teto) e a de RF-50 (produto com relatório ativo) chegam ao
      usuário como texto que explica a regra, não como erro genérico.
- [ ] **Não existe botão de criar nem de apagar produto ou relatório** (RN-49). A ausência é
      deliberada e vale um cenário.
- [ ] **F15, RF-35** — gestão de usuários administrativos e remoção de RELATOR.
- [ ] **RF-23, RF-51** — consulta do histórico de downloads, exibindo os identificadores **como
      estavam no momento**, inclusive de relatórios já inativados.
- [ ] **RF-10, RF-11** — solicitar Reprocessamento forçado com **motivo obrigatório**; sem motivo, a
      interface não deixa enviar e a API rejeita de todo jeito.
- [ ] A tela de reprocessamento deixa claro que ele opera **somente sobre a data corrente** (RN-20) e
      que **sobrescreve** os artefatos.
- [ ] **RF-17** — o ADMINISTRADOR enxerga e exporta todos os relatórios, sem passar pela Cadeia
      (RN-24).
- [ ] Nenhuma tela aceita data de referência como entrada (RF-53).
