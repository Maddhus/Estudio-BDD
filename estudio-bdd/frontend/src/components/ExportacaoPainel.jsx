/** Mostra o conteúdo .feature devolvido pela API e permite baixar o arquivo. */
export default function ExportacaoPainel({ arquivo, aoFechar }) {
  const baixar = () => {
    const blob = new Blob([arquivo.conteudo], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = arquivo.nomeArquivo;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <section className="exportacao" aria-label="Arquivo .feature exportado">
      <div className="cabecalho-secao">
        <h2>{arquivo.nomeArquivo}</h2>
        <div className="acoes">
          <button type="button" className="primario" onClick={baixar}>
            Baixar arquivo
          </button>
          <button type="button" onClick={aoFechar}>
            Fechar
          </button>
        </div>
      </div>
      <pre>{arquivo.conteudo}</pre>
    </section>
  );
}
