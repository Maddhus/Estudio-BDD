package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Prioridade;

import java.time.LocalDateTime;
import java.util.List;

public record HistoriaResponse(
        Long id,
        Long projetoId,
        String titulo,
        String papel,
        String objetivo,
        String beneficio,
        Prioridade prioridade,
        UsuarioResponse autor,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        long sugestoesPendentes,
        List<CenarioResponse> cenarios) {

    public static HistoriaResponse de(HistoriaUsuario h) {
        long pendentes = h.getSugestoes().stream()
                .filter(s -> s.getDecisao() == DecisaoSugestao.PENDENTE)
                .count();
        return new HistoriaResponse(
                h.getId(),
                h.getProjeto().getId(),
                h.getTitulo(),
                h.getPapel(),
                h.getObjetivo(),
                h.getBeneficio(),
                h.getPrioridade(),
                UsuarioResponse.de(h.getAutor()),
                h.getDataCriacao(),
                h.getDataAtualizacao(),
                pendentes,
                h.getCenarios().stream().map(CenarioResponse::de).toList());
    }
}
