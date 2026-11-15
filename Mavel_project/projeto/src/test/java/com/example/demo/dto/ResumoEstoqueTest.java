package com.example.demo.dto;

import com.example.demo.Exemplos;
import com.example.demo.model.StatusVeiculo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumoEstoqueTest {

    @Test
    void contaPorStatusESomaSoOQueAindaEstaAVenda() {
        ResumoEstoque r = ResumoEstoque.de(List.of(
                Exemplos.veiculo("Fiat", "Argo", "Hatch", 2022, "Prata", "70000", StatusVeiculo.DISPONIVEL),
                Exemplos.veiculo("Fiat", "Argo", "Hatch", 2023, "Preto", "80000", StatusVeiculo.RESERVADO),
                Exemplos.veiculo("Fiat", "Strada", "Picape", 2023, "Branco", "110000", StatusVeiculo.VENDIDO),
                Exemplos.veiculo("Toyota", "Hilux", "Picape", 2019, "Branco", "189000", StatusVeiculo.DESCONTINUADO),
                Exemplos.veiculo("Honda", "CG 160", "Moto", 2024, "Vermelho", "17900.50", StatusVeiculo.DISPONIVEL)));
        assertEquals(5, r.total());
        assertEquals(2, r.disponiveis());
        assertEquals(1, r.reservados());
        assertEquals(1, r.vendidos());
        assertEquals(1, r.descontinuados());
        assertEquals(new BigDecimal("167900.50"), r.valorEmEstoque());
    }

    @Test
    void estoqueVazio() {
        ResumoEstoque r = ResumoEstoque.de(List.of());
        assertEquals(0, r.total());
        assertEquals(new BigDecimal("0.00"), r.valorEmEstoque());
    }
}
