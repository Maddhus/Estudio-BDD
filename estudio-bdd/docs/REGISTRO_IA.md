# Registro de uso de IA

Registrem cada interação **no dia em que ela acontece**. Mínimo: **6 interações relevantes, 3 da API e 3 da SPA**.
Usem dados fictícios e nunca enviem credenciais. A IA propõe; **o grupo decide e responde pelo código**.

Modelo de cada registro:

> **Interação N — [API | SPA] — data — quem do grupo conduziu**
> - **Problema:** o que precisávamos resolver (e o cenário Dado/Quando/Então que serve de critério).
> - **Ferramenta e prompt:** nome/versão da ferramenta e o prompt usado (ou o trecho principal).
> - **Proposta da IA:** o que ela sugeriu (resumo).
> - **Decisão do grupo:** aceitamos / aceitamos com ajustes / rejeitamos — e **por quê**.
> - **Mudanças realizadas:** arquivos/classes alterados.
> - **Verificação:** como confirmamos que está certo (teste, `mvn test`, chamada com curl, tela, ArchUnit…).

---

## Interação 1 — API — geração do esqueleto do projeto *(pré-preenchida; completem os campos marcados)*

- **Problema:** partir de uma base completa (API em cinco camadas, SPA, testes, documentação) a partir do enunciado.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt: "Consegue fazer tudo isso pra mim pra eu analisar o trabalho completo e
  conseguir estudar e entregar pro professor?", com o enunciado e os slides de BDD anexados.
- **Proposta da IA:** projeto Spring Boot (entidades, repositórios, serviços, controllers, DTOs, tratamento de erros),
  SPA React, testes JUnit/Mockito/ArchUnit, scripts e documentação.
- **Decisão do grupo:** _(preencher: o que foi lido, o que foi alterado, o que foi descartado e por quê)_
- **Mudanças realizadas:** _(preencher)_
- **Verificação:** _(preencher: ex.: "rodamos `mvn test`", "rodamos o roteiro de verificação", "cada integrante explicou a classe X")_

## Interação 2 — API — ...

## Interação 3 — API — ...

## Interação 4 — API — ...

## Interação 5 — SPA — ...

## Interação 6 — SPA — ...

## Interação 7 — SPA — ...

---

## Padrões observados *(o enunciado pede para observar que tipo de contribuição a IA traz)*

- Sugestões úteis (casos de borda, nomes, estrutura): _(preencher)_
- Sugestões genéricas, incorretas ou fora do escopo: _(preencher)_
- Erros que se repetiram e entraram no `AGENTS.md`: _(preencher)_
