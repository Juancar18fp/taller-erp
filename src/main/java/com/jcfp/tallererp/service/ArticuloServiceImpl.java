package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Articulo;
import com.jcfp.tallererp.repository.ArticuloRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long, ArticuloRepository> implements ArticuloService {
    private final ArticuloRepository articuloRepository;
    @Autowired
    public ArticuloServiceImpl(ArticuloRepository articuloRepository) {
        super(articuloRepository);
        this.articuloRepository = articuloRepository;
    }

    @Override
    public List<Articulo> findByFilterParam(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        return articuloRepository.findByDescripcionContainingIgnoreCase(filter);
    }
}
