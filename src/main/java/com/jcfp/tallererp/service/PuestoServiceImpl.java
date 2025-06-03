package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Puesto;
import com.jcfp.tallererp.repository.PuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuestoServiceImpl implements PuestoService{
    private final PuestoRepository repository;
    @Autowired
    public PuestoServiceImpl(PuestoRepository repository) {
        this.repository = repository;
    }
    @Override
    public Page<Puesto> findAllFiltered(String filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<Puesto> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Puesto> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Puesto create(Puesto entity) {
        return null;
    }

    @Override
    public Puesto update(Long aLong, Puesto entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
