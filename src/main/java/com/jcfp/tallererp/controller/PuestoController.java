package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Puesto;
import com.jcfp.tallererp.service.PuestoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/puestos")
public class PuestoController extends BaseController<Puesto, PuestoService> {

    @Autowired
    public PuestoController(PuestoService service) {
        super(service);
    }

}
