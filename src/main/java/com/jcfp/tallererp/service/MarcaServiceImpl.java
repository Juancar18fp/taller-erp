package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaServiceImpl implements MarcaService {


    private final MarcaRepository marcaRepository;
    @Autowired
    public MarcaServiceImpl(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }
    @Override
    public Page<Marca> findAllFiltered(String filter, Pageable pageable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Marca> findAll() {
        return marcaRepository.findAll();
    }

    @Override
    public Optional<Marca> findById(Long aLong) {
        return marcaRepository.findById(aLong);
    }

    @Override
    public Marca create(Marca entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Marca update(Long aLong, Marca entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long aLong) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
