package com.example.demo.dto;

import com.example.demo.model.Veiculo;

import java.math.BigDecimal;
import java.util.Collection;

// Números do topo da tela: quantidade por status e valor do que ainda está à venda.
public record ResumoEstoque(long total, long disponiveis, long reservados, long vendidos,
                            long descontinuados, BigDecimal valorEmEstoque) {

    public static ResumoEstoque de(Collection<Veiculo> veiculos) {
        long disponiveis = 0, reservados = 0, vendidos = 0, descontinuados = 0;
        BigDecimal valor = BigDecimal.ZERO.setScale(2);
        for (Veiculo v : veiculos) {
            switch (v.getStatus()) {
                case DISPONIVEL -> disponiveis++;
                case RESERVADO -> reservados++;
                case VENDIDO -> vendidos++;
                case DESCONTINUADO -> descontinuados++;
            }
            if (v.getStatus().isEmEstoque()) {
                valor = valor.add(v.getPreco());
            }
        }
        return new ResumoEstoque(veiculos.size(), disponiveis, reservados, vendidos, descontinuados, valor);
    }
}
