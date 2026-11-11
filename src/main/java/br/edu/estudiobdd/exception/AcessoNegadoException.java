package br.edu.estudiobdd.exception;

/** Usuário identificado, mas sem permissão para a operação (resposta HTTP 403). */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
