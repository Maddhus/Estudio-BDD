# language: pt
Funcionalidade: Login de cliente
  Como cliente cadastrado
  Quero acessar minha conta com e-mail e senha
  Para consultar meus pedidos com segurança

  Cenário: Login com credenciais válidas
    Dado o cliente "maria@exemplo.com" está cadastrado
    Quando ela faz login com a senha correta
    Então o acesso é concedido
    E a sessão do cliente é iniciada

  Cenário: Login com senha incorreta é recusado
    Dado o cliente "maria@exemplo.com" está cadastrado
    Quando ela faz login com a senha "errada123"
    Então o acesso é recusado
    E a mensagem informa credenciais inválidas
