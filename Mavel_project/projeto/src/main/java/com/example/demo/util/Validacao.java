package com.example.demo.util;

import com.example.demo.excecao.RegraNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

// Regras de validação usadas pelos setters (encapsulamento).
public final class Validacao {

    public static final int ANO_MINIMO = 1900;
    public static final int KM_MAXIMA = 3_000_000;
    public static final BigDecimal PRECO_MAXIMO = new BigDecimal("9999999999.99");

    private Validacao() {
    }

    // Texto obrigatório, sem espaços sobrando e com tamanho máximo.
    public static String texto(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("Campo obrigatório: " + campo + ".");
        }
        String limpo = valor.trim().replaceAll("\\s+", " ");
        if (limpo.length() > tamanhoMaximo) {
            throw new RegraNegocioException(campo + " deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }

    public static <T> T obrigatorio(T valor, String campo) {
        if (valor == null) {
            throw new RegraNegocioException("Campo obrigatório: " + campo + ".");
        }
        return valor;
    }

    // Ano entre 1900 e o ano que vem.
    public static int ano(Integer ano) {
        obrigatorio(ano, "Ano");
        int maximo = anoMaximo();
        if (ano < ANO_MINIMO || ano > maximo) {
            throw new RegraNegocioException("Ano deve estar entre " + ANO_MINIMO + " e " + maximo + ".");
        }
        return ano;
    }

    public static int anoMaximo() {
        return Year.now().getValue() + 1;
    }

    // Preço maior que zero, guardado com 2 casas decimais.
    public static BigDecimal preco(BigDecimal preco) {
        obrigatorio(preco, "Preço");
        if (preco.signum() <= 0) {
            throw new RegraNegocioException("Preço deve ser maior que zero.");
        }
        if (preco.compareTo(PRECO_MAXIMO) > 0) {
            throw new RegraNegocioException("Preço acima do limite permitido.");
        }
        return preco.setScale(2, RoundingMode.HALF_UP);
    }

    public static int quilometragem(Integer km) {
        obrigatorio(km, "Quilometragem");
        if (km < 0) {
            throw new RegraNegocioException("Quilometragem não pode ser negativa.");
        }
        if (km > KM_MAXIMA) {
            throw new RegraNegocioException("Quilometragem acima do limite (" + KM_MAXIMA + " km).");
        }
        return km;
    }
}
