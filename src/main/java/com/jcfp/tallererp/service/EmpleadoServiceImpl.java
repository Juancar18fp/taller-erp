package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Empleado;
import com.jcfp.tallererp.repository.EmpleadoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoServiceImpl extends BaseServiceImpl<Empleado, Long, EmpleadoRepository> implements EmpleadoService{
    private final EmpleadoRepository repository;
    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public List<Empleado> findByFilterParam(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        return repository.findByNombreContainingIgnoreCase(filter);    }
}
