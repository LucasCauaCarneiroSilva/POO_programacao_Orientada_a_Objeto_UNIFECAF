package com.example.demo.model;

import com.example.demo.util.Validacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.LinkedHashMap;
import java.util.Map;

// Marca do veículo: Fiat, Toyota... Uma marca tem vários modelos.
@Entity
@Table(name = "marca")
public class Marca extends EntidadeBase {

    public static final int TAMANHO_NOME = 60;

    @Column(nullable = false, unique = true, length = TAMANHO_NOME)
    private String nome;

    // Construtor vazio exigido pelo JPA
    protected Marca() {
    }

    public Marca(String nome) {
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarNome(nome);
    }

    public static String validarNome(String nome) {
        return Validacao.texto(nome, "Nome da marca", TAMANHO_NOME);
    }

    @Override
    public String nomeEntidade() {
        return "Marca";
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
