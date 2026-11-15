package com.example.demo.controller;

import com.example.demo.excecao.NaoEncontradoException;
import com.example.demo.excecao.RegraNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Transforma as exceções em resposta JSON {"erro": "..."}.
@RestControllerAdvice
public class ErroHandler {

    public record Erro(String erro) {
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Erro> naoEncontrado(NaoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Erro> regra(RegraNegocioException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            BindException.class})
    public ResponseEntity<Erro> dadosInvalidos(Exception e) {
        return responder(HttpStatus.BAD_REQUEST,
                "Dados inválidos: confira números, datas e o status enviados.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Erro> conflito(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT,
                "A operação conflita com registros existentes (nome repetido ou registro em uso).");
    }

    private ResponseEntity<Erro> responder(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new Erro(mensagem));
    }
}
