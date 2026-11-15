package com.example.demo.repository;

import com.example.demo.model.HistoricoAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoRepository extends JpaRepository<HistoricoAlteracao, Long> {

    List<HistoricoAlteracao> findTop300ByOrderByDataHoraDescIdDesc();

    List<HistoricoAlteracao> findTop300ByEntidadeOrderByDataHoraDescIdDesc(String entidade);

    List<HistoricoAlteracao> findByEntidadeAndRegistroIdOrderByDataHoraDescIdDesc(String entidade, Long registroId);
}
