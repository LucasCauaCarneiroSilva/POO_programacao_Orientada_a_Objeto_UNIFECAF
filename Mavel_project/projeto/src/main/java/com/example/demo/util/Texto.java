package com.example.demo.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

// Busca sem acento e sem diferenciar maiúsculas: "seda" acha "Sedã".
public final class Texto {

    public static final String USUARIO_PADRAO = "sistema";

    private Texto() {
    }

    // Minúsculas e sem acentos.
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).trim();
    }

    // Só letras e números (ignora hífen, ponto, espaço...).
    public static String compacto(String texto) {
        return normalizar(texto).replaceAll("[^a-z0-9]", "");
    }

    // true se todas as palavras da busca aparecem no alvo.
    public static boolean contemTodas(String alvo, String busca) {
        if (busca == null || busca.isBlank()) {
            return true;
        }
        String alvoNormal = normalizar(alvo);
        String alvoCompacto = compacto(alvo);
        for (String palavra : normalizar(busca).split("\\s+")) {
            if (palavra.isEmpty()) {
                continue;
            }
            String palavraCompacta = compacto(palavra);
            boolean achou = alvoNormal.contains(palavra)
                    || (!palavraCompacta.isEmpty() && alvoCompacto.contains(palavraCompacta));
            if (!achou) {
                return false;
            }
        }
        return true;
    }

    // Corta o texto no tamanho máximo da coluna.
    public static String limitar(String texto, int maximo) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
    }

    // Nome do usuário enviado pela tela no cabeçalho X-Usuario.
    public static String usuario(String cabecalho) {
        if (cabecalho == null || cabecalho.isBlank()) {
            return USUARIO_PADRAO;
        }
        String nome;
        try {
            nome = URLDecoder.decode(cabecalho, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            nome = cabecalho;
        }
        nome = nome.trim();
        return nome.isEmpty() ? USUARIO_PADRAO : limitar(nome, 60);
    }
}
