package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {
    private final RolService service;

    @Autowired
    public RolController(RolService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> getAllRols() {
        List<Rol> clientes = service.findAll();
        return ResponseEntity.ok(clientes);
    }
}
