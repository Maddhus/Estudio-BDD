package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.OrigemCenario;
import br.edu.estudiobdd.model.ResumoProjeto;

import java.util.List;
import java.util.Map;

public record ResumoProjetoResponse(
        long totalHistorias,
        Map<OrigemCenario, Long> cenariosPorOrigem,
        Map<DecisaoSugestao, Long> sugestoesPorDecisao,
        List<MotivoFrequenteResponse> motivosRejeicaoMaisFrequentes) {

    public record MotivoFrequenteResponse(MotivoRejeicao motivo, long quantidade) {
    }

    public static ResumoProjetoResponse de(ResumoProjeto r) {
        return new ResumoProjetoResponse(
                r.totalHistorias(),
                r.cenariosPorOrigem(),
                r.sugestoesPorDecisao(),
                r.motivosRejeicaoMaisFrequentes().stream()
                        .map(m -> new MotivoFrequenteResponse(m.motivo(), m.quantidade()))
                        .toList());
    }
}
