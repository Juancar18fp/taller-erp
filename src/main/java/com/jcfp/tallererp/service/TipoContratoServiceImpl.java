package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.TipoContrato;
import com.jcfp.tallererp.repository.TipoContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TipoContratoServiceImpl implements TipoContratoService{
    private final TipoContratoRepository repository;

    @Autowired
    public TipoContratoServiceImpl(TipoContratoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TipoContrato> findAllFiltered(String filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<TipoContrato> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<TipoContrato> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public TipoContrato create(TipoContrato entity) {
        return null;
    }

    @Override
    public TipoContrato update(Long aLong, TipoContrato entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
