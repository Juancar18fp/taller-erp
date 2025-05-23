package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.model.Modelo;

import java.util.List;

public interface ModeloService extends CrudService<Modelo, Long> {

    List<Modelo> findAllModeloByMarca(Marca marca);

}
