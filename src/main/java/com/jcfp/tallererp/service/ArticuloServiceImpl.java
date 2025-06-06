package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Articulo;
import com.jcfp.tallererp.repository.ArticuloRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public boolean descontarStock(Long articuloId, int cantidad) {
        Optional<Articulo> articuloOpt = articuloRepository.findById(articuloId);

        if (articuloOpt.isPresent()) {
            Articulo articulo = articuloOpt.get();

            if (articulo.getStock() >= cantidad) {
                articulo.setStock(articulo.getStock() - cantidad);
                articuloRepository.save(articulo);
                return true;
            } else {
                throw new RuntimeException("Stock insuficiente. Disponible: " + articulo.getStock());
            }
        } else {
            throw new RuntimeException("Artículo no encontrado con ID: " + articuloId);
        }
    }

    public void restaurarStock(Long articuloId, int cantidad) {
        Optional<Articulo> articuloOpt = articuloRepository.findById(articuloId);

        if (articuloOpt.isPresent()) {
            Articulo articulo = articuloOpt.get();
            articulo.setStock(articulo.getStock() + cantidad);
            articuloRepository.save(articulo);
        } else {
            throw new RuntimeException("Artículo no encontrado con ID: " + articuloId);
        }
    }

    public boolean verificarStock(Long articuloId, int cantidadRequerida) {
        Optional<Articulo> articuloOpt = articuloRepository.findById(articuloId);

        return articuloOpt.filter(articulo -> articulo.getStock() >= cantidadRequerida).isPresent();

    }
}
