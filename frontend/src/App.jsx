import { useCallback, useEffect, useMemo, useState } from "react";
import { api, definirUsuarioAtual } from "./api.js";
import { UsuarioContext } from "./usuario.jsx";
import { Alerta } from "./ui.jsx";
import SelecaoUsuario from "./pages/SelecaoUsuario.jsx";
import ProjetosPage from "./pages/ProjetosPage.jsx";
import ProjetoPage from "./pages/ProjetoPage.jsx";
import HistoriaPage from "./pages/HistoriaPage.jsx";

const CHAVE_USUARIO = "estudiobdd.usuarioId";

/** Navegação por hash (#/projetos/1): simples e sem dependências. */
function useRota() {
  const ler = () => window.location.hash.replace(/^#/, "") || "/";
  const [caminho, setCaminho] = useState(ler);
  useEffect(() => {
    const aoMudar = () => setCaminho(ler());
    window.addEventListener("hashchange", aoMudar);
    return () => window.removeEventListener("hashchange", aoMudar);
  }, []);

  let m;
  if ((m = caminho.match(/^\/projetos\/(\d+)$/))) return { nome: "projeto", id: Number(m[1]) };
  if ((m = caminho.match(/^\/historias\/(\d+)$/))) return { nome: "historia", id: Number(m[1]) };
  return { nome: "projetos" };
}

export default function App() {
  const [usuarios, setUsuarios] = useState(null);
  const [erro, setErro] = useState(null);
  const [usuarioId, setUsuarioId] = useState(() => Number(localStorage.getItem(CHAVE_USUARIO)) || null);
  const rota = useRota();

  useEffect(() => {
    api.usuarios
      .listar()
      .then(setUsuarios)
      .catch(setErro);
  }, []);

  const usuario = useMemo(
    () => usuarios?.find((u) => u.id === usuarioId) || null,
    [usuarios, usuarioId]
  );

  // Mantém o cabeçalho X-Usuario-Id sincronizado com o usuário escolhido.
  useEffect(() => {
    definirUsuarioAtual(usuario ? usuario.id : null);
  }, [usuario]);

  const selecionar = useCallback((id) => {
    if (id) localStorage.setItem(CHAVE_USUARIO, String(id));
    else localStorage.removeItem(CHAVE_USUARIO);
    setUsuarioId(id);
  }, []);

  if (erro) {
    return (
      <main className="centro">
        <Alerta erro={erro} />
        <p className="dica">
          Suba o PostgreSQL e a API (<code>mvn spring-boot:run</code>) e recarregue esta página.
        </p>
      </main>
    );
  }
  if (!usuarios) return <main className="centro">Carregando…</main>;
  if (!usuario) return <SelecaoUsuario usuarios={usuarios} aoEscolher={selecionar} />;

  return (
    <UsuarioContext.Provider value={{ usuario, usuarios, selecionar }}>
      <header className="topo">
        <a className="marca" href="#/">
          Estúdio BDD
        </a>
        <label className="usuario-atual">
          Usuário
          <select value={usuario.id} onChange={(e) => selecionar(Number(e.target.value))}>
            {usuarios.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nome}
              </option>
            ))}
          </select>
        </label>
      </header>
      <main className="pagina">
        {/* key força o recarregamento ao trocar de usuário ou de página */}
        {rota.nome === "projetos" && <ProjetosPage key={`p-${usuario.id}`} />}
        {rota.nome === "projeto" && <ProjetoPage key={`pr-${rota.id}-${usuario.id}`} id={rota.id} />}
        {rota.nome === "historia" && <HistoriaPage key={`h-${rota.id}-${usuario.id}`} id={rota.id} />}
      </main>
    </UsuarioContext.Provider>
  );
}
