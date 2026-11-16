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
- **Ferramenta e prompt:** Claude (Anthropic). Prompt: "Consegue me ajudar me explicando o que eu preciso fazer nesse trabalho para eu conseguir fazer o mesmo e entregar pro professor?", com o enunciado e os slides de BDD anexados.
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

## Interação 3 — API — 12/11/2026 — conduzida por Maria Eduarda — API no ar para a SPA no Codespaces (porta 8080 e CORS)

- **Problema:** a SPA, rodando no Codespaces, precisava chamar a API. `mvn spring-boot:run` falhava com "Port 8080 was already in use" (uma API antiga continuava rodando) e a API só liberava CORS para `http://localhost:5173`. Critério: a SPA lista os projetos vindos da API.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt (trecho): "ela está ocupada pois eu subi de novo pra poder listar ela como pública! é a partir dai que você tem que me dizer o que fazer", com os logs colados na conversa.
- **Proposta da IA:** parar a API antiga, subir a nova com `CORS_ORIGINS` apontando para o endereço da SPA no Codespaces, deixar a porta 8080 pública e conferir o CORS com `curl` enviando o cabeçalho `Origin`.
- **Decisão do grupo:** aceitamos. A configuração por variável de ambiente (`app.cors.allowed-origins`) já existia no código, então não foi preciso alterá-lo.
- **Mudanças realizadas:** nenhuma no código; só a forma de subir a API e a visibilidade da porta 8080 no Codespace.
- **Verificação:** o `curl` com a origem da SPA devolveu `Access-Control-Allow-Origin` com o endereço da SPA, e `GET /api/projetos` respondeu com os projetos.

## Interação 4 — API — 12/11 — conduzida por Maria Eduarda — onde fica o banco e como inspecioná-lo

- **Problema:** saber onde estão os dados fictícios, se era preciso usar um banco externo e como mostrar o banco caso o professor peça.
- **Ferramenta e prompt:** Claude (Anthropic). Prompts (trechos): "Onde está o banco de dados dos dados ficticios que voce botou no programa? Nao sei se eu deveria usar um banco de dados externo ou não..." e "eu queria olhar no banco de dados pra saber onde achar ele no arquivo caso o processo peça, sabe?".
- **Proposta da IA:** o PostgreSQL roda em um contêiner Docker definido no `docker-compose.yml`, com os dados em um volume; os dados fictícios são criados por `DadosFicticios` só quando o banco está vazio; não usar banco externo (o trabalho proíbe credenciais no repositório); inspecionar com `docker exec … psql`, `\dt` e `\d sugestao`.
- **Decisão do grupo:** aceitamos manter o banco local no Docker. Não geramos o arquivo de dump, que era opcional.
- **Mudanças realizadas:** nenhuma no código.
- **Verificação:** `select … from sugestao` no `psql` mostrou 5 linhas: as 4 de exemplo e a sugestão de teste feita pela SPA.

## Interação 5 — SPA — 12/11/2026 — conduzida por Maria Eduarda — abrir a SPA pelo endereço do Codespaces

- **Problema:** a SPA não abria pelo endereço `*.app.github.dev`, porque o Vite recusa hosts que não estejam na lista de permitidos, e a porta precisava ser fixa por causa do CORS. Critério: abrir a tela "Quem está usando?" no navegador.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt (trecho): "alterei pra -5173 na url e deu isso aqui", com o print do erro.
- **Proposta da IA:** configurar `server: { port: 5173, strictPort: true, host: true, allowedHosts: [".app.github.dev"] }` no `vite.config.js` e rodar a SPA com `VITE_API_URL` apontando para a porta 8080 do Codespace.
- **Decisão do grupo:** aceitamos. É uma alteração mínima de configuração, sem mudar telas nem regras.
- **Mudanças realizadas:** `frontend/vite.config.js`.
- **Verificação:** a SPA abriu pelo globo da porta 5173, e navegamos Ana → Loja Virtual → Login de cliente com dados vindos da API.

## Interação 6 — SPA — 15/11/2026 — conduzida por Victor — testes do fluxo na tela e evidências do roteiro

- **Problema:** a linha 12 do roteiro exige verificar o fluxo pela SPA e anexar evidências; as linhas 9 e 11 também precisam de evidência guardada.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt (trecho): "eu tenho que ter print de todos esses testes? botei só 3".
- **Proposta da IA:** testar na tela o bloqueio de não autor (regra 3), o erro de cenário inválido (regra 2) e a exportação bloqueada e liberada (regra 5); guardar os prints em `docs/evidencias/`; gerar `mvn-test.txt` e `persistencia-apos-reinicio.txt` para as linhas 11 e 9.
- **Decisão do grupo:** aceitamos e refizemos o `mvn test` e o reinício da API em 13/11 para as datas e arquivos ficarem coerentes.
- **Mudanças realizadas:** coluna "Resultado observado" do `docs/ROTEIRO_VERIFICACAO.md` e arquivos em `docs/evidencias/` (prints `spa-*.jpeg`, `mvn-test.txt`, `persistencia-apos-reinicio.txt`). O código não foi alterado.
- **Verificação:** comparamos cada resultado com a coluna "Resultado esperado" do roteiro; `mvn test` em 15/11: 20 testes, 0 falhas.

## Interação 7 — SPA — 15/11/2026 — conduzida por Victor — sugestão de teste que não aparecia

- **Problema:** uma sugestão registrada pela SPA para teste não aparecia, e não sabíamos se tinha sido gravada.
- **Ferramenta e prompt:** Claude (Anthropic). Prompt (trecho): "nao estou conseguindo ver o banco e a sugestão teste que fiz".
- **Proposta da IA:** conferir no banco com `select … from sugestao`, depois abrir a história certa pelo endereço `#/historias/ID` e conferir o filtro "Mostrar" do painel de sugestões.
- **Decisão do grupo:** aceitamos. A sugestão estava gravada (id 5, REJEITADA, história 4) e apareceu na SPA depois dessas conferências.
- **Mudanças realizadas:** nenhuma no código.
- **Verificação:** o `select` mostrou a sugestão no banco e ela apareceu no painel da história.

---

## Padrões observados 

- Sugestões úteis (casos de borda, nomes, estrutura): S1 e S7 do BDD, dois casos de borda que o grupo não tinha escrito (sugestões já decididas não bloqueiam a exportação; duas violações ao mesmo tempo); a configuração `allowedHosts` do Vite para abrir a SPA no Codespaces.
- Sugestões genéricas, incorretas ou fora do escopo: S5 (genérica, sem dado concreto nem resultado verificável); S3 e S4 (fora do escopo: a regra 3 não restringe a exportação e o enunciado não pede registrar quem exportou); S2 e S6 (duplicadas).
- Erros que se repetiram e entraram no `AGENTS.md`: o `vite.config.js` gerado não liberava o host do Codespaces; o roteiro testa CORS com `http://localhost:5173` e falha se a API estiver rodando com `CORS_ORIGINS` do Codespace.
