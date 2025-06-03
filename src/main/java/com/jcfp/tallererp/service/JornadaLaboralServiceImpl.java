package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.JornadaLaboral;
import com.jcfp.tallererp.repository.JornadaLaboralRepository;
import com.jcfp.tallererp.repository.PuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JornadaLaboralServiceImpl implements JornadaLaboralService {
    private final JornadaLaboralRepository repository;

    @Autowired
    public JornadaLaboralServiceImpl(JornadaLaboralRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<JornadaLaboral> findAllFiltered(String filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<JornadaLaboral> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<JornadaLaboral> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public JornadaLaboral create(JornadaLaboral entity) {
        return null;
    }

    @Override
    public JornadaLaboral update(Long aLong, JornadaLaboral entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
