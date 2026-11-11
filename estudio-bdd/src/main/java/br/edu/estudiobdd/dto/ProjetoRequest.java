package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Projeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjetoRequest(
        @NotBlank(message = "O nome do projeto é obrigatório.") @Size(max = 120) String nome,
        @Size(max = 500) String descricao,
        String contextoDominio) {

    public Projeto paraEntidade() {
        return new Projeto(nome, descricao, contextoDominio);
    }
}
