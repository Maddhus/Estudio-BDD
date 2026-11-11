# Atividade inicial: BDD à mão e primeira ampliação por IA

Data: 11/11/2026 · Regra escolhida: **regra 5 (exportação em `.feature`)** · Ferramenta de IA: Claude (Anthropic)

Este documento guarda as quatro etapas da atividade, **separando o que é do grupo do que veio da IA**:

| Etapa | Autoria |
|---|---|
| 1. Versão original (sem IA) | **Grupo** |
| 2. Versão revisada (4 ajustes) | **Claude**, a pedido do grupo; os ajustes foram apontados pela IA |
| 3. Sete sugestões de ampliação (S1 a S7) | **Claude** |
| 4. Decisões sobre as sugestões e cenários finais | **Grupo** (decidiu); a IA só corrigiu o motivo de duas decisões |

---

## Passo 1 — Regra escolhida

**Regra 5 do enunciado:** somente histórias com ao menos um cenário e sem sugestões pendentes podem ser exportadas em
`.feature`, em Gherkin com `# language: pt`.

## Passo 2 — Versão original, escrita pelo grupo SEM IA

Transcrita exatamente como foi escrita (com os problemas que a revisão apontou).

```gherkin
Funcionalidade: Exportação de História
Como analista
Quero exportar uma história para um arquivo .feature
Para poder acompanhar melhor o resultado das histórias de usuário

Cenário 1: Exportar história válida (Sucesso)
  Dado a história "Carrinho de Compras" que tem 2 cenários cadastrados
  E não possui nenhuma sugestão pendente
  Quando o analista solicita a exportação
  Então a história é exportada com sucesso como um arquivo .feature

Cenário 2: Tentar exportar história sem cenários (Falha)
  Dado a história "Pagamento PIX" que é feita sem cenários
  Quando o analista solicita a exportação
  Então a exportação é bloqueada
  E a mensagem informa que o analista deve adicionar um cenário

Cenário 3: Tentar exportar história com sugestão pendente (Falha)
  Dado a história "Perfil de Usuário" que tem 1 cenário cadastrado
  Mas possui uma sugestão da IA com o status de pendente
  Quando o analista solicita a exportação
  Então a exportação é bloqueada
  E a mensagem informa que todas as sugestões precisam ser decididas
```

**O que a revisão apontou:**

1. Faltava `# language: pt`, e `Cenário 1:` não é palavra-chave (o correto é `Cenário:`).
2. Os títulos traziam "(Sucesso)" e "(Falha)"; devem dizer o resultado esperado.
3. "Exportada com sucesso" não é verificável: é preciso dizer o que se confere no arquivo gerado.
4. O "Para…" ("acompanhar melhor o resultado") era vago: não dizia quem usa o arquivo nem para quê.

**O que o grupo acertou:** 1 cenário de sucesso e 2 de falha; dados concretos; um comportamento por cenário; nada
de telas ou cliques; uso correto do `Mas` no cenário 3.

## Passo 3 — Versão revisada (escrita pela IA a pedido do grupo) e sugestões de ampliação

**Prompt:** o grupo colou a história e os 3 cenários e pediu revisão e ampliação. Depois pediu que a IA reescrevesse
os cenários com os 4 ajustes. A IA entregou a versão abaixo e 7 sugestões de novos cenários (S1 a S7).

### 3a. Versão revisada

```gherkin
# language: pt
Funcionalidade: Exportação de história
  Como analista
  Quero exportar uma história para um arquivo .feature
  Para compartilhar os critérios de aceite com o time de desenvolvimento e de testes

  Cenário: História com cenários e sem pendências é exportada como arquivo .feature
    Dado a história "Carrinho de Compras" com 2 cenários cadastrados
    E nenhuma sugestão pendente
    Quando o analista solicita a exportação
    Então o arquivo gerado começa com "# language: pt"
    E contém "Funcionalidade: Carrinho de Compras"
    E contém os 2 cenários da história

  Cenário: História sem nenhum cenário não é exportada
    Dado a história "Pagamento PIX" sem nenhum cenário cadastrado
    Quando o analista solicita a exportação
    Então a exportação é bloqueada
    E a mensagem informa que é preciso cadastrar ao menos um cenário

  Cenário: História com sugestão pendente não é exportada
    Dado a história "Perfil de Usuário" com 1 cenário cadastrado
    Mas com 1 sugestão da IA pendente
    Quando o analista solicita a exportação
    Então a exportação é bloqueada
    E a mensagem informa que todas as sugestões precisam ser aceitas ou rejeitadas antes de exportar
```

