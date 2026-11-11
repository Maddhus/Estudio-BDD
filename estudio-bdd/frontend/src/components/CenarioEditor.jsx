import { useState } from "react";
import { ROTULO_ORIGEM, ROTULO_PASSO, TIPOS_PASSO } from "../rotulos.js";

let contador = 0;
const novaChave = () => `p${++contador}`;

function passosIniciais(cenario) {
  if (cenario) return cenario.passos.map((p) => ({ chave: novaChave(), tipo: p.tipo, texto: p.texto }));
  return [
    { chave: novaChave(), tipo: "DADO", texto: "" },
    { chave: novaChave(), tipo: "QUANDO", texto: "" },
    { chave: novaChave(), tipo: "ENTAO", texto: "" },
  ];
}

/**
 * Edita título, origem e a lista ordenada de passos. A ordem da lista vira a posição de cada passo.
 * A SPA não valida a gramática (Dado → Quando → Então): quem decide é a API, e a mensagem dela é exibida.
 */
export default function CenarioEditor({ cenario, aoSalvar, aoCancelar }) {
  const [titulo, setTitulo] = useState(cenario?.titulo ?? "");
  const [origem, setOrigem] = useState(cenario?.origem ?? "HUMANO");
  const [passos, setPassos] = useState(() => passosIniciais(cenario));

  const alterar = (chave, mudanca) =>
    setPassos((lista) => lista.map((p) => (p.chave === chave ? { ...p, ...mudanca } : p)));

  const mover = (indice, delta) =>
    setPassos((lista) => {
      const destino = indice + delta;
      if (destino < 0 || destino >= lista.length) return lista;
      const copia = [...lista];
      [copia[indice], copia[destino]] = [copia[destino], copia[indice]];
      return copia;
    });

  const remover = (chave) => setPassos((lista) => lista.filter((p) => p.chave !== chave));

  const acrescentar = () =>
    setPassos((lista) => [
      ...lista,
      { chave: novaChave(), tipo: lista.length === 0 ? "DADO" : "E", texto: "" },
    ]);

  const enviar = (e) => {
    e.preventDefault();
    aoSalvar({
      titulo,
      origem,
      passos: passos.map(({ tipo, texto }) => ({ tipo, texto })),
    });
  };

  return (
    <form className="formulario editor-cenario" onSubmit={enviar}>
      <h3>{cenario ? "Editar cenário" : "Novo cenário"}</h3>
      <label>
        Título
        <input
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          placeholder="Cenário sem passo Então é rejeitado"
          required
          maxLength={200}
        />
      </label>
      <label className="curto">
        Origem
        <select value={origem} onChange={(e) => setOrigem(e.target.value)}>
          {Object.entries(ROTULO_ORIGEM).map(([valor, rotulo]) => (
            <option key={valor} value={valor}>
              {rotulo}
            </option>
          ))}
        </select>
      </label>

      <fieldset>
        <legend>Passos, na ordem em que acontecem</legend>
        <ol className="editor-passos">
          {passos.map((p, i) => (
            <li key={p.chave}>
              <select
                aria-label={`Tipo do passo ${i + 1}`}
                value={p.tipo}
                className={`tipo-passo passo-${p.tipo}`}
                onChange={(e) => alterar(p.chave, { tipo: e.target.value })}
              >
                {TIPOS_PASSO.map((t) => (
                  <option key={t} value={t}>
                    {ROTULO_PASSO[t]}
                  </option>
                ))}
              </select>
              <input
                aria-label={`Texto do passo ${i + 1}`}
                value={p.texto}
                onChange={(e) => alterar(p.chave, { texto: e.target.value })}
                placeholder="o analista cadastra o cenário"
                maxLength={500}
              />
              <button type="button" aria-label="Mover para cima" onClick={() => mover(i, -1)} disabled={i === 0}>
                ↑
              </button>
              <button
                type="button"
                aria-label="Mover para baixo"
                onClick={() => mover(i, 1)}
                disabled={i === passos.length - 1}
              >
                ↓
              </button>
              <button type="button" aria-label="Remover passo" className="perigo" onClick={() => remover(p.chave)}>
                Remover
              </button>
            </li>
          ))}
        </ol>
        <button type="button" onClick={acrescentar}>
          Adicionar passo
        </button>
      </fieldset>

      <div className="acoes">
        <button type="submit" className="primario">
          Salvar cenário
        </button>
        <button type="button" onClick={aoCancelar}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
