# Guia de estudo

Na avaliação individual vocês vão **explicar o código** (finalidade, entradas, saídas, dependências) e fazer uma
**pequena alteração sem consultar IA**. Este guia organiza o estudo. Cada integrante deve conseguir percorrer um fluxo
inteiro, da tela ao banco, sem olhar nada.

## 1. Ordem de leitura sugerida

1. **`docs/diagrama-classes.png`** e o enunciado (seções 3 e 4): o que existe e quais são as regras.
2. **`model/`** — `HistoriaUsuario` (associações, `cascade`, `orphanRemoval`, `@OrderBy`), `Cenario`, `Passo`, `Sugestao`.
   Pergunte-se: quem é o lado dono da associação? O que `mappedBy` significa? Por que `LAZY`?
3. **`repository/`** — consultas derivadas (`existsByProjetoIdAndTituloIgnoreCase…`) e as `@Query` de contagem.
4. **`service/`**, nesta ordem: `ValidadorCenario` (regra 2, lógica pura) → `RegrasAutoria` → `CenarioService`
   → `PassoService` → `SugestaoService` → `ExportacaoService` e `GeradorGherkin` → `ProjetoService` (consulta e resumo).
5. **`dto/`** e **`controller/`** — como um JSON vira objeto de domínio e volta; `TratadorDeErros` (ProblemDetail).
6. **`src/test/`** — os testes são os cenários BDD da regra escritos em JUnit; `ArquiteturaTest` protege as camadas.
7. **`frontend/src/`** — `api.js` (cabeçalho `X-Usuario-Id`, erros), `App.jsx` (usuário e rotas), `HistoriaPage.jsx`,
   `CenarioEditor.jsx`, `SugestoesPanel.jsx`.

## 2. Perguntas que costumam aparecer (respostas curtas e onde olhar)

| Pergunta | Resposta em uma frase | Onde |
|---|---|---|
| LAZY × EAGER? | LAZY carrega a associação só quando acessada; EAGER carrega junto. O padrão de `@ManyToOne` é EAGER, por isso declaramos LAZY. | `HistoriaUsuario` |
| O que é `open-in-view` e por que está `true`? | Mantém a sessão do JPA aberta até o fim da requisição, para o controller converter entidade em DTO navegando em associações LAZY. É uma simplificação; a alternativa é `JOIN FETCH`/projeções. | `application.yml`, README |
| `cascade = ALL` e `orphanRemoval`? | Persistir/remover o pai propaga aos filhos; remover o filho da coleção o apaga do banco. Usado só nas composições. | `HistoriaUsuario`, `Cenario` |
| Por que o controller não acessa o repositório? | Separação de responsabilidades: regra e transação ficam no serviço. O ArchUnit garante isso. | `ArquiteturaTest` |
| Onde fica a regra "cenário precisa de Quando e Então"? | No serviço (`ValidadorCenario`), não na tela nem no controller. | `ValidadorCenario` |
| Por que DTO e não a entidade no JSON? | Evita expor/serializar o grafo da entidade (ciclos, LAZY) e permite validar a entrada separadamente. | `dto/` |
| 400 × 422 × 409? | 400: dado malformado (Bean Validation). 422: dado bem formado que viola regra de negócio. 409: conflito com o estado (duplicidade, sugestão já decidida). | `TratadorDeErros` |
| O que `@Transactional` faz aqui? | Abre/fecha a transação no método do serviço; falhou, desfaz tudo. `readOnly = true` nas consultas. | `service/` |
| Por que injeção por construtor? | Dependências explícitas, imutáveis e fáceis de testar com mocks. | qualquer serviço |
| O que é CORS e o que é *preflight*? | O navegador só deixa a SPA (origem 5173) chamar a API (8080) se a API liberar a origem; com cabeçalho customizado (`X-Usuario-Id`) ele antes faz uma requisição `OPTIONS`. | `CorsConfig` |
| `X-Usuario-Id` é autenticação? | Não. É identidade simulada: qualquer cliente pode informar qualquer id. | README, `UsuarioService.exigirIdentificado` |
| Como a ordem dos passos é mantida? | Cada passo tem `posicao`; `Cenario.substituirPassos` renumera de 1..n pela ordem da lista; a coleção usa `@OrderBy("posicao ASC")`. | `Cenario`, `PassoService` |
| Os 3 pilares do BDD? | Descoberta (conversar com exemplos), formulação (Gherkin) e automação (testes executáveis). | slides da aula 2 |

## 3. Pequenas alterações para treinar (sem IA!)

Para cada uma: faça, rode `mvn test` (ou a SPA) e confira com `curl`.

1. **Mensagem:** troque o texto do erro "O cenário precisa ter ao menos um passo Então." e ajuste o teste que a verifica.
2. **Regra nova no serviço:** um cenário pode ter no máximo 10 passos → `ValidadorCenario` + teste novo. (Qual código HTTP? Por quê?)
3. **Novo motivo de rejeição** (`DESATUALIZADA`): enum `MotivoRejeicao` + rótulo em `frontend/src/rotulos.js`.
4. **Campo novo na história** (`observacoes`, opcional): entidade → DTOs → serviço (copiar no `atualizar`) → SPA. Quais camadas mudam e por quê?
5. **Filtro novo:** `GET /projetos/{id}/historias?autorId=1` → repositório, serviço, controller.
6. **Regra nova na exportação:** exigir também que todo cenário tenha título com ao menos 5 caracteres → `ExportacaoService` + teste.
7. **ArchUnit:** escreva uma regra nova ("classes em `service` não dependem de `dto`") e faça-a falhar de propósito.
8. **SPA:** mostrar na lista de histórias a data de atualização (precisa de mudança na API também?).

## 4. Autoteste antes da avaliação

- Desenhe, de memória, o diagrama de classes e os cinco pacotes com as setas de dependência permitidas.
- Explique o caminho de "aceitar uma sugestão" do clique ao `UPDATE` no banco. O que acontece se a sugestão já estiver decidida?
- Explique por que "aceitar" **não** cria cenário automaticamente (regra 4) e o que a SPA mostra depois.
- Mostre, com `curl`, um cenário inválido e a resposta `422`. Onde nasceu essa mensagem?
- Por que o teste de "rejeição sem motivo" não precisa de banco de dados?

## 5. Cuidado

O código-base foi gerado por IA a partir do enunciado. Rodem tudo, leiam cada classe e **registrem no
`docs/REGISTRO_IA.md` o que mudaram e por quê**. Código que vocês não conseguem explicar é risco na avaliação individual.
