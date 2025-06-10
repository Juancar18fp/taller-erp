package com.jcfp.tallererp.dto;

import com.jcfp.tallererp.entity.ArticuloUsado;

public class ArticuloUsadoMapper {

    public static ArticuloUsadoDTO toDTO(ArticuloUsado entity) {
        ArticuloUsadoDTO dto = new ArticuloUsadoDTO();
        dto.setArticuloId(entity.getId());
        dto.setCantidad(entity.getCantidad());

        if (entity.getArticulo() != null) {
            dto.setArticuloId(entity.getArticulo().getId());
        }

        return dto;
    }

}
