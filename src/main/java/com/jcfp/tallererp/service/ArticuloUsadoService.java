package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.ArticuloUsadoDTO;
import com.jcfp.tallererp.entity.ArticuloUsado;
import com.jcfp.tallererp.util.CrudService;

public interface ArticuloUsadoService extends CrudService<ArticuloUsado, Long> {
    ArticuloUsado guardar(ArticuloUsadoDTO dto);
    ArticuloUsado actualizar(Long id, ArticuloUsadoDTO dto);
}
