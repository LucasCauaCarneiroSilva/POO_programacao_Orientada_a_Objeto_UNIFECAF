package com.example.demo.dto;

import com.example.demo.model.StatusVeiculo;

import java.math.BigDecimal;

// Dados recebidos pela API (records).
public final class Formularios {

    private Formularios() {
    }

    // Tipo ou marca: só o nome.
    public record NomeForm(String nome) {
    }

    public record ModeloForm(String nome, Long marcaId, Long tipoId) {
    }

    // Cadastro e edição completa do veículo.
    public record VeiculoForm(Long modeloId, Integer ano, String cor, BigDecimal preco,
                              Integer quilometragem, StatusVeiculo status) {
    }

    // Atualização rápida: só os campos enviados mudam (preço, km, status).
    public record VeiculoAjuste(BigDecimal preco, Integer quilometragem, StatusVeiculo status) {

        public boolean vazio() {
            return preco == null && quilometragem == null && status == null;
        }
    }
}
