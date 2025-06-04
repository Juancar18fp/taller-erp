package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.service.MarcaService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marcas")
public class MarcaController extends BaseController<Marca, MarcaService> {

    @Autowired
    public MarcaController(MarcaService service) {
        super(service);
    }
}
