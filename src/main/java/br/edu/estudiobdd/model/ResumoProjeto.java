package br.edu.estudiobdd.model;

import java.util.List;
import java.util.Map;

/** Indicadores do projeto (regra 7). */
public record ResumoProjeto(
        long totalHistorias,
        Map<OrigemCenario, Long> cenariosPorOrigem,
        Map<DecisaoSugestao, Long> sugestoesPorDecisao,
        List<MotivoFrequente> motivosRejeicaoMaisFrequentes) {

    public record MotivoFrequente(MotivoRejeicao motivo, long quantidade) {
    }
}
