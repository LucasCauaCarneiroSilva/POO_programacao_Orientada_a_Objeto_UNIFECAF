package com.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextoTest {

    @Test
    void normalizarTiraAcentoEMaiusculas() {
        assertEquals("seda prata", Texto.normalizar("  Sedã PRATA "));
        assertEquals("", Texto.normalizar(null));
    }

    @Test
    void buscaIgnoraAcentoHifenEOrdemDasPalavras() {
        String alvo = "Volkswagen T-Cross SUV Cinza 2024 Reservado";
        assertTrue(Texto.contemTodas(alvo, "tcross"));
        assertTrue(Texto.contemTodas(alvo, "2024 t-cross"));
        assertTrue(Texto.contemTodas("Toyota Corolla Sedã Prata", "seda prata"));
    }

    @Test
    void buscaExigeTodasAsPalavras() {
        assertFalse(Texto.contemTodas("Fiat Argo Hatch Vermelho", "argo onix"));
    }

    @Test
    void buscaVaziaAceitaTudo() {
        assertTrue(Texto.contemTodas("qualquer coisa", null));
        assertTrue(Texto.contemTodas("qualquer coisa", "   "));
    }

    @Test
    void usuarioDecodificaOCabecalhoEUsaPadraoQuandoVazio() {
        assertEquals("João", Texto.usuario("Jo%C3%A3o"));
        assertEquals("Ana Paula", Texto.usuario("Ana%20Paula"));
        assertEquals(Texto.USUARIO_PADRAO, Texto.usuario(null));
        assertEquals(Texto.USUARIO_PADRAO, Texto.usuario("   "));
        assertEquals("100%", Texto.usuario("100%"));
    }

    @Test
    void limitarCortaComReticencias() {
        assertEquals("abc", Texto.limitar("abc", 5));
        assertEquals("abcd…", Texto.limitar("abcdefgh", 5));
    }
}
