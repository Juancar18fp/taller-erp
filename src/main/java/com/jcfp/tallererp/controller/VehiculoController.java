package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Vehiculo;
import com.jcfp.tallererp.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController extends BaseController<Vehiculo, VehiculoService> {

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        super(vehiculoService);
    }

}
