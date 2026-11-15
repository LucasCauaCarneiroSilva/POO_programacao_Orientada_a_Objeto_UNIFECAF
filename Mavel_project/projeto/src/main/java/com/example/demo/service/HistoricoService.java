package com.example.demo.service;

import com.example.demo.model.AcaoHistorico;
import com.example.demo.model.Auditavel;
import com.example.demo.model.HistoricoAlteracao;
import com.example.demo.repository.HistoricoRepository;
import com.example.demo.util.Diferencas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

// Grava e consulta o histórico de qualquer objeto Auditavel.
@Service
@Transactional
public class HistoricoService {

    private final HistoricoRepository repositorio;

    public HistoricoService(HistoricoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void inclusao(Auditavel alvo, String usuario) {
        repositorio.save(new HistoricoAlteracao(usuario, alvo, AcaoHistorico.INCLUSAO,
                Diferencas.listar(alvo.snapshot()), null));
    }

    // Só grava se algum campo mudou de fato.
    public void alteracao(Auditavel alvo, Map<String, Object> antes, String usuario) {
        String mudancas = Diferencas.comparar(antes, alvo.snapshot());
        if (!mudancas.isEmpty()) {
            repositorio.save(new HistoricoAlteracao(usuario, alvo, AcaoHistorico.ALTERACAO, mudancas, null));
        }
    }

    public void exclusao(Auditavel alvo, String usuario, String motivo) {
        repositorio.save(new HistoricoAlteracao(usuario, alvo, AcaoHistorico.EXCLUSAO,
                Diferencas.listar(alvo.snapshot()), motivo));
    }

    // Últimos 300 registros, com filtro opcional.
    @Transactional(readOnly = true)
    public List<HistoricoAlteracao> listar(String entidade, Long registroId) {
        boolean semEntidade = entidade == null || entidade.isBlank();
        if (semEntidade) {
            return repositorio.findTop300ByOrderByDataHoraDescIdDesc();
        }
        if (registroId != null) {
            return repositorio.findByEntidadeAndRegistroIdOrderByDataHoraDescIdDesc(entidade, registroId);
        }
        return repositorio.findTop300ByEntidadeOrderByDataHoraDescIdDesc(entidade);
    }
}
