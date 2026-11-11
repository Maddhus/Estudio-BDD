package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.HistoriaResumo;
import br.edu.estudiobdd.model.Prioridade;

public record HistoriaResumoResponse(
        Long id,
        String titulo,
        Prioridade prioridade,
        String autorNome,
        long totalCenarios,
        long sugestoesPendentes) {

    public static HistoriaResumoResponse de(HistoriaResumo r) {
        return new HistoriaResumoResponse(r.id(), r.titulo(), r.prioridade(), r.autorNome(),
                r.totalCenarios(), r.sugestoesPendentes());
    }
}
