package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Articulo;
import com.jcfp.tallererp.service.ArticuloService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/articulos")
public class ArticuloController extends BaseController<Articulo, ArticuloService> {
    private final ArticuloService articuloService;
    @Autowired
    public ArticuloController(ArticuloService articuloService) {
        super(articuloService);
        this.articuloService = articuloService;
    }
    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<Map<String, Object>> descontarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> payload) {

        try {
            Integer cantidad = payload.get("cantidad");

            if (cantidad == null || cantidad <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Cantidad inválida");
                errorResponse.put("message", "La cantidad debe ser mayor a 0");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            boolean exito = articuloService.descontarStock(id, cantidad);

            if (exito) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Stock descontado correctamente");
                response.put("articuloId", id);
                response.put("cantidadDescontada", cantidad);

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Stock insuficiente");
                errorResponse.put("message", "No hay suficiente stock disponible");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al descontar stock");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/restaurar-stock")
    public ResponseEntity<Map<String, Object>> restaurarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> payload) {

        try {
            Integer cantidad = payload.get("cantidad");

            if (cantidad == null || cantidad <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Cantidad inválida");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            articuloService.restaurarStock(id, cantidad);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock restaurado correctamente");
            response.put("articuloId", id);
            response.put("cantidadRestaurada", cantidad);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al restaurar stock");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
