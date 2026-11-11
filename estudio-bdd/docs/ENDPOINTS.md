# Documentação dos endpoints

Base: `http://localhost:8080/api` · Formato: JSON (`application/json`) · Erros: `application/problem+json`.

**Identidade simulada.** As operações que alteram dados recebem o usuário atual no cabeçalho `X-Usuario-Id` (id de um
usuário existente). Isso **não é autenticação**: qualquer cliente pode informar qualquer id. Sem o cabeçalho (ou com um id
inexistente) a API responde `401`. As consultas (`GET`) não exigem o cabeçalho.

## Códigos de resposta usados

| Código | Quando | Exceção no código |
|---|---|---|
| 200 / 201 / 204 | Sucesso (consulta e edição / criação / exclusão) | – |
| 400 | Campos inválidos (Bean Validation) ou JSON/enum ilegível | `MethodArgumentNotValidException` |
| 401 | `X-Usuario-Id` ausente ou de usuário inexistente | `UsuarioNaoIdentificadoException` |
| 403 | Usuário identificado, mas não é o autor da história | `AcessoNegadoException` |
| 404 | Recurso inexistente | `RecursoNaoEncontradoException` |
| 409 | Duplicidade (e-mail, nome do projeto, título da história/cenário) ou sugestão já decidida | `ConflitoException` |
| 422 | Regra de negócio violada (gramática do cenário, rejeição sem motivo, exportação bloqueada…) | `RegraNegocioException` |

Formato de erro (ProblemDetail, RFC 9457):

```json
{
  "type": "urn:estudio-bdd:erro:regra-de-negocio",
  "title": "Regra de negócio violada",
  "status": 422,
  "detail": "Cenário inválido: O cenário precisa ter ao menos um passo Então.",
  "timestamp": "2026-11-09T19:30:00Z"
}
```

Erros de validação (`400`) trazem também a lista `erros`: `[{"campo": "beneficio", "mensagem": "O benefício (para...) é obrigatório."}]`.

## Usuários

| Método e caminho | Cabeçalho | Corpo | Resposta |
|---|---|---|---|
| `GET /usuarios` | – | – | 200, lista de usuários |
| `GET /usuarios/{id}` | – | – | 200 / 404 |
| `POST /usuarios` | – | `{nome, email}` | 201 / 400 / 409 (e-mail repetido) |

## Projetos

| Método e caminho | Cabeçalho | Corpo | Resposta |
|---|---|---|---|
| `GET /projetos` | – | – | 200, lista |
| `POST /projetos` | – | `{nome, descricao?, contextoDominio?}` | 201 / 400 / 409 (nome repetido) |
| `GET /projetos/{id}` | – | – | 200 / 404 |
| `PUT /projetos/{id}` | – | igual ao POST | 200 / 400 / 404 / 409 |
| `DELETE /projetos/{id}` | – | – | 204 / 404 / 422 (projeto com histórias não é excluído) |
| `GET /projetos/{id}/historias?prioridade=ALTA` | – | – | 200: histórias com `totalCenarios` e `sugestoesPendentes` (**consulta do projeto**, regra 6) |
| `POST /projetos/{id}/historias` | `X-Usuario-Id` | `{titulo, papel, objetivo, beneficio, prioridade}` | 201 / 400 / 401 / 404 / 409 |
| `GET /projetos/{id}/resumo` | – | – | 200: `totalHistorias`, `cenariosPorOrigem`, `sugestoesPorDecisao`, `motivosRejeicaoMaisFrequentes` (**resumo**, regra 7) |

`prioridade`: `ALTA`, `MEDIA` ou `BAIXA`.

## Histórias

| Método e caminho | Cabeçalho | Corpo | Resposta |
|---|---|---|---|
| `GET /historias/{id}` | – | – | 200: história com cenários, passos e `sugestoesPendentes` / 404 |
| `PUT /historias/{id}` | `X-Usuario-Id` | igual ao POST de criação | 200 / 400 / 401 / **403 (não autor)** / 404 / 409 |
| `DELETE /historias/{id}` | `X-Usuario-Id` | – | 204 / 401 / **403** / 404 (remove cenários, passos e sugestões) |
| `GET /historias/{id}/feature` | – | – | 200 `text/plain` (arquivo `.feature`, com `Content-Disposition`) / **422 se sem cenários ou com sugestão pendente** |

