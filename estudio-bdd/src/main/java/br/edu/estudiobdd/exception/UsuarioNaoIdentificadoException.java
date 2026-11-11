package br.edu.estudiobdd.exception;

/** Identidade simulada ausente ou inválida (cabeçalho X-Usuario-Id) (resposta HTTP 401). */
public class UsuarioNaoIdentificadoException extends RuntimeException {

    public UsuarioNaoIdentificadoException(String mensagem) {
        super(mensagem);
    }
}
