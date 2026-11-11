package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Sugestao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SugestaoRequest(
        @NotBlank(message = "Informe a ferramenta de IA usada.") @Size(max = 100) String ferramenta,
        @NotBlank(message = "Informe o prompt utilizado.") String prompt,
        @NotBlank(message = "Informe o conteúdo da sugestão.") String conteudo) {

    public Sugestao paraEntidade() {
        return new Sugestao(ferramenta, prompt, conteudo);
    }
}
