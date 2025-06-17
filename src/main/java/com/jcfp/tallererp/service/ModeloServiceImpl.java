package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Modelo;
import com.jcfp.tallererp.repository.ModeloRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloServiceImpl extends BaseServiceImpl<Modelo, Long, ModeloRepository> implements ModeloService{

    private final ModeloRepository modeloRepository;

    @Autowired
    public ModeloServiceImpl(ModeloRepository modeloRepository) {
        super(modeloRepository);
        this.modeloRepository = modeloRepository;
    }

    @Override
    public List<Modelo> findByFilterParam(String filter) {
        return List.of();
    }

    @Override
    public List<Modelo> findModeloByMarcaAndNombre(Long marca, String modelo) {
        return modeloRepository.findByMarca_IdAndNombreContainingIgnoreCase(marca,modelo);
    }

}
