package com.example.demo.model;

import com.example.demo.util.Texto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Registro de quem incluiu, alterou ou excluiu o quê. Continua salvo após a remoção do veículo.
@Entity
@Table(name = "historico_alteracao")
public class HistoricoAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 60)
    private String usuario;

    @Column(nullable = false, length = 30)
    private String entidade;

    @Column(name = "registro_id")
    private Long registroId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private AcaoHistorico acao;

    // Descrição do registro no momento da ação ("Fiat Argo 2022 Prata").
    @Column(nullable = false, length = 200)
    private String registro;

    // "campo: antes → depois" (alteração) ou todos os valores (inclusão/exclusão).
    @Column(length = 2000)
    private String detalhes;

    @Column(length = 200)
    private String motivo;

    protected HistoricoAlteracao() {
        // exigido pelo JPA
    }

    public HistoricoAlteracao(String usuario, Auditavel alvo, AcaoHistorico acao, String detalhes,
                              String motivo) {
        this.dataHora = LocalDateTime.now();
        this.usuario = Texto.usuario(usuario);
        this.entidade = alvo.nomeEntidade();
        this.registroId = alvo.getId();
        this.acao = acao;
        this.registro = Texto.limitar(alvo.descricao(), 200);
        this.detalhes = Texto.limitar(detalhes, 2000);
        this.motivo = (motivo == null || motivo.isBlank()) ? null : Texto.limitar(motivo.trim(), 200);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getEntidade() {
        return entidade;
    }

    public Long getRegistroId() {
        return registroId;
    }

    public AcaoHistorico getAcao() {
        return acao;
    }

    public String getRegistro() {
        return registro;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String getMotivo() {
        return motivo;
    }
}
