package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.model.Modelo;
import com.jcfp.tallererp.service.MarcaService;
import com.jcfp.tallererp.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/modelos")
public class ModeloController{
    private final ModeloService service;
    private final MarcaService marcaService;


    @Autowired
    public ModeloController(ModeloService service, MarcaService marcaService) {
        this.service = service;
        this.marcaService = marcaService;
    }

    @GetMapping
    public ResponseEntity<List<Modelo>> getAllModelos() {
        List<Modelo> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Modelo>> getAllModelosByIdMarca(@PathVariable Long id) {

        Optional<Marca> optionalMarca = marcaService.findById(id);
        if (optionalMarca.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Marca marca = optionalMarca.get();
        List<Modelo> modelos = service.findAllModeloByMarca(marca);
        return ResponseEntity.ok(modelos);
    }
}