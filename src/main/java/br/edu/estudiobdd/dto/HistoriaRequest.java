package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HistoriaRequest(
        @NotBlank(message = "O título é obrigatório.") @Size(max = 200) String titulo,
        @NotBlank(message = "O papel (Como...) é obrigatório.") @Size(max = 200) String papel,
        @NotBlank(message = "O objetivo (quero...) é obrigatório.") @Size(max = 300) String objetivo,
        @NotBlank(message = "O benefício (para...) é obrigatório.") @Size(max = 300) String beneficio,
        @NotNull(message = "A prioridade é obrigatória (ALTA, MEDIA ou BAIXA).") Prioridade prioridade) {

    public HistoriaUsuario paraEntidade() {
        return new HistoriaUsuario(titulo, papel, objetivo, beneficio, prioridade);
    }
}
