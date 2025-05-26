package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Articulo;
import com.jcfp.tallererp.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long, ArticuloRepository> implements ArticuloService {
    @Autowired
    public ArticuloServiceImpl(ArticuloRepository articuloRepository) {
        super(articuloRepository);
    }
}