### 3b. Sugestões da IA (S1 a S7)

| # | Título da sugestão | Resumo do conteúdo |
|---|---|---|
| S1 | Exportação é liberada quando as sugestões já foram todas decididas | "Carrinho de Compras" com 2 cenários, 1 sugestão aceita e 1 rejeitada → o arquivo é gerado |
| S2 | Arquivo exportado traz a história e seus cenários em português | Arquivo começa com `# language: pt`, traz `Funcionalidade: Carrinho de Compras` e os 2 cenários na ordem |
| S3 | Exportação é bloqueada para quem não é o autor | "Perfil de Usuário" de Ana; Bruno tenta exportar → bloqueado |
| S4 | Exportação registra quem exportou | Ao exportar, o sistema registra usuário, data e hora |
| S5 | Exportação funciona com dados válidos | "Dado uma história válida… então o resultado é o esperado" |
| S6 | Exportação é bloqueada quando a história não possui cenários | "Pagamento PIX" sem cenários → exportação bloqueada (sem mensagem) |
| S7 | Exportação é bloqueada quando falta cenário e há sugestão pendente | "Pagamento PIX" sem cenários e com 1 pendente → bloqueada; a mensagem informa que falta um cenário |

## Passo 4 — Decisões do grupo

Critério: cada sugestão foi julgada contra a **seção 4 do enunciado**, porque a IA não conhece o trabalho do grupo a fundo.

A decisão de **S2 e S6 (duplicadas) foi tomada em relação à versão revisada** (3a). Em relação à versão **original** (passo 2),
S2 seria ACEITA, pois resolvia o item 3 da revisão (resultado não verificável).

| # | Decisão inicial do grupo | Decisão final | Motivo | Justificativa |
|---|---|---|---|---|
| S1 | ACEITA | **ACEITA** | – | _(rascunho; reescrever com suas palavras)_ Caso de borda que faltava: só sugestões pendentes bloqueiam, as já decididas não |
| S2 | REJEITADA, DUPLICADA | **REJEITADA** | DUPLICADA | _(rascunho; reescrever)_ Já coberta pelo cenário 1 revisado (conteúdo do arquivo exportado) |
| S3 | REJEITADA, FORA_DO_ESCOPO | **REJEITADA** | FORA_DO_ESCOPO | _(rascunho; reescrever)_ A regra 3 restringe só edição e exclusão; exportar não exige ser autor |
| S4 | ACEITA | **REJEITADA** | FORA_DO_ESCOPO | Nada no enunciado pede para registrar quem exportou e em qual horário a exportação foi feita, além de não haver nenhuma entidade para guardar isso |
| S5 | REJEITADA, GENERICA | **REJEITADA** | GENERICA | _(rascunho; reescrever)_ "História válida" e "resultado esperado" não são verificáveis |
| S6 | REJEITADA, GENERICA | **REJEITADA** | DUPLICADA | É uma cópia do cenário 2 (o mesmo caso, com um Então mais fraco, sem a mensagem) |
| S7 | ACEITA | **ACEITA** | – | _(rascunho; reescrever)_ Caso de borda: duas violações ao mesmo tempo; o sistema checa os cenários primeiro |

**Decisões que mudaram depois da conversa com a IA:**

- **S4:** de ACEITA para REJEITADA, FORA_DO_ESCOPO. O grupo concordou que a regra 5 só define *quando* e *em que formato*
  exportar. Registrar exportações exigiria inventar uma entidade e uma funcionalidade que ninguém pediu.
