package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Vehiculo;
import com.jcfp.tallererp.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiculoServiceImpl extends BaseServiceImpl<Vehiculo, Long, VehiculoRepository> implements VehiculoService {
    @Autowired
    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        super(vehiculoRepository);
    }
}
