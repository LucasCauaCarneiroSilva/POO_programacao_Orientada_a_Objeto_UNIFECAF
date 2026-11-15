package com.example.demo;

import com.example.demo.model.Marca;
import com.example.demo.model.Modelo;
import com.example.demo.model.StatusVeiculo;
import com.example.demo.model.TipoVeiculo;
import com.example.demo.model.Veiculo;

import java.math.BigDecimal;

/** Objetos prontos para os testes de unidade (sem banco). */
public final class Exemplos {

    private Exemplos() {
    }

    public static Modelo modelo(String marca, String modelo, String tipo) {
        return new Modelo(modelo, new Marca(marca), new TipoVeiculo(tipo));
    }

    public static Veiculo veiculo(String marca, String modelo, String tipo, int ano, String cor,
                                  String preco, StatusVeiculo status) {
        return new Veiculo(modelo(marca, modelo, tipo), ano, cor, new BigDecimal(preco), 10_000, status);
    }
}
