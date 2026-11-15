package com.example.demo.excecao;

// Dado inválido ou operação não permitida. A API responde 400 com a mensagem.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
