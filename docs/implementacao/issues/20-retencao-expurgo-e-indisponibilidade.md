# 20 — Retenção, expurgo e indisponibilidade explícita

**O que construir:** o fim de vida do artefato, e o que o usuário lê quando pede algo que já não
existe. Ao fim deste ticket os artefatos somem sozinhos passada a janela de retenção, o sistema
**sabe** que sumiram, a data expurgada desaparece da listagem, e quem pedir mesmo assim recebe uma
mensagem que diz "expirou por retenção" — nunca um erro genérico que faz parecer que o sistema
quebrou.

**Bloqueado por:** 16.

**Status:** ready-for-agent

**Critérios de aceitação**

- [ ] **Spike de conferência primeiro, e é barato.** Na tag de imagem que o Compose fixou: assinar o
      evento, apagar um objeto e observar o webhook disparar. O achado é do branch principal do
      MinIO e o contrato é estável há anos, mas custa minutos conferir (RA-21, arquitetura §14).
- [ ] **RA-21** — a assinatura é a de **remoção de objeto** (`--event delete` →
      `s3:ObjectRemoved:*`), **não** a de ciclo de vida. A documentação do MinIO manda usar o alias
      de ILM, que expande para restore e transition — a expiração **não está lá**. Seguir a
      documentação ao pé da letra produz um webhook que nunca dispara, sem erro algum.
- [ ] **RN-36, RNF-12** — política de ciclo de vida do bucket com janela padrão de **7 dias**,
      configurável por variável de ambiente (RA-20).
- [ ] Está registrado que a retenção efetiva é de ~7 dias e 18 horas: o cálculo trunca para o fim do
      dia em UTC, então com o Ciclo às 03h00 o artefato some por volta das **21h** do sétimo dia —
      nunca antes dos 7 dias prometidos, e o desvio é constante. O que não é intuitivo é o horário.
- [ ] **RA-63** — a marca de expurgo é recebida por **endpoint da API autenticado por credencial de
      serviço**, exposto ao MinIO. É superfície nova e está declarada como tal.
- [ ] **RA-63, segunda metade** — a marca é **autoritativa quando presente**; quando falta, a API
      deriva o estado *expirado* comparando a data de referência com a janela de retenção. Uma
      notificação perdida custa inconsistência transitória, não resposta errada.
- [ ] **RF-24** — solicitação de artefato expurgado é recusada com mensagem **explícita de
      indisponibilidade por retenção** (RN-39). Um cenário afirma isso também **com a notificação
      não entregue**, exercitando a derivação por datas.
- [ ] **RN-37** — a data cujos artefatos foram expurgados deixa de aparecer na listagem.
- [ ] **RN-51, RNF-16** — o expurgo atinge **só os artefatos**. Os metadados de Execução **nunca**
      são expurgados: a métrica primária tem janela de 30 dias e a retenção de artefato é de 7 — sem
      isso, a métrica passa a ser calculada sobre série truncada, sem sinal algum.
