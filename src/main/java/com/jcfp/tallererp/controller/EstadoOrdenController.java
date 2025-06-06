package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.EstadoOrden;
import com.jcfp.tallererp.service.EstadoOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estados-ordenes")
public class EstadoOrdenController {
    private final EstadoOrdenService service;
    @Autowired
    public EstadoOrdenController(EstadoOrdenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EstadoOrden>> getAllEstadoCivils() {
        List<EstadoOrden> ordenes = service.findAll();
        return ResponseEntity.ok(ordenes);
    }

}
