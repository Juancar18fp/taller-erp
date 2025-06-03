package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.EstadoCivil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.jcfp.tallererp.repository.EstadoCivilRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoCivilServiceImpl implements EstadoCivilService {
    private final EstadoCivilRepository repository;
    @Autowired
    public EstadoCivilServiceImpl(EstadoCivilRepository repository) {
        this.repository = repository;
    }
    @Override
    public Page<EstadoCivil> findAllFiltered(String filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<EstadoCivil> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<EstadoCivil> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public EstadoCivil create(EstadoCivil entity) {
        return null;
    }

    @Override
    public EstadoCivil update(Long aLong, EstadoCivil entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
