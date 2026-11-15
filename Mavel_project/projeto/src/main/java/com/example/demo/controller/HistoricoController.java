package com.example.demo.controller;

import com.example.demo.model.HistoricoAlteracao;
import com.example.demo.service.HistoricoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET /api/historico?entidade=Veículo&registroId=3
@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final HistoricoService servico;

    public HistoricoController(HistoricoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<HistoricoAlteracao> listar(@RequestParam(required = false) String entidade,
                                           @RequestParam(required = false) Long registroId) {
        return servico.listar(entidade, registroId);
    }
}
