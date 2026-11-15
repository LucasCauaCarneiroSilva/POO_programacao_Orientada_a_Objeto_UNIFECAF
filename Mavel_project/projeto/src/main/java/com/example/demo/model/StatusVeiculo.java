package com.example.demo.model;

// Situação do veículo. emEstoque = ainda pode ser vendido.
public enum StatusVeiculo {

    DISPONIVEL("Disponível", true),
    RESERVADO("Reservado", true),
    VENDIDO("Vendido", false),
    DESCONTINUADO("Descontinuado", false);

    private final String rotulo;
    private final boolean emEstoque;

    StatusVeiculo(String rotulo, boolean emEstoque) {
        this.rotulo = rotulo;
        this.emEstoque = emEstoque;
    }

    public String getRotulo() {
        return rotulo;
    }

    public boolean isEmEstoque() {
        return emEstoque;
    }
}
