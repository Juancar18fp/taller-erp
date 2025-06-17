package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.DatosEmpresaDto;

import java.util.Optional;

public interface DatosEmpresaService {
    Optional<DatosEmpresaDto> obtenerConfiguracionActiva();
    DatosEmpresaDto guardarConfiguracion(DatosEmpresaDto dto);
    void eliminarConfiguracion();
    boolean existeConfiguracionActiva();
}
