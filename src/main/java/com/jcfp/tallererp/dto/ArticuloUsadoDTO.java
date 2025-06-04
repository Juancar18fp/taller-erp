package com.jcfp.tallererp.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticuloUsadoDTO {
    private Long articuloId;
    private Long ordenTrabajoId;
    private Integer cantidad;

    public ArticuloUsadoDTO() {
    }

}
