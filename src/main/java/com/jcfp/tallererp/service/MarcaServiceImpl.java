package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Marca;
import com.jcfp.tallererp.repository.MarcaRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MarcaServiceImpl extends BaseServiceImpl<Marca, Long, MarcaRepository> implements MarcaService {

    private final MarcaRepository marcaRepository;
    @Autowired
    public MarcaServiceImpl(MarcaRepository marcaRepository) {
        super(marcaRepository);
        this.marcaRepository = marcaRepository;
    }

    @Override
    public List<Marca> findByFilterParam(String filter) {
        return marcaRepository.findByNombreContainingIgnoreCase(filter);

    }
}
