package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.AcessoNegadoException;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Usuario;

/** Regra 3: somente o autor edita ou exclui a história e seus cenários. */
final class RegrasAutoria {

    private RegrasAutoria() {
    }

    static void exigirAutor(HistoriaUsuario historia, Usuario usuario) {
        Long autorId = historia.getAutor().getId();
        if (autorId == null || !autorId.equals(usuario.getId())) {
            throw new AcessoNegadoException(
                    "Somente o autor da história (" + historia.getAutor().getNome()
                            + ") pode alterá-la ou excluí-la, incluindo seus cenários e passos.");
        }
    }
}
