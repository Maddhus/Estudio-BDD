export default function SelecaoUsuario({ usuarios, aoEscolher }) {
  return (
    <main className="centro selecao">
      <h1>Estúdio BDD</h1>
      <p className="lead">
        Escreva histórias de usuário e seus cenários, registre o que a IA sugeriu e decida o que entra na
        documentação.
      </p>
      <h2>Quem está usando?</h2>
      <p className="dica">
        Usuários de demonstração. Isto simula a identidade (a API recebe o id no cabeçalho
        <code> X-Usuario-Id</code>) e não é um login de verdade.
      </p>
      <ul className="lista-usuarios">
        {usuarios.map((u) => (
          <li key={u.id}>
            <button type="button" onClick={() => aoEscolher(u.id)}>
              <strong>{u.nome}</strong>
              <span>{u.email}</span>
            </button>
          </li>
        ))}
      </ul>
    </main>
  );
}
