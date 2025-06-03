package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Vehiculo;
import com.jcfp.tallererp.service.VehiculoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController extends BaseController<Vehiculo, VehiculoService> {

    private final VehiculoService vehiculoService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        super(vehiculoService);
        this.vehiculoService = vehiculoService;
    }

    @GetMapping(params = "search")
    public ResponseEntity<List<Vehiculo>> buscarPorVehiculo(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(vehiculoService.findByName(search));
        }
        return ResponseEntity.ok(vehiculoService.findAll());
    }
}
