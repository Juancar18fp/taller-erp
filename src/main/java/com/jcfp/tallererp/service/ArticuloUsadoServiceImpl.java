package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.ArticuloUsadoDTO;
import com.jcfp.tallererp.model.Articulo;
import com.jcfp.tallererp.model.ArticuloUsado;
import com.jcfp.tallererp.model.OrdenTrabajo;
import com.jcfp.tallererp.repository.ArticuloRepository;
import com.jcfp.tallererp.repository.ArticuloUsadoRepository;
import com.jcfp.tallererp.repository.OrdenTrabajoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ArticuloUsadoServiceImpl extends BaseServiceImpl<ArticuloUsado, Long, ArticuloUsadoRepository> implements ArticuloUsadoService{
    private final ArticuloUsadoRepository articuloUsadoRepository ;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ArticuloRepository articuloRepository;

    @Autowired
    public ArticuloUsadoServiceImpl(ArticuloUsadoRepository articuloUsadoRepository, OrdenTrabajoRepository ordenTrabajoRepository, ArticuloRepository articuloRepository) {
        super(articuloUsadoRepository);
        this.articuloUsadoRepository = articuloUsadoRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.articuloRepository = articuloRepository;
    }

    @Override
    public List<ArticuloUsado> findByFilterParam(String filter) {
        return List.of();
    }

    public ArticuloUsado guardar(ArticuloUsadoDTO dto) {
        Articulo articulo = articuloRepository.findById(dto.getArticuloId())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        OrdenTrabajo ordenTrabajo = ordenTrabajoRepository.findById(dto.getOrdenTrabajoId())
                .orElseThrow(() -> new EntityNotFoundException("Orden de trabajo no encontrada"));

        ArticuloUsado articuloUsado = new ArticuloUsado();
        articuloUsado.setArticulo(articulo);
        articuloUsado.setOrdenTrabajo(ordenTrabajo);
        articuloUsado.setCantidad(dto.getCantidad());

        return articuloUsadoRepository.save(articuloUsado);
    }

    public ArticuloUsado actualizar(Long id, ArticuloUsadoDTO dto) {
        ArticuloUsado existente = articuloUsadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el artículo usado"));

        Articulo articulo = articuloRepository.findById(dto.getArticuloId())
                .orElseThrow(() -> new RuntimeException("No se encontró el artículo"));

        existente.setCantidad(dto.getCantidad());
        existente.setArticulo(articulo);

        return articuloUsadoRepository.save(existente);
    }
}
