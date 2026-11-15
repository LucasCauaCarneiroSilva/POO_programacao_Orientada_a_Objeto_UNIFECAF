package com.example.demo.model;

import com.example.demo.util.Validacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// Veículo do estoque. A marca e o tipo vêm do modelo.
@Entity
@Table(name = "veiculo")
public class Veiculo extends EntidadeBase {

    public static final int TAMANHO_COR = 30;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, length = TAMANHO_COR)
    private String cor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quilometragem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVeiculo status;

    // Construtor vazio exigido pelo JPA
    protected Veiculo() {
    }

    // Sobrecarga: sem status, o veículo entra como DISPONIVEL
    public Veiculo(Modelo modelo, Integer ano, String cor, BigDecimal preco, Integer quilometragem) {
        this(modelo, ano, cor, preco, quilometragem, StatusVeiculo.DISPONIVEL);
    }

    public Veiculo(Modelo modelo, Integer ano, String cor, BigDecimal preco, Integer quilometragem,
                   StatusVeiculo status) {
        setModelo(modelo);
        setAno(ano);
        setCor(cor);
        setPreco(preco);
        setQuilometragem(quilometragem);
        setStatus(status == null ? StatusVeiculo.DISPONIVEL : status);
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = Validacao.obrigatorio(modelo, "Modelo");
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = Validacao.ano(ano);
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = Validacao.texto(cor, "Cor", TAMANHO_COR);
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = Validacao.preco(preco);
    }

    public Integer getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Integer quilometragem) {
        this.quilometragem = Validacao.quilometragem(quilometragem);
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = Validacao.obrigatorio(status, "Status");
    }

    // Texto usado na busca livre
    public String textoBusca() {
        return String.join(" ",
                modelo.getMarca().getNome(), modelo.getNome(), modelo.getTipo().getNome(),
                cor, String.valueOf(ano), status.getRotulo());
    }

    @Override
    public String nomeEntidade() {
        return "Veículo";
    }

    @Override
    public String descricao() {
        return modelo.descricao() + " " + ano + " " + cor;
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("modelo", modelo.descricao());
        valores.put("ano", ano);
        valores.put("cor", cor);
        valores.put("preço", preco);
        valores.put("quilometragem", quilometragem);
        valores.put("status", status.getRotulo());
        return valores;
    }
}
