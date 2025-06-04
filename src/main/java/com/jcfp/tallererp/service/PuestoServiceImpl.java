package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Puesto;
import com.jcfp.tallererp.repository.PuestoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PuestoServiceImpl extends BaseServiceImpl<Puesto, Long, PuestoRepository> implements PuestoService{
    @Autowired
    public PuestoServiceImpl(PuestoRepository repository) {
        super(repository);
    }

    @Override
    public List<Puesto> findByFilterParam(String filter) {
        return List.of();
    }
}
