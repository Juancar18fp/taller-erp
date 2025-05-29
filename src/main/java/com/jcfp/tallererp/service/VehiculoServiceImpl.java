package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Vehiculo;
import com.jcfp.tallererp.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoServiceImpl extends BaseServiceImpl<Vehiculo, Long, VehiculoRepository> implements VehiculoService {
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        super(vehiculoRepository);
        this.vehiculoRepository = vehiculoRepository;
    }
    @Override
    public List<Vehiculo> findByName(String name) {
        if (name == null || name.isEmpty()) {
            return findAll();
        }
        return vehiculoRepository.findByMatriculaContainingIgnoreCase(name);
    }
}
