package br.edu.estudiobdd.exception;

/** Violação de uma regra de negócio (resposta HTTP 422). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
