package com.example.demo.dto;

import com.example.demo.Exemplos;
import com.example.demo.excecao.RegraNegocioException;
import com.example.demo.model.StatusVeiculo;
import com.example.demo.model.Veiculo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VeiculoFiltroTest {

    private static VeiculoFiltro texto(String cor, String texto) {
        return new VeiculoFiltro(null, null, null, null, null, null, null, null, cor, texto);
    }

    private final Veiculo corolla = Exemplos.veiculo("Toyota", "Corolla", "Sedã", 2022, "Prata Sirius",
            "142500", StatusVeiculo.DISPONIVEL);

    @Test
    void faixaInvertidaDeAnoOuPrecoEhRecusada() {
        assertThrows(RegraNegocioException.class,
                () -> new VeiculoFiltro(null, null, null, null, 2024, 2020, null, null, null, null).validar());
        assertThrows(RegraNegocioException.class,
                () -> new VeiculoFiltro(null, null, null, null, null, null,
                        new BigDecimal("90000"), new BigDecimal("50000"), null, null).validar());
        assertDoesNotThrow(() -> new VeiculoFiltro(null, null, null, null, 2020, 2020, null, null, null, null).validar());
        assertDoesNotThrow(() -> VeiculoFiltro.vazio().validar());
    }

    @Test
    void corAceitaParteDoNomeSemAcento() {
        assertTrue(texto("prata", null).aceitaTexto(corolla));
        assertFalse(texto("branco", null).aceitaTexto(corolla));
    }

    @Test
    void textoLivreProcuraEmMarcaModeloTipoAnoEStatus() {
        assertTrue(texto(null, "toyota seda 2022").aceitaTexto(corolla));
        assertTrue(texto(null, "disponivel").aceitaTexto(corolla));
        assertFalse(texto(null, "corolla 2023").aceitaTexto(corolla));
        assertTrue(VeiculoFiltro.vazio().aceitaTexto(corolla));
    }
}
