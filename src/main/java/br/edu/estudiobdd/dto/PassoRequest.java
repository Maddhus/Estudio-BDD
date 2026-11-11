package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.TipoPasso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dentro de um cenário a ordem é a da lista (o campo "posicao" é ignorado).
 * Nos endpoints de passo isolado, "posicao" (1 = primeiro) é opcional.
 */
public record PassoRequest(
        @NotNull(message = "O tipo do passo é obrigatório (DADO, QUANDO, ENTAO, E ou MAS).") TipoPasso tipo,
        @NotBlank(message = "O texto do passo é obrigatório.") @Size(max = 500) String texto,
        @Min(value = 1, message = "A posição começa em 1.") Integer posicao) {

    public Passo paraEntidade() {
        return new Passo(tipo, texto);
    }
}
