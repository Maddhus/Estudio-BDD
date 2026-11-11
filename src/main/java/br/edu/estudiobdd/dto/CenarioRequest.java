package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.OrigemCenario;
import br.edu.estudiobdd.model.Passo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CenarioRequest(
        @NotBlank(message = "O título do cenário é obrigatório.") @Size(max = 200) String titulo,
        OrigemCenario origem,
        @NotNull(message = "A lista de passos é obrigatória.") @Valid List<PassoRequest> passos) {

    /** Converte para um cenário ainda não persistido; as posições dos passos são definidas pelo serviço. */
    public Cenario paraEntidade() {
        Cenario cenario = new Cenario(titulo, origem);
        for (PassoRequest p : passos) {
            Passo passo = p.paraEntidade();
            cenario.getPassos().add(passo);
        }
        return cenario;
    }
}
