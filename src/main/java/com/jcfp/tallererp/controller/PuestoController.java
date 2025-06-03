package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Puesto;
import com.jcfp.tallererp.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/puestos")
public class PuestoController{
    private final PuestoService service;

    @Autowired
    public PuestoController(PuestoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Puesto>> getAllPuestos() {
        List<Puesto> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }
}
