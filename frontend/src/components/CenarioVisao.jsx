import { Selo } from "../ui.jsx";
import { ROTULO_ORIGEM, ROTULO_PASSO } from "../rotulos.js";

/** Cenário em modo leitura: os passos aparecem como num arquivo Gherkin, com a palavra-chave em coluna. */
export default function CenarioVisao({ cenario, aoEditar, aoExcluir }) {
  return (
    <article className="cenario">
      <header>
        <h3>
          <span className="rotulo-cenario">Cenário:</span> {cenario.titulo}
        </h3>
        <div className="acoes">
          {cenario.origem === "SUGESTAO_IA" && <Selo tipo="ia">{ROTULO_ORIGEM.SUGESTAO_IA}</Selo>}
          <button type="button" onClick={aoEditar}>
            Editar
          </button>
          <button type="button" className="perigo" onClick={aoExcluir}>
            Excluir
          </button>
        </div>
      </header>
      <ol className="passos">
        {cenario.passos.map((p) => (
          <li key={p.id} className={`passo passo-${p.tipo}`}>
            <span className="palavra">{ROTULO_PASSO[p.tipo]}</span>
            <span className="texto">{p.texto}</span>
          </li>
        ))}
      </ol>
    </article>
  );
}
