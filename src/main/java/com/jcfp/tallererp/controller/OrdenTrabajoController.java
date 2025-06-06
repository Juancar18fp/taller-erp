package com.jcfp.tallererp.controller;


import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.service.OrdenTrabajoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ordenes")
public class OrdenTrabajoController extends BaseController<OrdenTrabajo, OrdenTrabajoService> {
    private final OrdenTrabajoService ordenTrabajoService;
    @Autowired
    public OrdenTrabajoController(OrdenTrabajoService service) {
        super(service);
        this.ordenTrabajoService = service;
    }

    @GetMapping("/siguiente-numero")
    public ResponseEntity<Map<String, Object>> obtenerSiguienteNumero(@RequestParam String fecha) {
        try {
            LocalDate fechaOrden = LocalDate.parse(fecha);
            int siguienteNumero = ordenTrabajoService.obtenerSiguienteNumeroOrden(fechaOrden);
            Map<String, Object> response = new HashMap<>();
            response.put("siguienteNumero", siguienteNumero);
            response.put("fecha", fecha);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar número de orden");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
