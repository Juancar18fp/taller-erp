package com.jcfp.tallererp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PuestoEmpleadoDTO {
    private Long empleadoId;
    private String puesto;
}
