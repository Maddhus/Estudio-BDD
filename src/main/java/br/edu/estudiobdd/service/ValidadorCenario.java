package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.TipoPasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Regra 2: gramática do cenário. Um cenário válido tem ao menos um passo Quando e um passo Então,
 * segue a ordem Dado → Quando → Então, e os passos E / Mas continuam o tipo anterior
 * (não podem iniciar o cenário). A lista recebida já está na ordem em que os passos aparecem.
 */
public final class ValidadorCenario {

    private ValidadorCenario() {
    }

    public static void validar(List<Passo> passos) {
        if (passos == null || passos.isEmpty()) {
            throw new RegraNegocioException(
                    "Cenário inválido: informe ao menos um passo Quando e um passo Então.");
        }

        List<String> erros = new ArrayList<>();
        TipoPasso efetivo = null;   // tipo "em vigor": E/Mas herdam o do passo anterior
        boolean temQuando = false;
        boolean temEntao = false;

        for (int i = 0; i < passos.size(); i++) {
            TipoPasso tipo = passos.get(i).getTipo();
            int numero = i + 1;

            if (tipo == null) {
                erros.add("O passo " + numero + " não tem tipo.");
                continue;
            }

            if (tipo.ehContinuacao()) {
                if (efetivo == null) {
                    erros.add("O passo " + numero + " (" + tipo.getRotulo()
                            + ") não pode iniciar o cenário: E e Mas continuam o tipo anterior.");
                }
                continue;
            }

            if (efetivo != null && tipo.getOrdem() < efetivo.getOrdem()) {
                erros.add("O passo " + numero + " (" + tipo.getRotulo()
                        + ") está fora de ordem: a sequência deve ser Dado → Quando → Então.");
            } else {
                efetivo = tipo;
            }

            if (tipo == TipoPasso.QUANDO) {
                temQuando = true;
            } else if (tipo == TipoPasso.ENTAO) {
                temEntao = true;
            }
        }

        if (!temQuando) {
            erros.add("O cenário precisa ter ao menos um passo Quando.");
        }
        if (!temEntao) {
            erros.add("O cenário precisa ter ao menos um passo Então.");
        }
        if (!erros.isEmpty()) {
            throw new RegraNegocioException("Cenário inválido: " + String.join(" ", erros));
        }
    }
}
