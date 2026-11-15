package com.example.demo.model;

import com.example.demo.util.Validacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.LinkedHashMap;
import java.util.Map;

// Tipo de veículo: Hatch, Sedã, SUV, Picape, Moto...
@Entity
@Table(name = "tipo_veiculo")
public class TipoVeiculo extends EntidadeBase {

    public static final int TAMANHO_NOME = 40;

    @Column(nullable = false, unique = true, length = TAMANHO_NOME)
    private String nome;

    // Construtor vazio exigido pelo JPA
    protected TipoVeiculo() {
    }

    public TipoVeiculo(String nome) {
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarNome(nome);
    }

    public static String validarNome(String nome) {
        return Validacao.texto(nome, "Nome do tipo", TAMANHO_NOME);
    }

    @Override
    public String nomeEntidade() {
        return "Tipo";
    }

    @Override
    public String descricao() {
        return nome;
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome", nome);
        return valores;
    }
}
