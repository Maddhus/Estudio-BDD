package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.MotivoRejeicao;
import jakarta.validation.constraints.Size;

/** O motivo NÃO tem @NotNull de propósito: a regra "rejeição exige motivo" é do serviço (camada de negócio). */
public record RejeitarSugestaoRequest(MotivoRejeicao motivo, @Size(max = 1000) String justificativa) {
}
