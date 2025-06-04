package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.OrdenTrabajo;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenTrabajoServiceImpl extends BaseServiceImpl<OrdenTrabajo,Long, OrdenTrabajoRepository> implements OrdenTrabajoService{
    @Autowired
    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository repository) {
        super(repository);
    }

    @Override
    public List<OrdenTrabajo> findByFilterParam(String filter) {
        return List.of();
    }

}