## Cenários e passos

| Método e caminho | Cabeçalho | Corpo | Resposta |
|---|---|---|---|
| `GET /historias/{id}/cenarios` | – | – | 200, lista ordenada por posição |
| `POST /historias/{id}/cenarios` | `X-Usuario-Id` | `{titulo, origem?, passos:[{tipo, texto}]}` | 201 / 400 / 401 / 403 / 404 / 409 (título repetido) / **422 (gramática)** |
| `GET /cenarios/{id}` | – | – | 200 / 404 |
| `PUT /cenarios/{id}` | `X-Usuario-Id` | igual ao POST (substitui **todos** os passos, na ordem da lista) | 200 / 400 / 401 / 403 / 404 / 409 / 422 |
| `DELETE /cenarios/{id}` | `X-Usuario-Id` | – | 204 / 401 / 403 / 404 |
| `POST /cenarios/{id}/passos` | `X-Usuario-Id` | `{tipo, texto, posicao?}` (1 = primeiro; sem posição vai para o fim) | 201 / 401 / 403 / 404 / 422 |
| `GET /passos/{id}` | – | – | 200 / 404 |
| `PUT /passos/{id}` | `X-Usuario-Id` | `{tipo, texto, posicao?}` (move o passo se a posição for informada) | 200 / 401 / 403 / 404 / 422 |
| `DELETE /passos/{id}` | `X-Usuario-Id` | – | 204 / 401 / 403 / 404 / 422 (o cenário ficaria inválido) |

- `tipo`: `DADO`, `QUANDO`, `ENTAO`, `E`, `MAS`. `origem`: `HUMANO` (padrão) ou `SUGESTAO_IA`.
- **Regra 2 (validada no serviço, em toda criação/edição):** ao menos um `QUANDO` e um `ENTAO`; ordem
  Dado → Quando → Então; `E`/`MAS` continuam o tipo anterior e não iniciam o cenário.
- Dentro de `POST/PUT /cenarios`, a ordem é a da lista `passos` (o campo `posicao` é ignorado). As posições são
  renumeradas a partir de 1 a cada alteração.

## Sugestões

| Método e caminho | Cabeçalho | Corpo | Resposta |
|---|---|---|---|
| `GET /historias/{id}/sugestoes?decisao=PENDENTE` | – | – | 200 (`decisao` opcional: `PENDENTE`, `ACEITA`, `REJEITADA`) |
| `POST /historias/{id}/sugestoes` | `X-Usuario-Id` | `{ferramenta, prompt, conteudo}` | 201 (nasce `PENDENTE`) / 400 / 401 / 404 |
| `GET /sugestoes/{id}` | – | – | 200 / 404 |
| `POST /sugestoes/{id}/aceitar` | `X-Usuario-Id` | `{justificativa?}` (corpo opcional) | 200 / 401 / 404 / **409 (já decidida)** |
| `POST /sugestoes/{id}/rejeitar` | `X-Usuario-Id` | `{motivo, justificativa?}` | 200 / 401 / 404 / **409 (já decidida)** / **422 (sem motivo)** |

`motivo`: `INCORRETA`, `GENERICA`, `DUPLICADA`, `FORA_DO_ESCOPO`, `OUTRO`. Aceitar uma sugestão **não altera os cenários**:
o analista faz o ajuste e pode criar/editar o cenário com `"origem": "SUGESTAO_IA"`.

## Exemplos com curl

```bash
# cenário válido
curl -X POST localhost:8080/api/historias/1/cenarios -H "Content-Type: application/json" -H "X-Usuario-Id: 1" \
  -d '{"titulo":"Login com senha correta","passos":[
        {"tipo":"DADO","texto":"um cliente cadastrado"},
        {"tipo":"QUANDO","texto":"ele informa a senha correta"},
        {"tipo":"ENTAO","texto":"o acesso é concedido"}]}'

# cenário sem Então -> 422
curl -i -X POST localhost:8080/api/historias/1/cenarios -H "Content-Type: application/json" -H "X-Usuario-Id: 1" \
  -d '{"titulo":"Sem Então","passos":[{"tipo":"DADO","texto":"a"},{"tipo":"QUANDO","texto":"b"}]}'

# exportar a história 1
curl -i localhost:8080/api/historias/1/feature
```
