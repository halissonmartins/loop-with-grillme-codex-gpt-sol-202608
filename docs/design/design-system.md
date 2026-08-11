# Design system

Todo valor visual de interface deve vir destes tokens. Não são admitidos cores, tipografia,
espaçamentos, raios ou sombras fora desta lista sem atualizar este documento.

## Tokens

| Categoria | Token | Valor |
|---|---|---|
| Cor | `color.text.primary` | `#14213D` |
| Cor | `color.text.secondary` | `#526276` |
| Cor | `color.surface.default` | `#FFFFFF` |
| Cor | `color.surface.page` | `#F5F7FB` |
| Cor | `color.action.primary` | `#203A69` |
| Cor | `color.feedback.success` | `#27824B` |
| Cor | `color.feedback.warning` | `#A46600` |
| Cor | `color.feedback.danger` | `#C83E3E` |
| Cor | `color.border.default` | `#AAB6C7` |
| Tipografia | `font.family.base` | `Arial, sans-serif` |
| Tipografia | `font.size.body` | `16px` |
| Tipografia | `font.size.small` | `14px` |
| Tipografia | `font.size.title` | `28px` |
| Espaçamento | `space.1` a `space.6` | `4px`, `8px`, `12px`, `16px`, `24px`, `32px` |
| Raio | `radius.control` / `radius.card` | `6px` / `10px` |
| Sombra | `shadow.card` | `0 2px 8px #14213D0D` |

## Estados canônicos

| Estado | Apresentação | Regra |
|---|---|---|
| Carregando | Texto “Carregando…” e indicador não textual | Mantém contexto e não simula sucesso. |
| Vazio | Explica a ausência e a próxima ação possível | Diferente de erro; caso pendente de vínculo usa a mensagem do fluxo 2. |
| Erro | Faixa `color.feedback.danger` com momento, descrição e Correlation ID | Nunca usa “erro genérico”. |
| Sucesso | Confirma a ação em `color.feedback.success` | Não esconde o resultado persistido. |
| Desabilitado | Controle visualmente atenuado e semanticamente desabilitado | Explica o motivo quando a ação depende de seleção ou permissão. |

## Acessibilidade

- Contraste mínimo WCAG AA para texto e controles.
- Todo elemento interativo recebe foco visível com `color.action.primary`.
- Todo campo possui `label` associado; texto de ajuda não substitui rótulo.
- Mensagens de estado usam texto, não somente cor ou ícone.
- Interface e mensagens permanecem em pt-BR.
