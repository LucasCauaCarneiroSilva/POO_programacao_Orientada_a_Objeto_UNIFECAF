package com.example.demo.service;

import com.example.demo.dto.Formularios.ModeloForm;
import com.example.demo.excecao.NaoEncontradoException;
import com.example.demo.excecao.RegraNegocioException;
import com.example.demo.model.Marca;
import com.example.demo.model.Modelo;
import com.example.demo.model.TipoVeiculo;
import com.example.demo.repository.MarcaRepository;
import com.example.demo.repository.ModeloRepository;
import com.example.demo.repository.TipoVeiculoRepository;
import com.example.demo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// Regras de tipos, marcas e modelos: nomes não se repetem e não se exclui o que está em uso.
@Service
@Transactional
public class CatalogoService {

    private final TipoVeiculoRepository tipos;
    private final MarcaRepository marcas;
    private final ModeloRepository modelos;
    private final VeiculoRepository veiculos;
    private final HistoricoService historico;

    public CatalogoService(TipoVeiculoRepository tipos, MarcaRepository marcas, ModeloRepository modelos,
                           VeiculoRepository veiculos, HistoricoService historico) {
        this.tipos = tipos;
        this.marcas = marcas;
        this.modelos = modelos;
        this.veiculos = veiculos;
        this.historico = historico;
    }

    // Tipos

    @Transactional(readOnly = true)
    public List<TipoVeiculo> listarTipos() {
        return tipos.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public TipoVeiculo buscarTipo(Long id) {
        Objects.requireNonNull(id);
        return tipos.findById(id).orElseThrow(() -> new NaoEncontradoException("Tipo não encontrado (id " + id + ")."));
    }

    public TipoVeiculo criarTipo(String nome, String usuario) {
        String limpo = TipoVeiculo.validarNome(nome);
        exigirTipoLivre(limpo, null);
        TipoVeiculo tipo = tipos.save(new TipoVeiculo(limpo));
        historico.inclusao(tipo, usuario);
        return tipo;
    }

    public TipoVeiculo renomearTipo(Long id, String nome, String usuario) {
        TipoVeiculo tipo = buscarTipo(id);
        String limpo = TipoVeiculo.validarNome(nome);
        exigirTipoLivre(limpo, id);
        Map<String, Object> antes = tipo.snapshot();
        tipo.setNome(limpo);
        historico.alteracao(tipo, antes, usuario);
        return tipo;
    }

    public void excluirTipo(Long id, String usuario) {
        TipoVeiculo tipo = buscarTipo(id);
        if (modelos.existsByTipoId(id)) {
            throw new RegraNegocioException("O tipo \"" + tipo.getNome()
                    + "\" está em uso por modelos. Altere ou exclua esses modelos antes.");
        }
        tipos.delete(tipo);
        historico.exclusao(tipo, usuario, null);
    }

    private void exigirTipoLivre(String nome, Long idAtual) {
        tipos.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe o tipo \"" + existente.getNome() + "\".");
                });
    }

    // Marcas

    @Transactional(readOnly = true)
    public List<Marca> listarMarcas() {
        return marcas.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Marca buscarMarca(Long id) {
        Objects.requireNonNull(id);
        return marcas.findById(id).orElseThrow(() -> new NaoEncontradoException("Marca não encontrada (id " + id + ")."));
    }

    public Marca criarMarca(String nome, String usuario) {
        String limpo = Marca.validarNome(nome);
        exigirMarcaLivre(limpo, null);
        Marca marca = marcas.save(new Marca(limpo));
        historico.inclusao(marca, usuario);
        return marca;
    }

    public Marca renomearMarca(Long id, String nome, String usuario) {
        Marca marca = buscarMarca(id);
        String limpo = Marca.validarNome(nome);
        exigirMarcaLivre(limpo, id);
        Map<String, Object> antes = marca.snapshot();
        marca.setNome(limpo);
        historico.alteracao(marca, antes, usuario);
        return marca;
    }

    public void excluirMarca(Long id, String usuario) {
        Marca marca = buscarMarca(id);
        if (modelos.existsByMarcaId(id)) {
            throw new RegraNegocioException("A marca \"" + marca.getNome()
                    + "\" tem modelos cadastrados. Exclua os modelos antes.");
        }
        marcas.delete(marca);
        historico.exclusao(marca, usuario, null);
    }

    private void exigirMarcaLivre(String nome, Long idAtual) {
        marcas.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe a marca \"" + existente.getNome() + "\".");
                });
    }

    // Modelos

    @Transactional(readOnly = true)
    public List<Modelo> listarModelos(Long marcaId) {
        return marcaId == null ? modelos.findAllByOrderByMarcaNomeAscNomeAsc()
                : modelos.findByMarcaIdOrderByNomeAsc(marcaId);
    }

    @Transactional(readOnly = true)
    public Modelo buscarModelo(Long id) {
        Objects.requireNonNull(id);
        return modelos.findById(id).orElseThrow(() -> new NaoEncontradoException("Modelo não encontrado (id " + id + ")."));
    }

    public Modelo criarModelo(ModeloForm form, String usuario) {
        String nome = Modelo.validarNome(form.nome());
        Marca marca = buscarMarca(exigirId(form.marcaId(), "Marca"));
        TipoVeiculo tipo = buscarTipo(exigirId(form.tipoId(), "Tipo"));
        exigirModeloLivre(marca, nome, null);
        Modelo modelo = modelos.save(new Modelo(nome, marca, tipo));
        historico.inclusao(modelo, usuario);
        return modelo;
    }

    public Modelo atualizarModelo(Long id, ModeloForm form, String usuario) {
        Modelo modelo = buscarModelo(id);
        String nome = Modelo.validarNome(form.nome());
        Marca marca = buscarMarca(exigirId(form.marcaId(), "Marca"));
        TipoVeiculo tipo = buscarTipo(exigirId(form.tipoId(), "Tipo"));
        exigirModeloLivre(marca, nome, id);
        Map<String, Object> antes = modelo.snapshot();
        modelo.setNome(nome);
        modelo.setMarca(marca);
        modelo.setTipo(tipo);
        historico.alteracao(modelo, antes, usuario);
        return modelo;
    }

    public void excluirModelo(Long id, String usuario) {
        Modelo modelo = buscarModelo(id);
        if (veiculos.existsByModeloId(id)) {
            throw new RegraNegocioException("O modelo \"" + modelo.descricao()
                    + "\" tem veículos no estoque. Remova os veículos antes.");
        }
        modelos.delete(modelo);
        historico.exclusao(modelo, usuario, null);
    }

    private void exigirModeloLivre(Marca marca, String nome, Long idAtual) {
        modelos.findByMarcaIdAndNomeIgnoreCase(marca.getId(), nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe o modelo \"" + existente.descricao() + "\".");
                });
    }

    static Long exigirId(Long id, String campo) {
        if (id == null) {
            throw new RegraNegocioException("Campo obrigatório: " + campo + ".");
        }
        return id;
    }
}
