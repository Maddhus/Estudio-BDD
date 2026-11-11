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

## Interação 1 — API — geração do esqueleto do projeto

- **Problema:** partir de uma base completa (API em cinco camadas, SPA, testes, documentação) a partir do enunciado.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt: "Consegue me ajudar no que eu preciso fazer nesse trabalho para eu conseguir fazer o mesmo e entregar pro professor?", com o enunciado e os slides de BDD anexados.
- **Proposta da IA:** projeto Spring Boot (entidades, repositórios, serviços, controllers, DTOs, tratamento de erros),
  SPA React, testes JUnit/Mockito/ArchUnit, scripts e documentação.
- **Decisão do grupo:** aceitamos a proposta como ponto de partida, não como entrega pronta. Rodamos os
  testes, estamos estudando cada camada e vamos registrar aqui cada alteração
  feita por nós.
- **Mudanças realizadas:** em 11/11 removemos a pasta duplicada `estudio-bdd/` do repositório e alteramos `frontend/vite.config.js` (`host` e `allowedHosts`, alteração proposta pela IA) para abrir a SPA pelo endereço do GitHub Codespaces; o restante do código-fonte não foi alterado.
- **Verificação:** rodamos `mvn test` em 11/11/2026: 20 testes, 0 falhas (inclui a regra ArchUnit de que controllers não acessam repositórios); rodamos `scripts/roteiro-verificacao.sh`: 67 verificações ok, 0 falhas; reiniciamos a API e os 3 projetos continuaram no banco; abrimos a SPA no Codespaces e testamos bloqueio de exclusão, erro de cenário e liberação da exportação (prints em `docs/evidencias/`)..

## Interação 2 — API — 11/11/2026 — conduzida por Victor — BDD da regra 5 (exportação)

- **Problema:** praticar BDD antes de depender da IA (atividade inicial) e deixar a regra 5 especificada por exemplos. Critério
  de aceite: os cenários da regra 5 devem ser concretos, verificáveis e coerentes com a seção 4 do enunciado.
- **Ferramenta e prompt:** Claude (Anthropic). O grupo escreveu **sem IA** uma história e 3 cenários (original em
  `docs/bdd-a-mao.md`) e colou na conversa. Prompts seguintes: pedir a revisão e a ampliação e, depois, "Seus três cenários e a
  história reescritos com os 4 ajustes. Você consegue fazer isso por mim?".
- **Proposta da IA:** (1) apontou 4 ajustes (`# language: pt` e `Cenário:` sem número; títulos que dizem o resultado esperado;
  resultado verificável no cenário de sucesso; "Para…" mais claro); (2) reescreveu a história e os 3 cenários; (3) propôs 7
  sugestões de ampliação (S1 a S7), de qualidade mista de propósito.
- **Decisão do grupo:** aceitamos a versão revisada, escrita pela IA, mantendo a estrutura, os nomes e os casos do grupo.
  Das 7 sugestões, **aceitamos S1 e S7**; **rejeitamos** S2 (DUPLICADA), S3 (FORA_DO_ESCOPO), S4 (FORA_DO_ESCOPO), S5 (GENERICA)
  e S6 (DUPLICADA). Depois da conversa com a IA mudamos duas decisões: S4 passou de ACEITA para REJEITADA/FORA_DO_ESCOPO
  (nada no enunciado pede registrar quem exportou e não há entidade para guardar isso) e o motivo de S6 passou de GENERICA
  para DUPLICADA (é cópia do cenário 2). S2 e S6 foram julgadas em relação à versão revisada.
- **Mudanças realizadas:** `docs/bdd-a-mao.md` completo; S1 e S7 viram os cenários 4 e 5 da regra 5 (ao cadastrá-los no
  sistema, marcar a origem como `SUGESTAO_IA`, conforme a regra 4). O código não foi alterado.
- **Verificação:** cada sugestão foi conferida contra a seção 4 do enunciado; a decisão de S4 foi explicada pelo grupo com
  suas palavras. Os cenários 2, 3 e 5 correspondem a `ExportacaoServiceTest` e `ExportacaoService`; Os cenários 1 a 4 correspondem a testes de `ExportacaoServiceTest`; o cenário 5 está implementado em `ExportacaoService.exportar`, que checa os cenários primeiro, e não tem teste próprio. `mvn test` em 11/11/2026: 20 testes, 0 falhas.

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
