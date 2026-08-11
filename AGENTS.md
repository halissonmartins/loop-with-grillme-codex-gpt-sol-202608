# Referências

- Glossário (fonte única das definições do domínio): @docs/glossario.md
- PRD: @docs/prd.md
- Arquitetura inicial: @docs/arquitetura-inicial.md
- Decisões arquiteturais: `docs/adr/`
- Guia de implementação: @docs/guias/guia-app-web.md

Um termo do domínio é definido **apenas** no glossário. O PRD e a arquitetura usam os termos e
não os redefinem.

# Workflow de implementação (importante):
- Uma ISSUE por vez, mesmo que ela esteja "bloqueada por nada"
- Planeje a ISSUE
- Se tiver codifição, implemente cada item do plano usando Desenvolvimento Orientado a Testes (TDD)
- Certifique que todos os cenários referenciados foram implementados 
- Se tiver codifição, certifique que o projeto ou módulo executa sua construção com sucesso
- Inicie um agente para fazer a revisão. Verificar o item foi implementado conforme descrito na documentação e também classificar cada item encontrado por criticidade
- Caso o agente encontre algo na revisão: analisar, corrigir, validar e construir
- Marque o critério de aceitação que foi entregue como concluído
- Fazer commits pequenos e atômicos, onde cada commit deve focar em apenas uma tarefa lógica
- Após concluir complementamente todos os itens da ISSUE, fazer o push

# Diretrizes

Diretrizes comportamentais para reduzir erros comuns. Combine com instruções específicas do projeto, conforme necessário.

**Contraponto:** Estas diretrizes priorizam a cautela em detrimento da velocidade. Para tarefas triviais, use o bom senso.

## 1. Pense antes de fazer

**Não faça suposições. Não esconda a confusão. Apresente as vantagens e desvantagens.**

Antes da implementação:
- Exponha suas suposições explicitamente. Em caso de dúvida, pergunte.
- Se existirem múltiplas interpretações, apresente-as - não escolha em silêncio.
- Se existir uma abordagem mais simples, diga-a. Questione-a quando necessário.
- Se algo não estiver claro, pare. Nomeie o que está causando confusão. Pergunte.

## 2. Simplicidade em Primeiro Lugar

**Código mínimo que resolve o problema. Nada de especulações.**

- Sem funcionalidades além das solicitadas.
- Sem abstrações para código de uso único.
- Nenhuma "flexibilidade" ou "configurabilidade" que não tenha sido solicitada.
- Se você escrever 200 linhas e elas poderiam ser reduzidas a 50, reescreva.

Pergunte a si mesmo: "Um sênior diria que isso é muito complicado?" Se sim, simplifique.

## 3. Execução Orientada a Objetivos

**Defina os critérios de sucesso. Repita o processo até que sejam verificados.**

Transformar tarefas em objetivos verificáveis:
- "Adicionar validação" → "Escrever testes para entradas inválidas e, em seguida, fazê-los passar"
- "Corrigir o erro" → "Escreva um teste que o reproduza e, em seguida, faça com que ele seja aprovado"

Critérios de sucesso robustos permitem que você crie ciclos independentes. Critérios fracos ("faça funcionar") exigem esclarecimentos constantes.

---

**Estas diretrizes estão funcionando se:** houver menos alterações desnecessárias nas diferenças, menos reescritas devido à complexidade excessiva e as perguntas para esclarecimento forem feitas antes.
