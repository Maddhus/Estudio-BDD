package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.exception.AcessoNegadoException;
import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.exception.UsuarioNaoIdentificadoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Tratamento de erros padronizado: toda falha vira um ProblemDetail (RFC 9457, application/problem+json)
 * com status, título, detalhe em português e, nas validações de campo, a lista "erros".
 */
@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), "nao-encontrado");
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail regraDeNegocio(RegraNegocioException ex) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.getMessage(), "regra-de-negocio");
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException ex) {
        return problema(HttpStatus.CONFLICT, "Conflito", ex.getMessage(), "conflito");
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail acessoNegado(AcessoNegadoException ex) {
        return problema(HttpStatus.FORBIDDEN, "Acesso negado", ex.getMessage(), "acesso-negado");
    }

    @ExceptionHandler(UsuarioNaoIdentificadoException.class)
    public ProblemDetail naoIdentificado(UsuarioNaoIdentificadoException ex) {
        return problema(HttpStatus.UNAUTHORIZED, "Usuário não identificado", ex.getMessage(), "nao-identificado");
    }

    /** Rede de segurança: a unicidade também é garantida por constraints no banco. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade", ex);
        return problema(HttpStatus.CONFLICT, "Violação de integridade",
                "A operação viola uma restrição de unicidade ou de relacionamento do banco de dados.", "integridade");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente ou consulte o log do servidor.", "erro-interno");
    }

    // ---- erros padrão do Spring MVC, com mensagens em português ----

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Um ou mais campos são inválidos.", "validacao");
        List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                        "campo", e.getField(),
                        "mensagem", e.getDefaultMessage() == null ? "valor inválido" : e.getDefaultMessage()))
                .toList();
        pd.setProperty("erros", erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Requisição ilegível",
                "O corpo da requisição não é um JSON válido ou contém um valor não aceito "
                        + "(confira os valores dos enumeradores, como tipo do passo e prioridade).", "corpo-ilegivel");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    private static ProblemDetail problema(HttpStatus status, String titulo, String detalhe, String tipo) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        pd.setType(URI.create("urn:estudio-bdd:erro:" + tipo));
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}
