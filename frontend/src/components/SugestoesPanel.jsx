import { useState } from "react";
import { api } from "../api.js";
import { Alerta, Aviso, Selo, Vazio, useExecutar } from "../ui.jsx";
import { MOTIVOS, ROTULO_DECISAO, ROTULO_MOTIVO, formatarData } from "../rotulos.js";

const FILTROS = [
  ["", "Todas"],
  ["PENDENTE", "Pendentes"],
  ["ACEITA", "Aceitas"],
  ["REJEITADA", "Rejeitadas"],
];

const NOVA_VAZIA = { ferramenta: "", prompt: "", conteudo: "" };

export default function SugestoesPanel({ historiaId, sugestoes, aoMudar }) {
  const [filtro, setFiltro] = useState("");
  const [nova, setNova] = useState(null);
  const [erro, executar, limparErro] = useExecutar();

  const visiveis = sugestoes.filter((s) => !filtro || s.decisao === filtro);

  const registrar = (e) => {
    e.preventDefault();
    executar(async () => {
      await api.sugestoes.registrar(historiaId, nova);
      setNova(null);
      await aoMudar();
    });
  };

  const campo = (nome) => ({
    value: nova[nome],
    onChange: (e) => setNova({ ...nova, [nome]: e.target.value }),
  });

  return (
    <section className="sugestoes" aria-label="Sugestões da IA">
      <div className="cabecalho-secao">
        <h2>Sugestões da IA</h2>
        {!nova && (
          <button type="button" onClick={() => setNova({ ...NOVA_VAZIA })}>
            Registrar sugestão
          </button>
        )}
      </div>
      <p className="dica">
        Use a ferramenta de IA que preferir e registre aqui o que ela respondeu. A IA rascunha; o grupo decide.
      </p>

      <Alerta erro={erro} aoFechar={limparErro} />

      {nova && (
        <form className="formulario compacto" onSubmit={registrar}>
          <label>
            Ferramenta
            <input {...campo("ferramenta")} placeholder="Claude, ChatGPT, Gemini…" required maxLength={100} />
          </label>
          <label>
            Prompt utilizado
            <textarea {...campo("prompt")} rows={3} required />
          </label>
          <label>
            Conteúdo da sugestão
            <textarea {...campo("conteudo")} rows={5} required />
          </label>
          <div className="acoes">
            <button type="submit" className="primario">
              Registrar sugestão
            </button>
            <button type="button" onClick={() => setNova(null)}>
              Cancelar
            </button>
          </div>
        </form>
      )}

      <div className="filtro">
        <label>
          Mostrar
          <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
            {FILTROS.map(([valor, rotulo]) => (
              <option key={valor} value={valor}>
                {rotulo}
              </option>
            ))}
          </select>
        </label>
      </div>

      {visiveis.length === 0 && <Vazio>Nenhuma sugestão {filtro ? "nesta situação" : "registrada"}.</Vazio>}

      <ul className="lista-sugestoes">
        {visiveis.map((s) => (
          <SugestaoItem key={s.id} sugestao={s} aoMudar={aoMudar} />
        ))}
      </ul>
    </section>
  );
}

function SugestaoItem({ sugestao: s, aoMudar }) {
  const [modo, setModo] = useState(null); // null | "aceitar" | "rejeitar"
  const [motivo, setMotivo] = useState("");
  const [justificativa, setJustificativa] = useState("");
  const [lembrete, setLembrete] = useState(false);
  const [erro, executar, limparErro] = useExecutar();

  const tipoSelo = { PENDENTE: "pendente", ACEITA: "aceita", REJEITADA: "rejeitada" }[s.decisao];

  const confirmar = (e) => {
    e.preventDefault();
    executar(async () => {
      if (modo === "aceitar") {
        await api.sugestoes.aceitar(s.id, { justificativa });
        setLembrete(true);
      } else {
        await api.sugestoes.rejeitar(s.id, { motivo: motivo || null, justificativa });
      }
      setModo(null);
      await aoMudar();
    });
  };

  return (
    <li className={`sugestao sugestao-${s.decisao}`}>
      <header>
        <strong>{s.ferramenta}</strong>
        <Selo tipo={tipoSelo}>{ROTULO_DECISAO[s.decisao]}</Selo>
      </header>
      <p className="meta">
        Registrada por {s.registradaPor.nome} em {formatarData(s.dataRegistro)}
      </p>
      <p className="conteudo-sugestao">{s.conteudo}</p>
      <details>
        <summary>Prompt utilizado</summary>
        <p className="conteudo-sugestao">{s.prompt}</p>
      </details>

      {s.decisao !== "PENDENTE" && (
        <p className="decisao">
          {s.decisao === "ACEITA" ? "Aceita" : `Rejeitada: ${ROTULO_MOTIVO[s.motivoRejeicao]}`} por{" "}
          {s.decididaPor?.nome} em {formatarData(s.dataDecisao)}.
          {s.justificativa && <> Justificativa: {s.justificativa}</>}
        </p>
      )}

      {lembrete && s.decisao === "ACEITA" && (
        <Aviso>
          Aceitar não altera os cenários. Ajuste os cenários e, se vieram desta sugestão, marque a origem como
          “Sugestão da IA”.
        </Aviso>
      )}

      <Alerta erro={erro} aoFechar={limparErro} />

      {s.decisao === "PENDENTE" && !modo && (
        <div className="acoes">
          <button type="button" onClick={() => setModo("aceitar")}>
            Aceitar
          </button>
          <button type="button" onClick={() => setModo("rejeitar")}>
            Rejeitar
          </button>
        </div>
      )}

      {modo && (
        <form className="formulario compacto" onSubmit={confirmar}>
          {modo === "rejeitar" && (
            <label>
              Motivo da rejeição
              <select value={motivo} onChange={(e) => setMotivo(e.target.value)}>
                <option value="">Selecione o motivo</option>
                {MOTIVOS.map((m) => (
                  <option key={m} value={m}>
                    {ROTULO_MOTIVO[m]}
                  </option>
                ))}
              </select>
            </label>
          )}
          <label>
            Justificativa {modo === "aceitar" && "(opcional)"}
            <textarea rows={2} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} />
          </label>
          <div className="acoes">
            <button type="submit" className="primario">
              {modo === "aceitar" ? "Confirmar aceitação" : "Confirmar rejeição"}
            </button>
            <button type="button" onClick={() => setModo(null)}>
              Cancelar
            </button>
          </div>
        </form>
      )}
    </li>
  );
}
