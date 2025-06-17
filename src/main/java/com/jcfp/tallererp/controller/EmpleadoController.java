package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.dto.CambiarPasswordRequest;
import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.service.EmpleadoServiceImpl;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController extends BaseController<Empleado, EmpleadoServiceImpl> {

    private final EmpleadoServiceImpl empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoServiceImpl service) {
        super(service);
        this.empleadoService = service;
    }

    @PutMapping("/{id}/contrato-activo")
    public ResponseEntity<Void> cambiarContratoActivo(
            @PathVariable Long id,
            @RequestParam(required = false) Long contratoId) {
        try {
            empleadoService.actualizarContratosEmpleado(id, contratoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activos")
    public List<Empleado> getEmpleadosActivos() {
        return empleadoService.getEmpleadosActivos();
    }

    @GetMapping("/inactivos")
    public List<Empleado> getEmpleadosInactivos() {
        return empleadoService.getEmpleadosInactivos();
    }

    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@PathVariable Long id, @RequestBody CambiarPasswordRequest request) {
        try {
            empleadoService.cambiarPassword(id, request);
            return ResponseEntity.ok().body(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}