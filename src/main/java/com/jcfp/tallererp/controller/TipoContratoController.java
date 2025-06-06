package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.TipoContrato;
import com.jcfp.tallererp.service.TipoContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-contrato")
public class TipoContratoController {
    private final TipoContratoService service;

    @Autowired
    public TipoContratoController(TipoContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoContrato>> getAllTipoContratos() {
        List<TipoContrato> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }
}
