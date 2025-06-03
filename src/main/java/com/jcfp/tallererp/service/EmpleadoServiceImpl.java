package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Empleado;
import com.jcfp.tallererp.repository.EmpleadoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServiceImpl extends BaseServiceImpl<Empleado, Long, EmpleadoRepository> implements EmpleadoService{
    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository repository) {
        super(repository);
    }
}
