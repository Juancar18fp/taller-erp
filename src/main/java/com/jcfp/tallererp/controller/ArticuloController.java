package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Articulo;
import com.jcfp.tallererp.service.ArticuloService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articulos")
public class ArticuloController extends BaseController<Articulo, ArticuloService> {
    @Autowired
    public ArticuloController(ArticuloService articuloService) {
        super(articuloService);
    }
}
