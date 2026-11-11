import { useCallback, useEffect, useState } from "react";
import { api } from "../api.js";
import { Alerta, Vazio, useExecutar } from "../ui.jsx";

const FORM_VAZIO = { nome: "", descricao: "", contextoDominio: "" };

export default function ProjetosPage() {
  const [projetos, setProjetos] = useState(null);
  const [formulario, setFormulario] = useState(null); // null = fechado; { id?, ...campos }
  const [erro, executar, limparErro] = useExecutar();

  const carregar = useCallback(
    () => executar(async () => setProjetos(await api.projetos.listar())),
    [executar]
  );
  useEffect(() => {
    carregar();
  }, [carregar]);

  const salvar = (e) => {
    e.preventDefault();
    executar(async () => {
      const { id, ...dados } = formulario;
      if (id) await api.projetos.atualizar(id, dados);
      else await api.projetos.criar(dados);
      setFormulario(null);
      await carregar();
    });
  };

  const excluir = (projeto) => {
    if (!window.confirm(`Excluir o projeto "${projeto.nome}"?`)) return;
    executar(async () => {
      await api.projetos.excluir(projeto.id);
      await carregar();
    });
  };

  const campo = (nome) => ({
    value: formulario[nome] ?? "",
    onChange: (e) => setFormulario({ ...formulario, [nome]: e.target.value }),
  });

  return (
    <>
      <div className="cabecalho-pagina">
        <h1>Projetos</h1>
        {!formulario && (
          <button type="button" className="primario" onClick={() => setFormulario({ ...FORM_VAZIO })}>
            Novo projeto
          </button>
        )}
      </div>

      <Alerta erro={erro} aoFechar={limparErro} />

      {formulario && (
        <form className="formulario" onSubmit={salvar}>
          <h2>{formulario.id ? "Editar projeto" : "Novo projeto"}</h2>
          <label>
            Nome
            <input {...campo("nome")} required maxLength={120} />
          </label>
          <label>
            Descrição
            <input {...campo("descricao")} maxLength={500} />
          </label>
          <label>
            Contexto do domínio
            <textarea
              {...campo("contextoDominio")}
              rows={4}
              placeholder="Glossário e informações do negócio que ajudam a escrever os cenários."
            />
          </label>
          <div className="acoes">
            <button type="submit" className="primario">
              Salvar projeto
            </button>
            <button type="button" onClick={() => setFormulario(null)}>
              Cancelar
            </button>
          </div>
        </form>
      )}

      {projetos && projetos.length === 0 && <Vazio>Nenhum projeto ainda. Crie o primeiro para começar.</Vazio>}

      <ul className="lista-projetos">
        {projetos?.map((p) => (
          <li key={p.id}>
            <div>
              <a className="titulo-link" href={`#/projetos/${p.id}`}>
                {p.nome}
              </a>
              {p.descricao && <p>{p.descricao}</p>}
              {p.contextoDominio && <p className="contexto">{p.contextoDominio}</p>}
            </div>
            <div className="acoes">
              <button type="button" onClick={() => setFormulario({ ...p })}>
                Editar
              </button>
              <button type="button" className="perigo" onClick={() => excluir(p)}>
                Excluir
              </button>
            </div>
          </li>
        ))}
      </ul>
    </>
  );
}
