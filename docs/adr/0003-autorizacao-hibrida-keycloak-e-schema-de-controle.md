# Autorização híbrida entre Keycloak e schema de controle

A cadeia **Relatório → Role de relatório → Grupo → Usuário** não cabe inteira no Keycloak, porque
o Keycloak não conhece o conceito de Relatório. Ficou dividida assim: **Perfil** é *realm role*,
**Role de relatório** é *client role* de um cliente dedicado (`relatorios`), **Grupo** e
pertinência são nativos, e o elo **Relatório → Role** é tabela no schema de controle.

## Considered Options

- **Tudo no Keycloak** (RA-31 original). Impossível como escrito.
- **Tudo em tabela própria**, com o Keycloak apenas autenticando. Vantagem real: as telas do
  GERENTE virariam CRUD comum, sem Admin REST API, e a aplicação não precisaria de credencial
  administrativa nenhuma. Rejeitada por descartar recursos nativos (grupos, mapeamento de roles) e
  tirar do Keycloak a fonte da verdade da autorização.
- **Híbrida, com separação realm role / client role** — escolhida.

## Consequences

- **RN-26 vira estrutural.** Os endpoints do GERENTE operam apenas sobre client roles do cliente
  `relatorios`; um Perfil está em outro espaço de nomes e outro endpoint, e não é alcançável nem
  por engano. Antes, a única barreira entre um GERENTE e a própria promoção era uma verificação no
  código — e uma verificação esquecida é escalada de privilégio.
- **Risco aceito e deliberado:** restringir o service account a gerir um único cliente depende de
  *fine-grained admin permissions*, que é **preview** no Keycloak 26.5.2. Não apoiamos a segurança
  do sistema num recurso preview, então o service account recebe `manage-users` e `manage-clients`.
  **A API tem, tecnicamente, poder de criar um ADMINISTRADOR**; o que a impede é o nosso código,
  reforçado pela separação de espaços de nomes acima. Quem revisar isto no futuro deve saber que a
  escolha foi consciente, e que a alternativa era esperar o recurso sair de preview.
- A resolução da listagem é uma consulta só: os relatórios cujas roles estão no
  `resource_access.relatorios.roles` do JWT.
