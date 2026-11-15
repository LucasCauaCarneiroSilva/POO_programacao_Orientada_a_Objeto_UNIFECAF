package com.example.demo.controller;

import com.example.demo.dto.Formularios.VeiculoAjuste;
import com.example.demo.dto.Formularios.VeiculoForm;
import com.example.demo.dto.ResumoEstoque;
import com.example.demo.dto.VeiculoFiltro;
import com.example.demo.model.StatusVeiculo;
import com.example.demo.model.Veiculo;
import com.example.demo.service.VeiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

// API REST dos veículos: listar/filtrar, buscar, cadastrar, editar, ajustar e remover.
@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private static final String USUARIO = "X-Usuario";

    private final VeiculoService servico;

    public VeiculoController(VeiculoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<Veiculo> listar(VeiculoFiltro filtro) {
        return servico.filtrar(filtro);
    }

    @GetMapping("/{id}")
    public Veiculo buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }

    @GetMapping("/resumo")
    public ResumoEstoque resumo() {
        return servico.resumo();
    }

    // Status possíveis com o texto para a tela.
    @GetMapping("/status")
    public List<OpcaoStatus> status() {
        return Arrays.stream(StatusVeiculo.values())
                .map(s -> new OpcaoStatus(s.name(), s.getRotulo(), s.isEmEstoque()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Veiculo cadastrar(@RequestBody VeiculoForm form,
                             @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.cadastrar(form, usuario);
    }

    @PutMapping("/{id}")
    public Veiculo atualizar(@PathVariable Long id, @RequestBody VeiculoForm form,
                             @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.atualizar(id, form, usuario);
    }

    @PatchMapping("/{id}")
    public Veiculo ajustar(@PathVariable Long id, @RequestBody VeiculoAjuste ajuste,
                           @RequestHeader(value = USUARIO, required = false) String usuario) {
        return servico.ajustar(id, ajuste, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, @RequestParam(required = false) String motivo,
                        @RequestHeader(value = USUARIO, required = false) String usuario) {
        servico.remover(id, motivo, usuario);
    }

    public record OpcaoStatus(String valor, String rotulo, boolean emEstoque) {
    }
}
