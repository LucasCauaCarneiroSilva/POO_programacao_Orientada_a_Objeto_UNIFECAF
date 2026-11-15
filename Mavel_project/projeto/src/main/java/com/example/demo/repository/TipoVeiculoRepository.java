package com.example.demo.repository;

import com.example.demo.model.TipoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoVeiculoRepository extends JpaRepository<TipoVeiculo, Long> {

    List<TipoVeiculo> findAllByOrderByNomeAsc();

    Optional<TipoVeiculo> findByNomeIgnoreCase(String nome);
}
