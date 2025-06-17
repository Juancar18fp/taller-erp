package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.dto.ArticuloUsadoDTO;
import com.jcfp.tallererp.dto.ArticuloUsadoMapper;
import com.jcfp.tallererp.entity.ArticuloUsado;
import com.jcfp.tallererp.service.ArticuloUsadoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/articulos-usados")
public class ArticuloUsadoController extends BaseController<ArticuloUsado, ArticuloUsadoService> {
    private final ArticuloUsadoService articuloUsadoService;
    @Autowired
    public ArticuloUsadoController(ArticuloUsadoService service, ArticuloUsadoService articuloUsadoService) {
        super(service);
        this.articuloUsadoService = articuloUsadoService;
    }
    @PostMapping("/guardar")
    public ResponseEntity<ArticuloUsado> guardarArticuloUsado(@RequestBody ArticuloUsadoDTO dto) {
        ArticuloUsado guardado = articuloUsadoService.guardar(dto);
        return ResponseEntity.ok(guardado);
    }
    @PutMapping("/guardar/{id}")
    public ResponseEntity<ArticuloUsadoDTO> actualizarArticuloUsado(
            @PathVariable Long id,
            @RequestBody ArticuloUsadoDTO dto) {

        ArticuloUsado actualizado = articuloUsadoService.actualizar(id, dto);
        return ResponseEntity.ok(ArticuloUsadoMapper.toDTO(actualizado));
    }
}
