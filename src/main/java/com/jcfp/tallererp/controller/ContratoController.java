package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.dto.PuestoEmpleadoDTO;
import com.jcfp.tallererp.entity.Contrato;
import com.jcfp.tallererp.service.ContratoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratos")
public class ContratoController extends BaseController<Contrato, ContratoService> {
    private final ContratoService contratoService;

    @Autowired
    public ContratoController(ContratoService contratoService) {
        super(contratoService);
        this.contratoService = contratoService;
    }

    @GetMapping("/activos")
    public List<PuestoEmpleadoDTO> getPuestosActuales() {
        return contratoService.getPuestosActuales();
    }

}
