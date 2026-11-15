package com.example.demo.service;

import com.example.demo.dto.Formularios.VeiculoAjuste;
import com.example.demo.dto.Formularios.VeiculoForm;
import com.example.demo.dto.ResumoEstoque;
import com.example.demo.dto.VeiculoFiltro;
import com.example.demo.excecao.NaoEncontradoException;
import com.example.demo.excecao.RegraNegocioException;
import com.example.demo.model.Modelo;
import com.example.demo.model.Veiculo;
import com.example.demo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// Regras do estoque: cadastrar, consultar/filtrar, atualizar e remover veículos.
@Service
@Transactional
public class VeiculoService {

    private final VeiculoRepository veiculos;
    private final CatalogoService catalogo;
    private final HistoricoService historico;

    public VeiculoService(VeiculoRepository veiculos, CatalogoService catalogo, HistoricoService historico) {
        this.veiculos = veiculos;
        this.catalogo = catalogo;
        this.historico = historico;
    }

    @Transactional(readOnly = true)
    public List<Veiculo> filtrar(VeiculoFiltro filtro) {
        VeiculoFiltro f = filtro == null ? VeiculoFiltro.vazio() : filtro;
        f.validar();
        return veiculos.filtrar(f.marcaId(), f.modeloId(), f.tipoId(), f.status(),
                        f.anoMin(), f.anoMax(), f.precoMin(), f.precoMax())
                .stream()
                .filter(f::aceitaTexto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Veiculo buscar(Long id) {
        Objects.requireNonNull(id);
        return veiculos.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Veículo não encontrado (id " + id + ")."));
    }

    public Veiculo cadastrar(VeiculoForm form, String usuario) {
        Modelo modelo = catalogo.buscarModelo(CatalogoService.exigirId(form.modeloId(), "Modelo"));
        Veiculo veiculo = new Veiculo(modelo, form.ano(), form.cor(), form.preco(),
                form.quilometragem(), form.status());
        veiculo = veiculos.save(veiculo);
        historico.inclusao(veiculo, usuario);
        return veiculo;
    }

    // Edição completa (todos os campos do formulário).
    public Veiculo atualizar(Long id, VeiculoForm form, String usuario) {
        Veiculo veiculo = buscar(id);
        Modelo modelo = catalogo.buscarModelo(CatalogoService.exigirId(form.modeloId(), "Modelo"));
        Map<String, Object> antes = veiculo.snapshot();
        veiculo.setModelo(modelo);
        veiculo.setAno(form.ano());
        veiculo.setCor(form.cor());
        veiculo.setPreco(form.preco());
        veiculo.setQuilometragem(form.quilometragem());
        veiculo.setStatus(form.status());
        historico.alteracao(veiculo, antes, usuario);
        return veiculo;
    }

    // Atualização rápida de preço, quilometragem e/ou status.
    public Veiculo ajustar(Long id, VeiculoAjuste ajuste, String usuario) {
        if (ajuste == null || ajuste.vazio()) {
            throw new RegraNegocioException("Nada para atualizar: informe preço, quilometragem ou status.");
        }
        Veiculo veiculo = buscar(id);
        Map<String, Object> antes = veiculo.snapshot();
        if (ajuste.preco() != null) {
            veiculo.setPreco(ajuste.preco());
        }
        if (ajuste.quilometragem() != null) {
            veiculo.setQuilometragem(ajuste.quilometragem());
        }
        if (ajuste.status() != null) {
            veiculo.setStatus(ajuste.status());
        }
        historico.alteracao(veiculo, antes, usuario);
        return veiculo;
    }

    // Remove do estoque (vendido, descontinuado...). O motivo fica no histórico.
    public void remover(Long id, String motivo, String usuario) {
        Veiculo veiculo = buscar(id);
        veiculos.delete(veiculo);
        historico.exclusao(veiculo, usuario, motivo);
    }

    @Transactional(readOnly = true)
    public ResumoEstoque resumo() {
        return ResumoEstoque.de(veiculos.findAll());
    }
}
