package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.dto.DatosEmpresaDto;
import com.jcfp.tallererp.service.DatosEmpresaService;
import com.jcfp.tallererp.util.DatosEmpresaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuracion")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DatosEmpresaController {

    private final DatosEmpresaService datosEmpresaService;
    
    @GetMapping("/empresa")
    public ResponseEntity<?> obtenerConfiguracion() {
        log.info("Petición para obtener configuración de empresa");

        try {
            return datosEmpresaService.obtenerConfiguracionActiva()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.noContent().build());

        } catch (Exception e) {
            log.error("Error al obtener configuración", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al obtener la configuración"));
        }
    }

    @PutMapping("/empresa")
    public ResponseEntity<?> guardarConfiguracion(@RequestBody DatosEmpresaDto configuracionDTO) {
        log.info("Petición para guardar configuración de empresa: {}", configuracionDTO.getNombre());
        try {
            DatosEmpresaDto configuracionGuardada = datosEmpresaService.guardarConfiguracion(configuracionDTO);

            return ResponseEntity.ok(configuracionGuardada);

        } catch (DatosEmpresaException e) {
            log.warn("Error de negocio al guardar configuración: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));

        } catch (Exception e) {
            log.error("Error inesperado al guardar configuración", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error interno del servidor"));
        }
    }

    @DeleteMapping("/empresa")
    public ResponseEntity<?> eliminarConfiguracion() {
        log.info("Petición para eliminar configuración de empresa");

        try {
            datosEmpresaService.eliminarConfiguracion();

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Configuración eliminada exitosamente");

            return ResponseEntity.ok(response);

        } catch (DatosEmpresaException e) {
            log.warn("Error al eliminar configuración: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));

        } catch (Exception e) {
            log.error("Error inesperado al eliminar configuración", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error interno del servidor"));
        }
    }

    @GetMapping("/empresa/existe")
    public ResponseEntity<Map<String, Boolean>> verificarExistencia() {
        log.info("Verificando existencia de configuración de empresa");

        try {
            boolean existe = datosEmpresaService.existeConfiguracionActiva();

            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al verificar existencia", e);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", false);
            return ResponseEntity.ok(response);
        }
    }

    private Map<String, String> crearMensajeError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
