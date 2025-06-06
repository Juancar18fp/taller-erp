package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.service.CsvService;
import com.jcfp.tallererp.util.JwtUtils;
import com.jcfp.tallererp.dto.ImportResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/csv")
@CrossOrigin(origins = "*")
public class CsvController {

    @Autowired
    private CsvService csvService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/{entity}/export")
    public ResponseEntity<?> exportEntityData(
            @PathVariable String entity,
            @RequestParam(value = "token", required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (isValidToken(token, authHeader)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            Resource resource = csvService.exportToCSV(entity);
            String filename = entity + "_" + java.time.LocalDate.now() + ".csv";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al exportar: " + e.getMessage()));
        }
    }

    @GetMapping("/{entity}/template")
    public ResponseEntity<?> downloadTemplate(
            @PathVariable String entity,
            @RequestParam(value = "token", required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (isValidToken(token, authHeader)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            Resource resource = csvService.generateTemplate(entity);
            String filename = "plantilla_" + entity + ".csv";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al generar plantilla: " + e.getMessage()));
        }
    }

    @PostMapping("/{entity}/import")
    public ResponseEntity<ImportResultDto> importData(
            @PathVariable String entity,
            @RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        try {
            List<Map<String, Object>> data = requestBody.get("data");
            if (data == null || data.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ImportResultDto(false, "No hay datos para importar", 0, 0));
            }

            ImportResultDto result = csvService.importFromCSV(entity, data);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ImportResultDto(false, "Error al importar: " + e.getMessage(), 0, 0));
        }
    }

    private boolean isValidToken(String paramToken, String authHeader) {
        String token = null;

        if (paramToken != null && !paramToken.trim().isEmpty()) {
            token = paramToken.trim();
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.trim().isEmpty()) {
            return true;
        }

        try {
            return !jwtUtils.validateJwtToken(token);
        } catch (Exception e) {
            return true;
        }
    }
}