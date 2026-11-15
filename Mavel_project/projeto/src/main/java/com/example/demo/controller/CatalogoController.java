package com.example.demo.controller;

import com.example.demo.dto.Formularios.ModeloForm;
import com.example.demo.dto.Formularios.NomeForm;
import com.example.demo.model.Marca;
import com.example.demo.model.Modelo;
import com.example.demo.model.TipoVeiculo;
import com.example.demo.service.CatalogoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API de tipos, marcas e modelos: GET lista, POST cria, PUT altera, DELETE exclui.
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private static final String USUARIO = "X-Usuario";

    private final CatalogoService servico;

    public CatalogoController(CatalogoService servico) {
        this.servico = servico;
    }

    // Tipos
    @GetMapping("/tipos")
    public List<TipoVeiculo> tipos() {
        return servico.listarTipos();
    }

    @PostMapping("/tipos")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoVeiculo criarTipo(@RequestBody NomeForm form,
                                 @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.criarTipo(form.nome(), usuario);
    }

    @PutMapping("/tipos/{id}")
    public TipoVeiculo renomearTipo(@PathVariable Long id, @RequestBody NomeForm form,
                                    @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.renomearTipo(id, form.nome(), usuario);
    }

    @DeleteMapping("/tipos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirTipo(@PathVariable Long id,
                            @RequestHeader(value = USUARIO, required = false) String usuario) {
        servico.excluirTipo(id, usuario);
    }

    // Marcas
    @GetMapping("/marcas")
    public List<Marca> marcas() {
        return servico.listarMarcas();
    }

    @PostMapping("/marcas")
    @ResponseStatus(HttpStatus.CREATED)
    public Marca criarMarca(@RequestBody NomeForm form,
                            @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.criarMarca(form.nome(), usuario);
    }

    @PutMapping("/marcas/{id}")
    public Marca renomearMarca(@PathVariable Long id, @RequestBody NomeForm form,
                               @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.renomearMarca(id, form.nome(), usuario);
    }

    @DeleteMapping("/marcas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirMarca(@PathVariable Long id,
                             @RequestHeader(value = USUARIO, required = false) String usuario) {
        servico.excluirMarca(id, usuario);
    }

    // Modelos
    @GetMapping("/modelos")
    public List<Modelo> modelos(@RequestParam(required = false) Long marcaId) {
        return servico.listarModelos(marcaId);
    }

    @PostMapping("/modelos")
    @ResponseStatus(HttpStatus.CREATED)
    public Modelo criarModelo(@RequestBody ModeloForm form,
                              @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.criarModelo(form, usuario);
    }

    @PutMapping("/modelos/{id}")
    public Modelo atualizarModelo(@PathVariable Long id, @RequestBody ModeloForm form,
                                  @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.atualizarModelo(id, form, usuario);
    }

    @DeleteMapping("/modelos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirModelo(@PathVariable Long id,
                              @RequestHeader(value = USUARIO, required = false) String usuario) {
        servico.excluirModelo(id, usuario);
    }
}
