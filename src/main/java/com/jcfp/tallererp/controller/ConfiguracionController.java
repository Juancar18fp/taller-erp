package com.jcfp.tallererp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    /**
     * Obtener configuración de empresa
     * GET /configuracion/empresa
     */
    @GetMapping("/empresa")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracionEmpresa() {
        // Por ahora devolver configuración por defecto
        // Más adelante puedes guardar esto en base de datos
        Map<String, Object> config = new HashMap<>();

        config.put("nombre", "TallerPro S.L.");
        config.put("razonSocial", "TallerPro Servicios Automotrices S.L.");
        config.put("cif", "B12345678");
        config.put("numeroRegistro", "");
        config.put("direccion", "Calle Principal, 123");
        config.put("codigoPostal", "28001");
        config.put("poblacion", "Madrid");
        config.put("provincia", "Madrid");
        config.put("pais", "España");
        config.put("telefono", "912345678");
        config.put("telefonoSecundario", "");
        config.put("email", "info@tallerpro.com");
        config.put("emailSecundario", "");
        config.put("sitioWeb", "https://www.tallerpro.com");
        config.put("horarioSemana", "09:00 - 18:00");
        config.put("horarioSabado", "09:00 - 14:00");
        config.put("horarioDomingo", "Cerrado");
        config.put("ivaGeneral", 21);
        config.put("ivaReducido", 10);
        config.put("moneda", "EUR");
        config.put("regimenEspecial", false);
        config.put("descripcion", "Taller especializado en reparación y mantenimiento de vehículos");
        config.put("numeroLicencia", "");
        config.put("seguroResponsabilidad", "");

        return ResponseEntity.ok(config);
    }

    /**
     * Guardar configuración de empresa
     * PUT /configuracion/empresa
     */
    @PutMapping("/empresa")
    public ResponseEntity<Map<String, String>> guardarConfiguracionEmpresa(
            @RequestBody Map<String, Object> configuracion) {

        try {
            // Por ahora solo loggeamos la configuración
            // Más adelante puedes guardar en base de datos
            System.out.println("Guardando configuración de empresa:");
            configuracion.forEach((key, value) ->
                    System.out.println(key + ": " + value)
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Configuración guardada correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al guardar la configuración");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint de prueba
     * GET /configuracion/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint de configuración funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}