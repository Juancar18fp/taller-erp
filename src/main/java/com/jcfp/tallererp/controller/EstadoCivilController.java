package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.EstadoCivil;
import com.jcfp.tallererp.service.EstadoCivilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estados-civiles")
public class EstadoCivilController {
    private final EstadoCivilService service;

    @Autowired
    public EstadoCivilController(EstadoCivilService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EstadoCivil>> getAllEstadoCivils() {
        List<EstadoCivil> estados = service.findAll();
        return ResponseEntity.ok(estados);
    }
}
