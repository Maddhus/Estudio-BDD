# Roteiro de verificação (seção 13 do enunciado)

Como usar: com banco e API no ar, rode `./scripts/roteiro-verificacao.sh | tee docs/evidencias/execucao-api.txt` e
complete a coluna **Resultado observado** com o que realmente aconteceu (o script imprime `ok`/`FALHA` por verificação
e a mensagem devolvida pela API). Os itens de SPA, de reinício e de `mvn test` são manuais: anexem capturas de tela em
`docs/evidencias/`.

| # | Verificação | Entrada | Resultado esperado | Resultado observado |
|---|---|---|---|---|
| 1a | História válida | `POST /projetos/{id}/historias` (Ana) com título, papel, objetivo, benefício e prioridade | 201; autor = Ana | _(preencher)_ |
| 1b | Título repetido no projeto | Mesmo título, mesmo projeto | 409 com mensagem de título repetido | |
| 1c | Narrativa incompleta | História sem `beneficio` | 400, erro no campo `beneficio` | |
| 2a | Cenário válido | Dado + Quando + Então (+ E) | 201; passos numerados 1..n | |
| 2b | Sem Quando | Dado + Então | 422 "precisa ter ao menos um passo Quando" | |
| 2c | Sem Então | Dado + Quando | 422 "precisa ter ao menos um passo Então" | |
| 2d | Fora de ordem | Quando, Dado, Então | 422 "fora de ordem" | |
| 2e | Iniciado por E (e por Mas) | E, Quando, Então | 422 "não pode iniciar o cenário" | |
| 3 | Passos na ordem após edição | `POST /cenarios/{id}/passos` com `posicao`; `PUT /cenarios/{id}` com nova lista | Ordem e posições conforme enviado; inserção inválida rejeitada sem alterar o cenário | |
| 4 | Bloqueio de não-autor | PUT/DELETE de história, cenário e passo com `X-Usuario-Id` de Bruno | 403 em todos | |
| 5a | Registro de sugestão | `POST /historias/{id}/sugestoes` | 201; `decisao = PENDENTE` | |
| 5b | Aceitação | `POST /sugestoes/{id}/aceitar` | 200; `ACEITA`; **cenários inalterados** | |
| 5c | Rejeição com motivo | `POST /sugestoes/{id}/rejeitar` com `motivo` | 200; `REJEITADA` com o motivo | |
| 5d | Rejeição sem motivo | Corpo sem `motivo` | 422; sugestão continua `PENDENTE` | |
| 6 | Sugestão já decidida | Aceitar/rejeitar de novo | 409 | |
| 7a | Exportação bloqueada sem cenários | `GET /historias/{id}/feature` | 422 | |
| 7b | Exportação bloqueada com pendente | idem, com sugestão `PENDENTE` | 422 | |
| 7c | `.feature` correto | idem, com cenário e sem pendências | 200; começa com `# language: pt`; confere com `docs/exemplo-formato.feature` | |
| 8 | Resumo coerente | `GET /projetos/{id}/resumo` e `.../historias` | Totais iguais aos dados cadastrados (conferir um a um) | |
| 9 | Persistência | Reiniciar a API (sem `docker compose down -v`) e listar `GET /projetos` | Dados continuam lá | |
| 10 | CORS | Requisição com `Origin: http://localhost:5173` e com outra origem | Só a primeira recebe `Access-Control-Allow-Origin` | |
| 11 | `mvn test` | `mvn test` | Todos os testes passam, incluindo ArchUnit | |
| 12 | Fluxo completo pela SPA | Criar história → cenários → registrar sugestão → aceitar/rejeitar → exportar `.feature` | Funciona; mensagens de erro da API aparecem na tela | |

Atividade bônus: não implementada.
