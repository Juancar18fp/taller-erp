package com.jcfp.tallererp.controller;


import com.jcfp.tallererp.model.OrdenTrabajo;
import com.jcfp.tallererp.service.OrdenTrabajoService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordenes")
public class OrdenTrabajoController extends BaseController<OrdenTrabajo, OrdenTrabajoService> {
    @Autowired
    public OrdenTrabajoController(OrdenTrabajoService service) {
        super(service);
    }
}
