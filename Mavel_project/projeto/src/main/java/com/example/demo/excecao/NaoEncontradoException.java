package com.example.demo.excecao;

// Registro procurado não existe. A API responde 404 com a mensagem.
public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
