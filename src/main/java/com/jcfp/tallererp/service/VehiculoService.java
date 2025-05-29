package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Vehiculo;

import java.util.List;

public interface VehiculoService extends CrudService<Vehiculo, Long>{
    List<Vehiculo> findByName (String name);

}
