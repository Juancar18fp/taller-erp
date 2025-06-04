package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Modelo;
import com.jcfp.tallererp.util.CrudService;

import java.util.List;

public interface ModeloService extends CrudService<Modelo, Long> {

    List<Modelo> findModeloByMarcaAndNombre(Long marca,String modelo);

}
