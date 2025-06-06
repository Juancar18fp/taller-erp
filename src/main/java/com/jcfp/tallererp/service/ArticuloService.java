package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Articulo;
import com.jcfp.tallererp.util.CrudService;

public interface ArticuloService extends CrudService<Articulo, Long> {

    void restaurarStock(Long articuloId, int cantidad);
    boolean descontarStock(Long articuloId, int cantidad);
}
