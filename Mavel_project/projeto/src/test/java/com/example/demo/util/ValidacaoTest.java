package com.example.demo.util;

import com.example.demo.excecao.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacaoTest {

    @Test
    void textoTiraEspacosDasPontasEJuntaRepetidos() {
        assertEquals("T-Cross Highline", Validacao.texto("  T-Cross    Highline ", "Modelo", 60));
    }

    @Test
    void textoVazioOuEmBrancoEhRecusado() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> Validacao.texto("   ", "Cor", 30));
        assertEquals("Campo obrigatório: Cor.", e.getMessage());
        assertThrows(RegraNegocioException.class, () -> Validacao.texto(null, "Cor", 30));
    }

    @Test
    void textoMaiorQueOLimiteEhRecusado() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class,
                () -> Validacao.texto("x".repeat(31), "Cor", 30));
        assertTrue(e.getMessage().contains("no máximo 30"));
    }

    @Test
    void anoAceitaDe1900AteOAnoQueVem() {
        int proximo = Year.now().getValue() + 1;
        assertEquals(1900, Validacao.ano(1900));
        assertEquals(proximo, Validacao.ano(proximo));
        assertThrows(RegraNegocioException.class, () -> Validacao.ano(1899));
        assertThrows(RegraNegocioException.class, () -> Validacao.ano(proximo + 1));
        assertThrows(RegraNegocioException.class, () -> Validacao.ano(null));
    }

    @Test
    void precoPrecisaSerMaiorQueZeroEFicaComDuasCasas() {
        assertEquals(new BigDecimal("50000.00"), Validacao.preco(new BigDecimal("50000")));
        assertEquals(new BigDecimal("10.13"), Validacao.preco(new BigDecimal("10.125")));
        assertThrows(RegraNegocioException.class, () -> Validacao.preco(BigDecimal.ZERO));
        assertThrows(RegraNegocioException.class, () -> Validacao.preco(new BigDecimal("-1")));
        assertThrows(RegraNegocioException.class, () -> Validacao.preco(new BigDecimal("99999999999")));
    }

    @Test
    void quilometragemNaoPodeSerNegativa() {
        assertEquals(0, Validacao.quilometragem(0));
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> Validacao.quilometragem(-5));
        assertEquals("Quilometragem não pode ser negativa.", e.getMessage());
        assertThrows(RegraNegocioException.class, () -> Validacao.quilometragem(Validacao.KM_MAXIMA + 1));
    }
}
