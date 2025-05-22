package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.model.Modelo;
import com.jcfp.tallererp.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloServiceImpl implements ModeloService{

    private final ModeloRepository modeloRepository;

    @Autowired
    public ModeloServiceImpl(ModeloRepository modeloRepository) {
        this.modeloRepository = modeloRepository;
    }
    @Override
    public Page<Modelo> findAllFiltered(String filter, Pageable pageable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Modelo> findAll() {
        return modeloRepository.findAll();
    }

    @Override
    public List<Modelo> findAllModeloByMarca(Marca marca) {
        return modeloRepository.findModeloByMarca(marca);
    }

    @Override
    public Optional<Modelo> findById(Long aLong) {
        return modeloRepository.findById(aLong);
    }

    @Override
    public Modelo create(Modelo entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Modelo update(Long aLong, Modelo entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long aLong) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
