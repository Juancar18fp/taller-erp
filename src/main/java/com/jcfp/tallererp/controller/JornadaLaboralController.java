package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.JornadaLaboral;
import com.jcfp.tallererp.service.JornadaLaboralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jornadas-laborales")
public class JornadaLaboralController {
    private final JornadaLaboralService service;

    @Autowired
    public JornadaLaboralController(JornadaLaboralService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<JornadaLaboral>> getAllJornadaLaborals() {
        List<JornadaLaboral> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }
}
