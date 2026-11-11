// Textos exibidos para os valores dos enumeradores da API.
export const ROTULO_PASSO = { DADO: "Dado", QUANDO: "Quando", ENTAO: "Então", E: "E", MAS: "Mas" };
export const TIPOS_PASSO = Object.keys(ROTULO_PASSO);

export const ROTULO_PRIORIDADE = { ALTA: "Alta", MEDIA: "Média", BAIXA: "Baixa" };
export const PRIORIDADES = Object.keys(ROTULO_PRIORIDADE);

export const ROTULO_ORIGEM = { HUMANO: "Escrito pelo grupo", SUGESTAO_IA: "Sugestão da IA" };

export const ROTULO_DECISAO = { PENDENTE: "Pendente", ACEITA: "Aceita", REJEITADA: "Rejeitada" };

export const ROTULO_MOTIVO = {
  INCORRETA: "Incorreta",
  GENERICA: "Genérica",
  DUPLICADA: "Duplicada",
  FORA_DO_ESCOPO: "Fora do escopo",
  OUTRO: "Outro",
};
export const MOTIVOS = Object.keys(ROTULO_MOTIVO);

export function formatarData(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}
