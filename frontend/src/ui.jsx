import { useCallback, useState } from "react";

/** Mensagem de erro vinda da API (ProblemDetail): detalhe em português e erros por campo. */
export function Alerta({ erro, aoFechar }) {
  if (!erro) return null;
  return (
    <div className="alerta" role="alert">
      <div>
        <strong>{erro.titulo || "Algo deu errado"}</strong>
        <p>{erro.message}</p>
        {erro.erros?.length > 0 && (
          <ul>
            {erro.erros.map((e, i) => (
              <li key={i}>
                {e.campo}: {e.mensagem}
              </li>
            ))}
          </ul>
        )}
      </div>
      {aoFechar && (
        <button type="button" className="botao-texto" onClick={aoFechar}>
          Fechar
        </button>
      )}
    </div>
  );
}

export function Aviso({ children }) {
  return <p className="aviso">{children}</p>;
}

export function Vazio({ children }) {
  return <p className="vazio">{children}</p>;
}

export function Selo({ tipo = "neutro", children }) {
  return <span className={`selo selo-${tipo}`}>{children}</span>;
}

/** Guarda o último erro e executa uma ação assíncrona capturando falhas da API. */
export function useExecutar() {
  const [erro, setErro] = useState(null);
  const executar = useCallback(async (acao) => {
    setErro(null);
    try {
      return await acao();
    } catch (e) {
      setErro(e);
      return undefined;
    }
  }, []);
  return [erro, executar, () => setErro(null)];
}
