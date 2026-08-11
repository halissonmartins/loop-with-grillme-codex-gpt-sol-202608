# O catálogo é derivado do código

Produto e Relatório não são criados pela aplicação. Cada módulo processador **é** um produto
(RA-04) e traz os seus JRXML (RA-07); ao iniciar, publica a si e aos seus relatórios no schema de
controle. A aplicação edita apenas o que é genuinamente mutável — nome, descrição e tempo estimado
— e inativa; nunca cria nem apaga.

## Considered Options

- **Catálogo como dado livre**, com telas de cadastro de produto e relatório, como o PRD anterior
  previa. Rejeitada: cadastrar `SEGUROS` criaria uma sigla sem módulo, sem base, sem JRXML e sem
  task na DAG; sob a reserva do ciclo (RN-45), o cadastro geraria execuções que nenhum contêiner
  apuraria, e **todas virariam erro** — um cadastro derrubaria a métrica primária.
- **Catálogo como dado, validado contra a lista de módulos conhecidos.** Rejeitada: é o
  auto-registro com um passo manual a mais, e a tela resultante "cadastra" apenas o que já existe.
- **Catálogo derivado do código** — escolhida.

## Consequences

- F10 e F11 encolhem para edição e inativação; RF-25, RF-26, RF-28 e RF-42 são aposentados; a
  matriz de perfis do PRD §3.2 perde quatro linhas.
- A unicidade do código dentro do produto (RN-03) deixa de ser validação de formulário e vira
  **validação de inicialização**: código duplicado impede o módulo de subir. A violação passa a ser
  impossível de persistir.
- Some a classe inteira de defeito "o catálogo diz uma coisa e o código faz outra".
- Adicionar um produto passa a exigir um PR — o que é honesto, porque já exigia de qualquer forma.
