package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Articulo;
import com.jcfp.tallererp.service.ArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articulos")
public class ArticuloController extends BaseController<Articulo, ArticuloService>{
    @Autowired
    public ArticuloController(ArticuloService articuloService) {
        super(articuloService);
    }
}
