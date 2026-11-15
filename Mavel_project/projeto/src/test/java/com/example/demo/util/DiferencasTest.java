package com.example.demo.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiferencasTest {

    private static Map<String, Object> valores(Object preco, Object status) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("preço", preco);
        m.put("status", status);
        return m;
    }

    @Test
    void mostraSoOQueMudou() {
        String texto = Diferencas.comparar(
                valores(new BigDecimal("50000.00"), "Disponível"),
                valores(new BigDecimal("48000.00"), "Disponível"));
        assertEquals("preço: 50000.00 → 48000.00", texto);
    }

    @Test
    void mesmoValorComEscalaDiferenteNaoEhMudanca() {
        assertEquals("", Diferencas.comparar(
                valores(new BigDecimal("50000"), "Vendido"),
                valores(new BigDecimal("50000.00"), "Vendido")));
    }

    @Test
    void valorNuloApareceComoVazio() {
        assertEquals("status: (vazio) → Vendido",
                Diferencas.comparar(valores(BigDecimal.ONE, null), valores(BigDecimal.ONE, "Vendido")));
    }

    @Test
    void listarMostraTodosOsCamposNaOrdem() {
        assertEquals("preço: 1.50; status: Reservado",
                Diferencas.listar(valores(new BigDecimal("1.5"), "Reservado")));
    }
}
