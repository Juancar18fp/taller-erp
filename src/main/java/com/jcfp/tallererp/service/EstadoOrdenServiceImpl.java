package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.EstadoOrden;
import com.jcfp.tallererp.repository.EstadoOrdenRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoOrdenServiceImpl extends BaseServiceImpl<EstadoOrden, Long, EstadoOrdenRepository> implements EstadoOrdenService {

    @Autowired
    public EstadoOrdenServiceImpl(EstadoOrdenRepository repository) {
        super(repository);
    }

    @Override
    public List<EstadoOrden> findByFilterParam(String filter) {
        return List.of();
    }
}
