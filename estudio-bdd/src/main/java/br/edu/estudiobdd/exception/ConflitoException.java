package br.edu.estudiobdd.exception;

/** Conflito com o estado atual (duplicidade ou sugestão já decidida) (resposta HTTP 409). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
