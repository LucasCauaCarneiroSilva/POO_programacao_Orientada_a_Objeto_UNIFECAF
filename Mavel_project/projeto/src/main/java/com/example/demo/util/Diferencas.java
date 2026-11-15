package com.example.demo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

// Monta o texto "campo: antes → depois" gravado no histórico de alterações.
public final class Diferencas {

    private Diferencas() {
    }

    // Só os campos que mudaram. Texto vazio quando nada mudou.
    public static String comparar(Map<String, Object> antes, Map<String, Object> depois) {
        Set<String> campos = new LinkedHashSet<>(antes.keySet());
        campos.addAll(depois.keySet());
        StringJoiner texto = new StringJoiner("; ");
        for (String campo : campos) {
            Object a = antes.get(campo);
            Object d = depois.get(campo);
            if (!iguais(a, d)) {
                texto.add(campo + ": " + formatar(a) + " → " + formatar(d));
            }
        }
        return texto.toString();
    }

    // Todos os campos, usado na inclusão e na exclusão.
    public static String listar(Map<String, Object> valores) {
        StringJoiner texto = new StringJoiner("; ");
        valores.forEach((campo, valor) -> texto.add(campo + ": " + formatar(valor)));
        return texto.toString();
    }

    static boolean iguais(Object a, Object b) {
        if (a instanceof BigDecimal x && b instanceof BigDecimal y) {
            return x.compareTo(y) == 0;
        }
        return Objects.equals(a, b);
    }

    static String formatar(Object valor) {
        if (valor == null) {
            return "(vazio)";
        }
        if (valor instanceof BigDecimal numero) {
            return numero.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return valor.toString();
    }
}
