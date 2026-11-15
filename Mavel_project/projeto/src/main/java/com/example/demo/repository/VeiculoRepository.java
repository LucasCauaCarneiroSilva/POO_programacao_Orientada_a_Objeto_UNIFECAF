package com.example.demo.repository;

import com.example.demo.model.StatusVeiculo;
import com.example.demo.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    // Filtro feito no banco: parâmetro nulo = sem filtro naquele campo.
    @Query("""
            select v from Veiculo v
            where (:marcaId is null or v.modelo.marca.id = :marcaId)
              and (:modeloId is null or v.modelo.id = :modeloId)
              and (:tipoId is null or v.modelo.tipo.id = :tipoId)
              and (:status is null or v.status = :status)
              and (:anoMin is null or v.ano >= :anoMin)
              and (:anoMax is null or v.ano <= :anoMax)
              and (:precoMin is null or v.preco >= :precoMin)
              and (:precoMax is null or v.preco <= :precoMax)
            order by v.modelo.marca.nome, v.modelo.nome, v.ano desc, v.id
            """)
    List<Veiculo> filtrar(@Param("marcaId") Long marcaId,
                          @Param("modeloId") Long modeloId,
                          @Param("tipoId") Long tipoId,
                          @Param("status") StatusVeiculo status,
                          @Param("anoMin") Integer anoMin,
                          @Param("anoMax") Integer anoMax,
                          @Param("precoMin") BigDecimal precoMin,
                          @Param("precoMax") BigDecimal precoMax);

    boolean existsByModeloId(Long modeloId);
}
