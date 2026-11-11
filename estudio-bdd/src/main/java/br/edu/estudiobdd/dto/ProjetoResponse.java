package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Projeto;

import java.time.LocalDateTime;

public record ProjetoResponse(
        Long id,
        String nome,
        String descricao,
        String contextoDominio,
        LocalDateTime dataCriacao) {

    public static ProjetoResponse de(Projeto p) {
        return new ProjetoResponse(p.getId(), p.getNome(), p.getDescricao(), p.getContextoDominio(), p.getDataCriacao());
    }
}
