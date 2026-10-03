package br.com.fiap.Excecao;

import br.com.fiap.Model.Dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({RequisicaoInvalidaException.class, AcaoDesconhecidaException.class})
    public ResponseEntity<ErrorResponse> tratarRequisicaoInvalida(RuntimeException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({MetodoNaoPermitidoException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> tratarMetodoNaoPermitido(Exception ex) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> tratarMediaTypeNaoSuportado(HttpMediaTypeNotSupportedException ex) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type não suportado. Utilize application/json.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tratarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Parâmetro \"" + ex.getName() + "\" inválido.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> tratarParametroAusente(MissingServletRequestParameterException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Parâmetro \"" + ex.getParameterName() + "\" é obrigatório.");
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> tratarRotaInexistente(Exception ex) {
        return responder(HttpStatus.NOT_FOUND, "Recurso não encontrado.");
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<ErrorResponse> tratarPersistencia(PersistenciaException ex) {
        LOGGER.error("Erro de persistência: {}", ex.getMessage(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarInesperado(Exception ex) {
        LOGGER.error("Erro inesperado: {}", ex.getMessage(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErrorResponse(mensagem));
    }
}
