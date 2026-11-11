#!/usr/bin/env bash
# Roteiro de verificação da API (seção 13 do enunciado), executado com curl.
#
# Uso:   ./scripts/roteiro-verificacao.sh [URL_BASE]        (padrão: http://localhost:8080/api)
# Evidência:  ./scripts/roteiro-verificacao.sh | tee docs/evidencias/execucao-api.txt
#
# Pré-requisitos: API no ar com os dados fictícios carregados (usuários ana@ e bruno@exemplo.com).
# O script cria um projeto novo a cada execução (nome com timestamp), então pode ser repetido.

BASE="${1:-http://localhost:8080/api}"
SUF="$(date +%s)"
PASS=0
FAIL=0
STATUS=""
CORPO=""

chamar() {   # metodo caminho [usuario] [corpo-json]  ->  preenche STATUS e CORPO
  local metodo="$1" caminho="$2" usuario="${3:-}" corpo="${4:-}"
  local args=(-s -w $'\n%{http_code}' -X "$metodo" "$BASE$caminho")
  if [ -n "$usuario" ]; then args+=(-H "X-Usuario-Id: $usuario"); fi
  if [ -n "$corpo" ]; then args+=(-H "Content-Type: application/json" -d "$corpo"); fi
  local saida
  saida="$(curl "${args[@]}")"
  STATUS="${saida##*$'\n'}"
  CORPO="${saida%$'\n'*}"
}

ok()    { PASS=$((PASS + 1)); printf '  ok     %s\n' "$1"; }
falha() { FAIL=$((FAIL + 1)); printf '  FALHA  %s\n         resposta: HTTP %s %s\n' "$1" "$STATUS" "$CORPO"; }

