package com.example.demo.model;

import java.util.Map;

// Interface: contrato de tudo que entra no histórico de alterações.
public interface Auditavel {

    Long getId();

    String nomeEntidade();

    String descricao();

    Map<String, Object> snapshot();
}
