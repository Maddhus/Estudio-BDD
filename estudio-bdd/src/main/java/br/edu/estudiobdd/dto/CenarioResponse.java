package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.OrigemCenario;

import java.util.List;

public record CenarioResponse(
        Long id,
        Long historiaId,
        String titulo,
        int posicao,
        OrigemCenario origem,
        List<PassoResponse> passos) {

    public static CenarioResponse de(Cenario c) {
        return new CenarioResponse(
                c.getId(),
                c.getHistoria().getId(),
                c.getTitulo(),
                c.getPosicao(),
                c.getOrigem(),
                c.getPassos().stream().map(PassoResponse::de).toList());
    }
}
