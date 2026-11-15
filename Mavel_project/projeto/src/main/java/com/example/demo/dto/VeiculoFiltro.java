package com.example.demo.dto;

import com.example.demo.excecao.RegraNegocioException;
import com.example.demo.model.StatusVeiculo;
import com.example.demo.model.Veiculo;
import com.example.demo.util.Texto;

import java.math.BigDecimal;

// Filtros da consulta (todos opcionais). Cor e texto livre são conferidos sem acento.
public record VeiculoFiltro(Long marcaId, Long modeloId, Long tipoId, StatusVeiculo status,
                            Integer anoMin, Integer anoMax, BigDecimal precoMin, BigDecimal precoMax,
                            String cor, String texto) {

    public static VeiculoFiltro vazio() {
        return new VeiculoFiltro(null, null, null, null, null, null, null, null, null, null);
    }

    public void validar() {
        if (anoMin != null && anoMax != null && anoMin > anoMax) {
            throw new RegraNegocioException("Ano inicial maior que o ano final.");
        }
        if (precoMin != null && precoMax != null && precoMin.compareTo(precoMax) > 0) {
            throw new RegraNegocioException("Preço mínimo maior que o preço máximo.");
        }
    }

    // Cor e texto livre (todas as palavras precisam aparecer).
    public boolean aceitaTexto(Veiculo veiculo) {
        return Texto.contemTodas(veiculo.getCor(), cor)
                && Texto.contemTodas(veiculo.textoBusca(), texto);
    }
}
