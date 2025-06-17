package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.EstadoCivil;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import com.jcfp.tallererp.repository.EstadoCivilRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EstadoCivilServiceImpl extends BaseServiceImpl<EstadoCivil,Long, EstadoCivilRepository> implements EstadoCivilService {
    @Autowired
    public EstadoCivilServiceImpl(EstadoCivilRepository repository) {
        super(repository);
    }

    @Override
    public List<EstadoCivil> findByFilterParam(String filter) {
        return List.of();
    }
}
