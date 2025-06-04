package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Empleado;
import com.jcfp.tallererp.service.EmpleadoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController extends BaseController<Empleado, EmpleadoService> {

    @Autowired
    public EmpleadoController(EmpleadoService service) {
        super(service);
    }


}
