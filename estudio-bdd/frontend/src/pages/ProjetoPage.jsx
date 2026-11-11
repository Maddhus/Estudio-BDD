import { useCallback, useEffect, useState } from "react";
import { api } from "../api.js";
import { Alerta, Selo, Vazio, useExecutar } from "../ui.jsx";
import {
  PRIORIDADES,
  ROTULO_DECISAO,
  ROTULO_MOTIVO,
  ROTULO_ORIGEM,
  ROTULO_PRIORIDADE,
} from "../rotulos.js";

const HISTORIA_VAZIA = { titulo: "", papel: "", objetivo: "", beneficio: "", prioridade: "MEDIA" };

export default function ProjetoPage({ id }) {
  const [projeto, setProjeto] = useState(null);
  const [historias, setHistorias] = useState(null);
  const [resumo, setResumo] = useState(null);
  const [prioridade, setPrioridade] = useState("");
  const [aba, setAba] = useState("historias");
  const [nova, setNova] = useState(null);
  const [erro, executar, limparErro] = useExecutar();

  const carregarHistorias = useCallback(
    () => executar(async () => setHistorias(await api.projetos.historias(id, prioridade))),
    [executar, id, prioridade]
  );

  useEffect(() => {
    executar(async () => setProjeto(await api.projetos.buscar(id)));
  }, [executar, id]);

  useEffect(() => {
    carregarHistorias();
  }, [carregarHistorias]);

  useEffect(() => {
    if (aba === "resumo") executar(async () => setResumo(await api.projetos.resumo(id)));
  }, [aba, executar, id]);

  const criar = (e) => {
    e.preventDefault();
    executar(async () => {
      const criada = await api.projetos.criarHistoria(id, nova);
      window.location.hash = `#/historias/${criada.id}`;
    });
  };

  const campo = (nome) => ({
    value: nova[nome],
    onChange: (e) => setNova({ ...nova, [nome]: e.target.value }),
  });

  return (
    <>
      <p className="migalha">
        <a href="#/">Projetos</a>
      </p>
      <div className="cabecalho-pagina">
        <div>
          <h1>{projeto ? projeto.nome : "Projeto"}</h1>
          {projeto?.descricao && <p className="lead">{projeto.descricao}</p>}
        </div>
        {aba === "historias" && !nova && (
          <button type="button" className="primario" onClick={() => setNova({ ...HISTORIA_VAZIA })}>
            Nova história
          </button>
        )}
      </div>

      {projeto?.contextoDominio && (
        <details className="contexto-dominio">
          <summary>Contexto do domínio</summary>
          <p>{projeto.contextoDominio}</p>
        </details>
      )}

      <Alerta erro={erro} aoFechar={limparErro} />

      <div className="abas" role="tablist">
        <button role="tab" aria-selected={aba === "historias"} onClick={() => setAba("historias")}>
          Histórias
        </button>
        <button role="tab" aria-selected={aba === "resumo"} onClick={() => setAba("resumo")}>
          Resumo
        </button>
      </div>

      {aba === "historias" && (
        <>
          {nova && (
            <form className="formulario" onSubmit={criar}>
              <h2>Nova história</h2>
              <label>
                Título
                <input {...campo("titulo")} required maxLength={200} />
              </label>
              <div className="narrativa-campos">
                <label>
                  Como…
                  <input {...campo("papel")} placeholder="analista de requisitos" required />
                </label>
                <label>
                  Quero…
                  <input {...campo("objetivo")} placeholder="registrar os cenários de uma história" required />
                </label>
                <label>
                  Para…
                  <input {...campo("beneficio")} placeholder="documentar os critérios de aceite" required />
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
                <button type="button" onClick={() => setNova(null)}>
                  Cancelar
                </button>
              </div>
            </form>
          )}

          <div className="filtro">
            <label>
              Prioridade
              <select value={prioridade} onChange={(e) => setPrioridade(e.target.value)}>
                <option value="">Todas</option>
                {PRIORIDADES.map((p) => (
                  <option key={p} value={p}>
                    {ROTULO_PRIORIDADE[p]}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {historias && historias.length === 0 && (
            <Vazio>
              {prioridade
                ? "Nenhuma história com essa prioridade."
                : "Este projeto ainda não tem histórias. Cadastre a primeira."}
            </Vazio>
          )}

          {historias && historias.length > 0 && (
            <div className="tabela-rolavel">
              <table>
                <thead>
                  <tr>
                    <th>História</th>
                    <th>Prioridade</th>
                    <th>Autor</th>
                    <th className="num">Cenários</th>
                    <th className="num">Sugestões pendentes</th>
                  </tr>
                </thead>
                <tbody>
                  {historias.map((h) => (
                    <tr key={h.id}>
                      <td>
                        <a className="titulo-link" href={`#/historias/${h.id}`}>
                          {h.titulo}
                        </a>
                      </td>
                      <td>
                        <Selo tipo={`prio-${h.prioridade}`}>{ROTULO_PRIORIDADE[h.prioridade]}</Selo>
                      </td>
                      <td>{h.autorNome}</td>
                      <td className="num">{h.totalCenarios}</td>
                      <td className="num">
                        {h.sugestoesPendentes > 0 ? (
                          <Selo tipo="pendente">{h.sugestoesPendentes}</Selo>
                        ) : (
                          0
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}

      {aba === "resumo" && resumo && (
        <section className="resumo">
          <dl className="indicadores">
            <div>
              <dt>Histórias</dt>
              <dd>{resumo.totalHistorias}</dd>
            </div>
            {Object.entries(resumo.cenariosPorOrigem).map(([origem, qtd]) => (
              <div key={origem}>
                <dt>Cenários: {ROTULO_ORIGEM[origem].toLowerCase()}</dt>
                <dd>{qtd}</dd>
              </div>
            ))}
            {Object.entries(resumo.sugestoesPorDecisao).map(([decisao, qtd]) => (
              <div key={decisao}>
                <dt>Sugestões {ROTULO_DECISAO[decisao].toLowerCase()}s</dt>
                <dd>{qtd}</dd>
              </div>
            ))}
          </dl>

          <h2>Motivos de rejeição mais frequentes</h2>
          {resumo.motivosRejeicaoMaisFrequentes.length === 0 ? (
            <Vazio>Nenhuma sugestão foi rejeitada neste projeto.</Vazio>
          ) : (
            <ol className="motivos">
              {resumo.motivosRejeicaoMaisFrequentes.map((m) => (
                <li key={m.motivo}>
                  <span>{ROTULO_MOTIVO[m.motivo] || m.motivo}</span>
                  <strong>{m.quantidade}</strong>
                </li>
              ))}
            </ol>
          )}
        </section>
      )}
    </>
  );
}
