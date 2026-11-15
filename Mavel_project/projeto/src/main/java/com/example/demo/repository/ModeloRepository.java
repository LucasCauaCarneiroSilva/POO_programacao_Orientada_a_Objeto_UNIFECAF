package com.example.demo.repository;

import com.example.demo.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    List<Modelo> findAllByOrderByMarcaNomeAscNomeAsc();

    List<Modelo> findByMarcaIdOrderByNomeAsc(Long marcaId);

    Optional<Modelo> findByMarcaIdAndNomeIgnoreCase(Long marcaId, String nome);

    boolean existsByMarcaId(Long marcaId);

    boolean existsByTipoId(Long tipoId);
}
