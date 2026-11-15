package com.example.demo.model;

import com.example.demo.util.Validacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.LinkedHashMap;
import java.util.Map;

// Modelo de uma marca (Argo, Corolla...). Associação com Marca e TipoVeiculo.
@Entity
@Table(name = "modelo",
        uniqueConstraints = @UniqueConstraint(name = "uk_modelo_marca_nome", columnNames = {"marca_id", "nome"}))
public class Modelo extends EntidadeBase {

    public static final int TAMANHO_NOME = 60;

    @Column(nullable = false, length = TAMANHO_NOME)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoVeiculo tipo;

    // Construtor vazio exigido pelo JPA
    protected Modelo() {
    }

    public Modelo(String nome, Marca marca, TipoVeiculo tipo) {
        setNome(nome);
        setMarca(marca);
        setTipo(tipo);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarNome(nome);
    }

    public static String validarNome(String nome) {
        return Validacao.texto(nome, "Nome do modelo", TAMANHO_NOME);
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = Validacao.obrigatorio(marca, "Marca");
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = Validacao.obrigatorio(tipo, "Tipo");
    }

    @Override
    public String nomeEntidade() {
        return "Modelo";
    }

    @Override
    public String descricao() {
        return marca.getNome() + " " + nome;
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome", nome);
        valores.put("marca", marca.getNome());
        valores.put("tipo", tipo.getNome());
        return valores;
    }
}
