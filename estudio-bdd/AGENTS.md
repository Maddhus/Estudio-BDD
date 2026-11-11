# AGENTS.md — regras para ferramentas de IA neste repositório

Projeto: **Estúdio BDD** (Programação de Sistemas II, 2026/2). API REST (Spring Boot 3.5, Java 17, Spring Data JPA,
PostgreSQL) em `src/`, SPA React (Vite) em `frontend/`. Leia `README.md` e `docs/ENDPOINTS.md` antes de propor mudanças.

## Comandos

| O quê | Comando |
|---|---|
| Subir o banco | `docker compose up -d` |
| Rodar a API | `mvn spring-boot:run` (porta 8080) |
| **Testes (regras + ArchUnit)** | `mvn test` — deve passar antes de qualquer commit |
| Rodar a SPA | `cd frontend && npm install && npm run dev` (porta 5173) |
| Roteiro de verificação | `./scripts/roteiro-verificacao.sh` (com a API no ar) |

## Arquitetura em cinco camadas (não quebrar)

SPA → controller → serviço → repositório → banco. Pacote base `br.edu.estudiobdd`:

1. **SPA** (`frontend/`): telas. **Nenhuma regra de negócio**; exibe as mensagens que a API devolve.
2. **`controller`**: recebe HTTP, valida DTOs (`@Valid`), converte DTO ↔ domínio, devolve resposta. **Nunca importa
   nada de `repository`** (há teste ArchUnit para isso). Erros padronizados em `TratadorDeErros` (ProblemDetail).
3. **`service`**: **todas as regras de negócio** e as transações (`@Transactional`). Recebe e devolve objetos de
   domínio, nunca DTOs.
4. **`repository`**: interfaces Spring Data JPA, sem regra de negócio.
5. **`model`**: entidades JPA e enums. Não depende de nenhuma outra camada.

`dto` (records com Bean Validation), `exception` (exceções de negócio) e `config` (CORS, dados fictícios) completam.

## Convenções

- Dependências **sempre por construtor** (sem `@Autowired` em campo). Sem Lombok.
- Associações **LAZY**; coleções com `cascade = ALL` + `orphanRemoval = true` apenas nas composições
  (História→Cenários/Sugestões, Cenário→Passos).
- Regras novas vão no **serviço** e ganham teste em `src/test/java/.../service` (JUnit + Mockito, sem banco).
- Códigos HTTP: 400 validação de campo, 401 sem identidade, 403 não é autor, 404 inexistente, 409 duplicidade ou
  sugestão já decidida, 422 regra de negócio.
- Mensagens ao usuário em português. Identificadores do domínio em português (como no enunciado).
- Identidade é **simulada** pelo cabeçalho `X-Usuario-Id` (não é autenticação). Não "consertar" isso com login.
- Sem credenciais no repositório. Dados de exemplo são fictícios.

## Fluxo de trabalho com IA

1. Escreva o **cenário** (Dado/Quando/Então) da regra antes de pedir código: ele é o critério para aceitar a proposta.
2. Peça mudanças pequenas; revise o código; rode `mvn test`; só então commite e registre em `docs/REGISTRO_IA.md`.
3. Ninguém commita código que não saiba explicar (finalidade, entradas, saídas, dependências).

## Erros que a IA já cometeu aqui (atualizar quando se repetir)

- _(preencher conforme acontecer; ex.: "colocou regra no controller", "injetou repositório no controller",
  "usou `@Autowired` em campo", "devolveu entidade JPA direto no JSON")_
