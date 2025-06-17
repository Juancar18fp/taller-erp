package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Modelo;
import com.jcfp.tallererp.service.ModeloService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/modelos")
public class ModeloController extends BaseController<Modelo,  ModeloService> {
    private final ModeloService service;

    @Autowired
    public ModeloController(ModeloService service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Modelo>> buscarModelos(
            @RequestParam(required = false) Long marca,
            @RequestParam(required = false) String modelo
    ) {
        if ((marca != null) || (modelo != null && !modelo.isEmpty())) {
            return ResponseEntity.ok(service.findModeloByMarcaAndNombre(
                    marca, modelo
            ));
        }
        return ResponseEntity.ok(service.findAll());
    }
}