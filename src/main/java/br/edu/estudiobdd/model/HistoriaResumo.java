package br.edu.estudiobdd.model;

/** Linha da consulta do projeto: história com a quantidade de cenários e de sugestões pendentes. */
public record HistoriaResumo(
        Long id,
        String titulo,
        Prioridade prioridade,
        String autorNome,
        long totalCenarios,
        long sugestoesPendentes) {
}
