package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.OrdenTrabajo;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoServiceImpl extends BaseServiceImpl<OrdenTrabajo,Long, OrdenTrabajoRepository> implements OrdenTrabajoService{
    @Autowired
    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository repository) {
        super(repository);
    }
}
