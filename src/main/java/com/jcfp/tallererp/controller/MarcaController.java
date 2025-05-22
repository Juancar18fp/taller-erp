package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/marcas")
public class MarcaController{
    private final MarcaService service;

    @Autowired
    public MarcaController(MarcaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Marca>> getAllMarcas() {
        List<Marca> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }
}
