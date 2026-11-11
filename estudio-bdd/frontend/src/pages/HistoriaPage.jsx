import { useCallback, useEffect, useState } from "react";
import { api } from "../api.js";
import { Alerta, Selo, Vazio, useExecutar } from "../ui.jsx";
import { PRIORIDADES, ROTULO_PRIORIDADE } from "../rotulos.js";
import { useUsuario } from "../usuario.jsx";
import CenarioVisao from "../components/CenarioVisao.jsx";
import CenarioEditor from "../components/CenarioEditor.jsx";
import SugestoesPanel from "../components/SugestoesPanel.jsx";
import ExportacaoPainel from "../components/ExportacaoPainel.jsx";

export default function HistoriaPage({ id }) {
  const { usuario } = useUsuario();
  const [historia, setHistoria] = useState(null);
  const [sugestoes, setSugestoes] = useState([]);
  const [edicaoHistoria, setEdicaoHistoria] = useState(null);
  const [editorCenario, setEditorCenario] = useState(null); // null | "novo" | cenário em edição
  const [arquivo, setArquivo] = useState(null);
  const [erro, executar, limparErro] = useExecutar();

  const carregar = useCallback(
    () =>
      executar(async () => {
        const [h, s] = await Promise.all([api.historias.buscar(id), api.sugestoes.listar(id)]);
        setHistoria(h);
        setSugestoes(s);
      }),
    [executar, id]
  );

  useEffect(() => {
    carregar();
  }, [carregar]);

  if (!historia) {
    return (
      <>
        <Alerta erro={erro} />
        {!erro && <p>Carregando…</p>}
      </>
    );
  }

  const ehAutor = historia.autor.id === usuario.id;

  const salvarHistoria = (e) => {
    e.preventDefault();
    executar(async () => {
      const { titulo, papel, objetivo, beneficio, prioridade } = edicaoHistoria;
      await api.historias.atualizar(id, { titulo, papel, objetivo, beneficio, prioridade });
      setEdicaoHistoria(null);
      await carregar();
    });
  };

  const excluirHistoria = () => {
    if (!window.confirm(`Excluir a história "${historia.titulo}" com todos os cenários e sugestões?`)) return;
    executar(async () => {
      await api.historias.excluir(id);
      window.location.hash = `#/projetos/${historia.projetoId}`;
    });
  };

  const salvarCenario = (dados) =>
    executar(async () => {
      if (editorCenario === "novo") await api.cenarios.criar(id, dados);
      else await api.cenarios.atualizar(editorCenario.id, dados);
      setEditorCenario(null);
      await carregar();
    });

  const excluirCenario = (cenario) => {
    if (!window.confirm(`Excluir o cenário "${cenario.titulo}"?`)) return;
    executar(async () => {
      await api.cenarios.excluir(cenario.id);
      await carregar();
    });
  };

  const exportar = () => {
    setArquivo(null);
    executar(async () => setArquivo(await api.historias.feature(id)));
  };

  const campo = (nome) => ({
    value: edicaoHistoria[nome],
    onChange: (e) => setEdicaoHistoria({ ...edicaoHistoria, [nome]: e.target.value }),
  });

  return (
    <>
      <p className="migalha">
        <a href="#/">Projetos</a> / <a href={`#/projetos/${historia.projetoId}`}>Voltar ao projeto</a>
      </p>

      <div className="cabecalho-pagina">
        <div>
          <h1>{historia.titulo}</h1>
          <p className="meta">
            Autor: {historia.autor.nome} · Prioridade {ROTULO_PRIORIDADE[historia.prioridade].toLowerCase()}
            {historia.sugestoesPendentes > 0 && (
              <>
                {" "}
                · <Selo tipo="pendente">{historia.sugestoesPendentes} sugestão(ões) pendente(s)</Selo>
              </>
            )}
          </p>
          {!ehAutor && (
            <p className="aviso">
              Você não é o autor desta história. A API vai recusar edições e exclusões feitas por você.
            </p>
          )}
        </div>
        <div className="acoes">
          <button type="button" className="primario" onClick={exportar}>
            Exportar .feature
          </button>
        </div>
      </div>

      <Alerta erro={erro} aoFechar={limparErro} />
      {arquivo && <ExportacaoPainel arquivo={arquivo} aoFechar={() => setArquivo(null)} />}

      <div className="duas-colunas">
        <div className="coluna-principal">
          {edicaoHistoria ? (
            <form className="formulario" onSubmit={salvarHistoria}>
              <h2>Editar história</h2>
              <label>
                Título
                <input {...campo("titulo")} required maxLength={200} />
              </label>
              <div className="narrativa-campos">
                <label>
                  Como…
                  <input {...campo("papel")} required />
                </label>
                <label>
                  Quero…
                  <input {...campo("objetivo")} required />
                </label>
                <label>
                  Para…
                  <input {...campo("beneficio")} required />
                </label>
              </div>
              <label className="curto">
                Prioridade
                <select {...campo("prioridade")}>
                  {PRIORIDADES.map((p) => (
                    <option key={p} value={p}>
                      {ROTULO_PRIORIDADE[p]}
                    </option>
                  ))}
                </select>
              </label>
              <div className="acoes">
                <button type="submit" className="primario">
                  Salvar história
                </button>
                <button type="button" onClick={() => setEdicaoHistoria(null)}>
                  Cancelar
                </button>
              </div>
            </form>
          ) : (
            <section className="narrativa" aria-label="Narrativa da história">
              <p>
                <span className="palavra">Como</span> {historia.papel}
              </p>
              <p>
                <span className="palavra">Quero</span> {historia.objetivo}
              </p>
              <p>
                <span className="palavra">Para</span> {historia.beneficio}
              </p>
              <div className="acoes">
                <button type="button" onClick={() => setEdicaoHistoria({ ...historia })}>
                  Editar história
                </button>
                <button type="button" className="perigo" onClick={excluirHistoria}>
                  Excluir história
                </button>
              </div>
            </section>
          )}

          <div className="cabecalho-secao">
            <h2>Cenários</h2>
            {!editorCenario && (
              <button type="button" onClick={() => setEditorCenario("novo")}>
                Novo cenário
              </button>
            )}
          </div>

          {editorCenario && (
            <CenarioEditor
              key={editorCenario === "novo" ? "novo" : editorCenario.id}
              cenario={editorCenario === "novo" ? null : editorCenario}
              aoSalvar={salvarCenario}
              aoCancelar={() => setEditorCenario(null)}
            />
          )}

          {historia.cenarios.length === 0 && !editorCenario && (
            <Vazio>Nenhum cenário ainda. Escreva o primeiro: um exemplo concreto de quando esta história está pronta.</Vazio>
          )}

          {historia.cenarios.map((c) => (
            <CenarioVisao
              key={c.id}
              cenario={c}
              aoEditar={() => setEditorCenario(c)}
              aoExcluir={() => excluirCenario(c)}
            />
          ))}
        </div>

        <aside className="coluna-lateral">
          <SugestoesPanel historiaId={id} sugestoes={sugestoes} aoMudar={carregar} />
        </aside>
      </div>
    </>
  );
}
