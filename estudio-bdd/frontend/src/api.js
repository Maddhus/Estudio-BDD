// Cliente HTTP da API. A SPA não tem regra de negócio: tudo que é validado, é validado no backend.
const BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

let usuarioAtualId = null;

/** Identidade simulada: o id vai no cabeçalho X-Usuario-Id de todas as requisições. */
export function definirUsuarioAtual(id) {
  usuarioAtualId = id;
}

export class ErroApi extends Error {
  constructor(status, problema) {
    super(problema?.detail || `Erro ${status} ao falar com a API.`);
    this.status = status;
    this.titulo = problema?.title || "Erro";
    this.erros = problema?.erros || [];
  }
}

async function requisicao(metodo, caminho, corpo, { cabecalhos = false } = {}) {
  const headers = {};
  if (corpo !== undefined) headers["Content-Type"] = "application/json";
  if (usuarioAtualId) headers["X-Usuario-Id"] = String(usuarioAtualId);

  let resp;
  try {
    resp = await fetch(BASE + caminho, {
      method: metodo,
      headers,
      body: corpo !== undefined ? JSON.stringify(corpo) : undefined,
    });
  } catch {
    throw new ErroApi(0, {
      title: "API indisponível",
      detail: `Não foi possível falar com a API em ${BASE}. Ela está em execução?`,
    });
  }

  if (!resp.ok) {
    let problema = null;
    try {
      problema = await resp.json();
    } catch {
      /* resposta sem corpo JSON */
    }
    throw new ErroApi(resp.status, problema);
  }
  if (resp.status === 204) return null;
  if (cabecalhos) return resp;
  return resp.json();
}

const consulta = (params) => {
  const q = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => v && q.set(k, v));
  const s = q.toString();
  return s ? `?${s}` : "";
};

export const api = {
  usuarios: {
    listar: () => requisicao("GET", "/usuarios"),
  },
  projetos: {
    listar: () => requisicao("GET", "/projetos"),
    buscar: (id) => requisicao("GET", `/projetos/${id}`),
    criar: (dados) => requisicao("POST", "/projetos", dados),
    atualizar: (id, dados) => requisicao("PUT", `/projetos/${id}`, dados),
    excluir: (id) => requisicao("DELETE", `/projetos/${id}`),
    historias: (id, prioridade) =>
      requisicao("GET", `/projetos/${id}/historias${consulta({ prioridade })}`),
    criarHistoria: (id, dados) => requisicao("POST", `/projetos/${id}/historias`, dados),
    resumo: (id) => requisicao("GET", `/projetos/${id}/resumo`),
  },
  historias: {
    buscar: (id) => requisicao("GET", `/historias/${id}`),
    atualizar: (id, dados) => requisicao("PUT", `/historias/${id}`, dados),
    excluir: (id) => requisicao("DELETE", `/historias/${id}`),
    /** Devolve { conteudo, nomeArquivo } do .feature (ou lança ErroApi se a exportação for bloqueada). */
    async feature(id) {
      const resp = await requisicao("GET", `/historias/${id}/feature`, undefined, { cabecalhos: true });
      const disposicao = resp.headers.get("Content-Disposition") || "";
      const m = disposicao.match(/filename="?([^";]+)"?/);
      return { conteudo: await resp.text(), nomeArquivo: m ? m[1] : `historia-${id}.feature` };
    },
  },
  cenarios: {
    criar: (historiaId, dados) => requisicao("POST", `/historias/${historiaId}/cenarios`, dados),
    atualizar: (id, dados) => requisicao("PUT", `/cenarios/${id}`, dados),
    excluir: (id) => requisicao("DELETE", `/cenarios/${id}`),
  },
  sugestoes: {
    listar: (historiaId, decisao) =>
      requisicao("GET", `/historias/${historiaId}/sugestoes${consulta({ decisao })}`),
    registrar: (historiaId, dados) => requisicao("POST", `/historias/${historiaId}/sugestoes`, dados),
    aceitar: (id, dados) => requisicao("POST", `/sugestoes/${id}/aceitar`, dados),
    rejeitar: (id, dados) => requisicao("POST", `/sugestoes/${id}/rejeitar`, dados),
  },
};
