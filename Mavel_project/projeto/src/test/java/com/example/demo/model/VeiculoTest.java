package com.example.demo.model;

import com.example.demo.Exemplos;
import com.example.demo.excecao.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VeiculoTest {

    @Test
    void veiculoNovoSemStatusFicaDisponivel() {
        Veiculo v = new Veiculo(Exemplos.modelo("Fiat", "Argo", "Hatch"), 2022, "Prata",
                new BigDecimal("71500"), 31000, null);
        assertEquals(StatusVeiculo.DISPONIVEL, v.getStatus());
        assertEquals(new BigDecimal("71500.00"), v.getPreco());
    }

    @Test
    void descricaoJuntaMarcaModeloAnoECor() {
        Veiculo v = Exemplos.veiculo("Fiat", "Argo", "Hatch", 2022, "Prata", "71500", StatusVeiculo.RESERVADO);
        assertEquals("Fiat Argo 2022 Prata", v.descricao());
        assertEquals("Veículo", v.nomeEntidade());
    }

    @Test
    void setterNaoAceitaValorInvalido() {
        Veiculo v = Exemplos.veiculo("Fiat", "Argo", "Hatch", 2022, "Prata", "71500", StatusVeiculo.DISPONIVEL);
        assertThrows(RegraNegocioException.class, () -> v.setPreco(new BigDecimal("-10")));
        assertThrows(RegraNegocioException.class, () -> v.setAno(1800));
        assertThrows(RegraNegocioException.class, () -> v.setQuilometragem(-1));
        assertThrows(RegraNegocioException.class, () -> v.setStatus(null));
        assertThrows(RegraNegocioException.class, () -> v.setModelo(null));
        // o valor anterior continua
        assertEquals(new BigDecimal("71500.00"), v.getPreco());
        assertEquals(2022, v.getAno());
    }

    @Test
    void snapshotTemOsCamposDoHistoricoNaOrdem() {
        Veiculo v = Exemplos.veiculo("Toyota", "Corolla", "Sedã", 2022, "Prata", "142500", StatusVeiculo.VENDIDO);
        assertEquals(List.of("modelo", "ano", "cor", "preço", "quilometragem", "status"),
                List.copyOf(v.snapshot().keySet()));
        assertEquals("Vendido", v.snapshot().get("status"));
        assertEquals("Toyota Corolla", v.snapshot().get("modelo"));
    }

    @Test
    void modeloExigeMarcaETipo() {
        assertThrows(RegraNegocioException.class, () -> new Modelo("Argo", null, new TipoVeiculo("Hatch")));
        assertThrows(RegraNegocioException.class, () -> new Modelo("Argo", new Marca("Fiat"), null));
        assertThrows(RegraNegocioException.class, () -> new Marca(" "));
    }

    @Test
    void polimorfismoCadaSubclasseSeDescreve() {
        List<EntidadeBase> entidades = List.of(
                new TipoVeiculo("SUV"), new Marca("Honda"), Exemplos.modelo("Honda", "CG 160", "Moto"),
                Exemplos.veiculo("Honda", "CG 160", "Moto", 2024, "Vermelho", "17900", StatusVeiculo.DISPONIVEL));
        List<String> nomes = entidades.stream().map(EntidadeBase::nomeEntidade).toList();
        assertEquals(List.of("Tipo", "Marca", "Modelo", "Veículo"), nomes);
        assertTrue(entidades.stream().allMatch(e -> !e.snapshot().isEmpty()));
    }

    @Test
    void sobrecargaDeConstrutorSemStatusEntraDisponivel() {
        Veiculo v = new Veiculo(Exemplos.modelo("Fiat", "Strada", "Picape"), 2023, "Branco",
                new BigDecimal("109900"), 22800);
        assertEquals(StatusVeiculo.DISPONIVEL, v.getStatus());
    }

    @Test
    void toStringSobrescritoUsaADescricao() {
        assertEquals("Marca: Toyota", new Marca("Toyota").toString());
        assertEquals("Modelo: Toyota Corolla", Exemplos.modelo("Toyota", "Corolla", "Sedã").toString());
    }

    @Test
    void interfaceAuditavelAceitaQualquerEntidade() {
        Auditavel item = new TipoVeiculo("Moto");
        assertEquals("Tipo", item.nomeEntidade());
        assertEquals("Moto", item.descricao());
    }
}