esperar() {  # descricao status-esperado
  if [ "$STATUS" = "$2" ]; then ok "$1 -> HTTP $STATUS"; else falha "$1 (esperado HTTP $2)"; fi
}
contem() {   # descricao texto
  if printf '%s' "$CORPO" | grep -q -- "$2"; then ok "$1"; else falha "$1 (esperava encontrar: $2)"; fi
}
nao_contem() {  # descricao texto
  if printf '%s' "$CORPO" | grep -q -- "$2"; then falha "$1 (não deveria conter: $2)"; else ok "$1"; fi
}
igual() {    # descricao obtido esperado
  if [ "$2" = "$3" ]; then ok "$1 = $3"; else FAIL=$((FAIL + 1)); printf '  FALHA  %s\n         esperado: %s\n         obtido:   %s\n' "$1" "$3" "$2"; fi
}
mensagem() { printf '         mensagem da API: %s\n' "$(printf '%s' "$CORPO" | grep -o '"detail":"[^"]*' | sed 's/"detail":"//')"; }

id_de() { printf '%s' "$CORPO" | sed -E 's/^\{"id":([0-9]+).*/\1/'; }
tipos_dos_passos() { printf '%s' "$CORPO" | grep -o '"tipo":"[A-Z]*"' | sed -E 's/.*:"([A-Z]*)"/\1/' | paste -sd, -; }
textos_dos_passos() { printf '%s' "$CORPO" | grep -o '"texto":"[^"]*"' | sed -E 's/"texto":"([^"]*)"/\1/' | paste -sd, -; }
ids_dos_passos() { printf '%s' "$CORPO" | grep -o '"passos":\[.*' | grep -o '"id":[0-9]*' | sed 's/"id"://'; }

passo()    { printf '{"tipo":"%s","texto":"%s"}' "$1" "$2"; }
cenario()  { printf '{"titulo":"%s","passos":[%s]}' "$1" "$2"; }

# ------------------------------------------------------------------ preparação
if ! curl -s -o /dev/null "$BASE/usuarios"; then
  echo "Não consegui falar com a API em $BASE. Ela está no ar?"
  exit 2
fi
USUARIOS="$(curl -s "$BASE/usuarios")"
id_usuario() { printf '%s' "$USUARIOS" | tr '{' '\n' | grep "$1" | sed -E 's/.*"id":([0-9]+).*/\1/' | head -1; }
ANA="$(id_usuario ana@exemplo.com)"
BRUNO="$(id_usuario bruno@exemplo.com)"
if [ -z "$ANA" ] || [ -z "$BRUNO" ]; then
  echo "Usuários ana@exemplo.com e bruno@exemplo.com não encontrados (dados fictícios carregados?)."
  exit 2
fi
echo "API: $BASE | Ana = usuário $ANA | Bruno = usuário $BRUNO | execução $SUF"

# ------------------------------------------------------------------ 1. projeto e história
echo
echo "== 1. Projeto e história =="
chamar POST /projetos "$ANA" "{\"nome\":\"Roteiro $SUF\",\"descricao\":\"Projeto criado pelo roteiro\",\"contextoDominio\":\"Dados fictícios\"}"
esperar "projeto válido é criado" 201
PROJETO="$(id_de)"
chamar POST /projetos "$ANA" "{\"nome\":\"Roteiro $SUF\"}"
esperar "nome de projeto repetido é rejeitado" 409; mensagem

HIST_OK='{"titulo":"Login de cliente","papel":"cliente cadastrado","objetivo":"acessar minha conta","beneficio":"consultar meus pedidos","prioridade":"ALTA"}'
chamar POST "/projetos/$PROJETO/historias" "$ANA" "$HIST_OK"
esperar "história válida é criada" 201
HISTORIA="$(id_de)"
contem "o autor da história é Ana" '"autor":{"id":'"$ANA"','
chamar POST "/projetos/$PROJETO/historias" "$ANA" "$HIST_OK"
esperar "título repetido no mesmo projeto é rejeitado" 409; mensagem
chamar POST "/projetos/$PROJETO/historias" "$ANA" '{"titulo":"Narrativa incompleta","papel":"cliente","objetivo":"entrar","prioridade":"ALTA"}'
esperar "narrativa incompleta (sem benefício) é rejeitada" 400
contem "a resposta indica o campo beneficio" '"campo":"beneficio"'
chamar POST "/projetos/$PROJETO/historias" "" "$HIST_OK"
esperar "sem cabeçalho X-Usuario-Id a criação é recusada" 401
chamar POST "/projetos/$PROJETO/historias" "99999" "$HIST_OK"
esperar "usuário inexistente no cabeçalho é recusado" 401

# ------------------------------------------------------------------ 2. cenários
echo
echo "== 2. Cenários (regra 2) =="
VALIDO="$(passo DADO 'um cliente cadastrado'),$(passo QUANDO 'ele informa a senha correta'),$(passo ENTAO 'o acesso é concedido')"
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Login com senha correta' "$VALIDO")"
esperar "cenário válido é criado" 201
CENARIO="$(id_de)"
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Sem Então' "$(passo DADO 'a'),$(passo QUANDO 'b')")"
esperar "cenário sem passo Então é rejeitado" 422; mensagem
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Sem Quando' "$(passo DADO 'a'),$(passo ENTAO 'b')")"
esperar "cenário sem passo Quando é rejeitado" 422; mensagem
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Fora de ordem' "$(passo QUANDO 'a'),$(passo DADO 'b'),$(passo ENTAO 'c')")"
esperar "cenário fora de ordem é rejeitado" 422; mensagem
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Inicia com E' "$(passo E 'a'),$(passo QUANDO 'b'),$(passo ENTAO 'c')")"
esperar "cenário iniciado por E é rejeitado" 422; mensagem
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Inicia com Mas' "$(passo MAS 'a'),$(passo QUANDO 'b'),$(passo ENTAO 'c')")"
esperar "cenário iniciado por Mas é rejeitado" 422
chamar POST "/historias/$HISTORIA/cenarios" "$ANA" "$(cenario 'Login com senha correta' "$VALIDO")"
esperar "título de cenário repetido na história é rejeitado" 409; mensagem

# ------------------------------------------------------------------ 3. ordem dos passos
echo
echo "== 3. Passos mantidos na ordem correta após edição =="
chamar POST "/cenarios/$CENARIO/passos" "$ANA" '{"tipo":"E","texto":"o idioma é português","posicao":2}'
esperar "passo E inserido na posição 2" 201
chamar GET "/cenarios/$CENARIO"
igual "ordem dos tipos após a inserção" "$(tipos_dos_passos)" "DADO,E,QUANDO,ENTAO"
chamar POST "/cenarios/$CENARIO/passos" "$ANA" '{"tipo":"DADO","texto":"tarde demais","posicao":4}'
esperar "passo Dado inserido depois do Quando é rejeitado" 422; mensagem
chamar GET "/cenarios/$CENARIO"
igual "ordem preservada após a tentativa rejeitada" "$(tipos_dos_passos)" "DADO,E,QUANDO,ENTAO"
chamar PUT "/cenarios/$CENARIO" "$ANA" "$(cenario 'Login com senha correta' "$(passo DADO 'primeiro'),$(passo DADO 'segundo'),$(passo QUANDO 'terceiro'),$(passo ENTAO 'quarto')")"
esperar "edição do cenário com novos passos" 200
chamar GET "/cenarios/$CENARIO"
igual "tipos na ordem enviada" "$(tipos_dos_passos)" "DADO,DADO,QUANDO,ENTAO"
igual "textos na ordem enviada" "$(textos_dos_passos)" "primeiro,segundo,terceiro,quarto"
PRIMEIRO_PASSO="$(ids_dos_passos | head -1)"
ULTIMO_PASSO="$(ids_dos_passos | tail -1)"
chamar DELETE "/passos/$ULTIMO_PASSO" "$ANA"
esperar "excluir o único passo Então é rejeitado" 422; mensagem
chamar DELETE "/passos/$PRIMEIRO_PASSO" "$ANA"
esperar "excluir um passo Dado é permitido (cenário continua válido)" 204
chamar GET "/cenarios/$CENARIO"
igual "tipos após a exclusão" "$(tipos_dos_passos)" "DADO,QUANDO,ENTAO"
PRIMEIRO_PASSO="$(ids_dos_passos | head -1)"

# ------------------------------------------------------------------ 4. autoria
echo
echo "== 4. Autoria (regra 3): Bruno não é o autor =="
chamar PUT "/historias/$HISTORIA" "$BRUNO" "$HIST_OK"
esperar "edição da história por não autor é bloqueada" 403; mensagem
chamar DELETE "/historias/$HISTORIA" "$BRUNO"
esperar "exclusão da história por não autor é bloqueada" 403
chamar POST "/historias/$HISTORIA/cenarios" "$BRUNO" "$(cenario 'Do Bruno' "$VALIDO")"
esperar "criação de cenário por não autor é bloqueada" 403
chamar PUT "/cenarios/$CENARIO" "$BRUNO" "$(cenario 'Alterado' "$VALIDO")"
esperar "edição de cenário por não autor é bloqueada" 403
chamar DELETE "/cenarios/$CENARIO" "$BRUNO"
esperar "exclusão de cenário por não autor é bloqueada" 403
chamar DELETE "/passos/$PRIMEIRO_PASSO" "$BRUNO"
esperar "exclusão de passo por não autor é bloqueada" 403
chamar DELETE "/cenarios/$CENARIO" ""
esperar "exclusão sem identificação é recusada" 401

# ------------------------------------------------------------------ 5. sugestões e exportação
echo
echo "== 5. Sugestões (regra 4) e exportação (regra 5) =="
chamar POST "/historias/$HISTORIA/sugestoes" "$ANA" '{"ferramenta":"Claude","prompt":"Amplie os cenários de login.","conteudo":"Cenário: bloquear a conta após 5 tentativas erradas."}'
esperar "sugestão é registrada" 201
SUG1="$(id_de)"
contem "a sugestão nasce PENDENTE" '"decisao":"PENDENTE"'
chamar GET "/historias/$HISTORIA/feature"
esperar "exportação bloqueada com sugestão pendente" 422; mensagem
chamar GET "/historias/$HISTORIA/sugestoes?decisao=PENDENTE"
esperar "listar sugestões pendentes" 200
contem "a lista contém a sugestão pendente" '"decisao":"PENDENTE"'
chamar POST "/sugestoes/$SUG1/rejeitar" "$ANA" '{"justificativa":"sem informar motivo"}'
esperar "rejeição sem motivo é bloqueada" 422; mensagem
chamar POST "/sugestoes/$SUG1/rejeitar" "$ANA" '{"motivo":"FORA_DO_ESCOPO","justificativa":"Bloqueio de conta não faz parte desta história."}'
esperar "rejeição com motivo é aceita" 200
contem "a sugestão ficou REJEITADA com o motivo" '"motivoRejeicao":"FORA_DO_ESCOPO"'
chamar POST "/sugestoes/$SUG1/aceitar" "$ANA" '{"justificativa":"mudei de ideia"}'
esperar "aceitar sugestão já decidida é bloqueado" 409; mensagem
chamar POST "/sugestoes/$SUG1/rejeitar" "$ANA" '{"motivo":"OUTRO"}'
esperar "rejeitar sugestão já decidida é bloqueado" 409
chamar POST "/historias/$HISTORIA/sugestoes" "$ANA" '{"ferramenta":"ChatGPT","prompt":"Quais casos de borda faltam?","conteudo":"Cenário: senha em branco é recusada."}'
SUG2="$(id_de)"
chamar POST "/sugestoes/$SUG2/aceitar" "$ANA" '{"justificativa":"Caso de borda que faltava."}'
esperar "aceitação de sugestão pendente" 200
contem "a sugestão ficou ACEITA" '"decisao":"ACEITA"'
chamar GET "/historias/$HISTORIA/cenarios"
contem "aceitar NÃO alterou os cenários (continua 1 cenário)" '"titulo":"Login com senha correta"'
chamar GET "/historias/$HISTORIA/sugestoes?decisao=PENDENTE"
igual "não restam sugestões pendentes" "$CORPO" "[]"

mkdir -p docs/evidencias 2>/dev/null
chamar GET "/historias/$HISTORIA/feature"
esperar "exportação liberada (cenário e nenhuma pendência)" 200
contem "o arquivo começa com # language: pt" '# language: pt'
contem "o arquivo traz a palavra-chave Funcionalidade" 'Funcionalidade: Login de cliente'
curl -s -o docs/evidencias/exportado-pelo-sistema.feature "$BASE/historias/$HISTORIA/feature" \
  && echo "         (arquivo salvo em docs/evidencias/exportado-pelo-sistema.feature)"

chamar POST "/projetos/$PROJETO/historias" "$ANA" '{"titulo":"Busca sem cenários","papel":"cliente","objetivo":"buscar produtos","beneficio":"achar o que quero","prioridade":"BAIXA"}'
esperar "segunda história criada (sem cenários)" 201
HISTORIA2="$(id_de)"
chamar GET "/historias/$HISTORIA2/feature"
esperar "exportação bloqueada sem cenários" 422; mensagem

# ------------------------------------------------------------------ 6. consulta e resumo
echo
echo "== 6. Consulta e resumo do projeto (regras 6 e 7) =="
chamar GET "/projetos/$PROJETO/historias"
esperar "consulta das histórias do projeto" 200
contem "história 1 tem 1 cenário" '"totalCenarios":1'
contem "nenhuma sugestão pendente na história 1" '"sugestoesPendentes":0'
chamar GET "/projetos/$PROJETO/historias?prioridade=ALTA"
contem "filtro ALTA traz o Login de cliente" 'Login de cliente'
nao_contem "filtro ALTA não traz a Busca sem cenários" 'Busca sem cenários'
chamar GET "/projetos/$PROJETO/resumo"
esperar "resumo do projeto" 200
contem "total de histórias = 2" '"totalHistorias":2'
contem "cenários por origem (HUMANO = 1)" '"HUMANO":1'
contem "cenários por origem (SUGESTAO_IA = 0)" '"SUGESTAO_IA":0'
contem "sugestões aceitas = 1" '"ACEITA":1'
contem "sugestões rejeitadas = 1" '"REJEITADA":1'
contem "sugestões pendentes = 0" '"PENDENTE":0'
contem "motivo de rejeição mais frequente" '"motivo":"FORA_DO_ESCOPO","quantidade":1'

# ------------------------------------------------------------------ 7. CORS
echo
echo "== 7. CORS: só a origem da SPA é liberada =="
PERMITIDA="$(curl -s -D - -o /dev/null -H 'Origin: http://localhost:5173' "$BASE/projetos" | tr -d '\r' | grep -i '^access-control-allow-origin' | head -1)"
OUTRA="$(curl -s -D - -o /dev/null -H 'Origin: http://site-qualquer.exemplo' "$BASE/projetos" | tr -d '\r' | grep -i '^access-control-allow-origin' | head -1)"
if [ -n "$PERMITIDA" ]; then ok "origem http://localhost:5173 recebe Access-Control-Allow-Origin"; else FAIL=$((FAIL + 1)); echo "  FALHA  origem da SPA não foi liberada"; fi
if [ -z "$OUTRA" ]; then ok "outra origem NÃO recebe Access-Control-Allow-Origin"; else FAIL=$((FAIL + 1)); echo "  FALHA  outra origem foi liberada: $OUTRA"; fi

# ------------------------------------------------------------------ resultado
echo
echo "Resultado: $PASS verificações ok, $FAIL falhas."
echo "Pendente (manual): reiniciar a API e conferir que o projeto \"Roteiro $SUF\" continua listado em GET $BASE/projetos."
[ "$FAIL" -eq 0 ]
