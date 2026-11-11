package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.TipoPasso;

public record PassoResponse(Long id, TipoPasso tipo, String texto, int posicao) {

    public static PassoResponse de(Passo p) {
        return new PassoResponse(p.getId(), p.getTipo(), p.getTexto(), p.getPosicao());
    }
}