- **S6:** o motivo mudou de GENERICA para DUPLICADA. A sugestão não é vaga (tem dado concreto e comportamento claro); ela
  repete o cenário 2. GENERICA fica para casos como o S5, que não dá para verificar.

## Cenários finais do grupo (regra 5)

Os cenários 1 a 3 são a versão revisada (3a). Os cenários **4 e 5** vêm das sugestões **aceitas** S1 e S7.

> Pela regra 4, **aceitar não altera os cenários automaticamente**: o grupo escreveu os cenários 4 e 5 e, ao cadastrá-los
> no sistema, marca a origem como `SUGESTAO_IA`. O cenário 4 teve o `Então` ajustado em relação ao texto da S1 ("o arquivo
> .feature é gerado" não é verificável, o mesmo problema do item 3 da revisão).

```gherkin
# language: pt
Funcionalidade: Exportação de história
  Como analista
  Quero exportar uma história para um arquivo .feature
  Para compartilhar os critérios de aceite com o time de desenvolvimento e de testes

  Cenário: História com cenários e sem pendências é exportada como arquivo .feature
    Dado a história "Carrinho de Compras" com 2 cenários cadastrados
    E nenhuma sugestão pendente
    Quando o analista solicita a exportação
    Então o arquivo gerado começa com "# language: pt"
    E contém "Funcionalidade: Carrinho de Compras"
    E contém os 2 cenários da história

  Cenário: História sem nenhum cenário não é exportada
    Dado a história "Pagamento PIX" sem nenhum cenário cadastrado
    Quando o analista solicita a exportação
    Então a exportação é bloqueada
    E a mensagem informa que é preciso cadastrar ao menos um cenário

  Cenário: História com sugestão pendente não é exportada
    Dado a história "Perfil de Usuário" com 1 cenário cadastrado
    Mas com 1 sugestão da IA pendente
    Quando o analista solicita a exportação
    Então a exportação é bloqueada
    E a mensagem informa que todas as sugestões precisam ser aceitas ou rejeitadas antes de exportar

  Cenário: História com sugestões já decididas é exportada
    Dado a história "Carrinho de Compras" com 2 cenários cadastrados
    E 1 sugestão aceita e 1 sugestão rejeitada
    Quando o analista solicita a exportação
    Então o arquivo gerado contém os 2 cenários da história

  Cenário: História sem cenários e com sugestão pendente não é exportada
    Dado a história "Pagamento PIX" sem nenhum cenário cadastrado
    E com 1 sugestão da IA pendente
    Quando o analista solicita a exportação
    Então a exportação é bloqueada
    E a mensagem informa que é preciso cadastrar ao menos um cenário
```

## Ligação com o código (para estudar)

| Cenário | Onde aparece no projeto |
|---|---|
| 1 e 4 (exporta) | `ExportacaoServiceTest.historiaSemPendenciasGeraFeatureEmPortugues` (o teste tem uma sugestão **rejeitada**, que é o caso do cenário 4) e `GeradorGherkin` |
| 2 (sem cenários) | `ExportacaoServiceTest.exportacaoBloqueadaSemCenarios`; regra em `ExportacaoService.exportar` |
| 3 (pendente) | `ExportacaoServiceTest.exportacaoBloqueadaComSugestaoPendente`; regra em `ExportacaoService.exportar` |
| 5 (as duas violações) | `ExportacaoService.exportar` verifica **os cenários primeiro**, então a mensagem é a de falta de cenário |

Executados em 11/11/2026 com mvn test: 20 testes, 0 falhas.git

## O que foi útil e o que foi genérico

- **Útil:** S1 e S7, dois casos de borda que o grupo não tinha escrito (sugestões já decididas não bloqueiam; duas
  violações ao mesmo tempo). S2 também era útil, mas o conteúdo já tinha entrado na revisão.
- **Genérica:** S5, sem dado concreto e sem resultado verificável.
- **Fora do escopo:** S3 e S4, sugestões plausíveis que o enunciado não pede.
- **Duplicada:** S2 e S6, repetiam algo já coberto.
