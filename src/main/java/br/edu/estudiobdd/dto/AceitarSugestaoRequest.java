package br.edu.estudiobdd.dto;

import jakarta.validation.constraints.Size;

public record AceitarSugestaoRequest(@Size(max = 1000) String justificativa) {
}
