package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.TipoPasso;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.edu.estudiobdd.service.Fabrica.passo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regra 2: gramática do cenário (Dado → Quando → Então; E/Mas não iniciam). */
class ValidadorCenarioTest {

    private static String mensagemDe(List<Passo> passos) {
        return assertThrows(RegraNegocioException.class, () -> ValidadorCenario.validar(passos)).getMessage();
    }

    @Test
    void cenarioCompletoEmOrdemEValido() {
        assertDoesNotThrow(() -> ValidadorCenario.validar(List.of(
                passo(TipoPasso.DADO, "uma história cadastrada"),
                passo(TipoPasso.E, "um projeto existente"),
                passo(TipoPasso.QUANDO, "o analista cadastra o cenário"),
                passo(TipoPasso.ENTAO, "o cenário é salvo"),
                passo(TipoPasso.E, "a posição é 1"))));
    }

    @Test
    void dadoEOpcional() {
        assertDoesNotThrow(() -> ValidadorCenario.validar(List.of(
                passo(TipoPasso.QUANDO, "o analista abre o resumo"),
                passo(TipoPasso.ENTAO, "os totais são exibidos"))));
    }

    @Test
    void semPassoQuandoERejeitado() {
        String msg = mensagemDe(List.of(
                passo(TipoPasso.DADO, "uma história"),
                passo(TipoPasso.ENTAO, "algo acontece")));
        assertTrue(msg.contains("Quando"), msg);
    }

    @Test
    void semPassoEntaoERejeitado() {
        String msg = mensagemDe(List.of(
                passo(TipoPasso.DADO, "uma história"),
                passo(TipoPasso.QUANDO, "o analista age")));
        assertTrue(msg.contains("Então"), msg);
    }

    @Test
    void passoForaDeOrdemERejeitado() {
        String msg = mensagemDe(List.of(
                passo(TipoPasso.QUANDO, "o analista age"),
                passo(TipoPasso.DADO, "uma história"),
                passo(TipoPasso.ENTAO, "algo acontece")));
        assertTrue(msg.contains("fora de ordem"), msg);
    }

    @Test
    void cenarioIniciadoPorEOuMasERejeitado() {
        assertTrue(mensagemDe(List.of(
                passo(TipoPasso.E, "algo"),
                passo(TipoPasso.QUANDO, "o analista age"),
                passo(TipoPasso.ENTAO, "algo acontece"))).contains("não pode iniciar"));
        assertTrue(mensagemDe(List.of(
                passo(TipoPasso.MAS, "algo"),
                passo(TipoPasso.QUANDO, "o analista age"),
                passo(TipoPasso.ENTAO, "algo acontece"))).contains("não pode iniciar"));
    }

    @Test
    void listaVaziaERejeitada() {
        assertThrows(RegraNegocioException.class, () -> ValidadorCenario.validar(List.of()));
    }
}
