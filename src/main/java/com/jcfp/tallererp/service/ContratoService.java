package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.PuestoEmpleadoDTO;
import com.jcfp.tallererp.entity.Contrato;
import com.jcfp.tallererp.util.CrudService;

import java.util.List;

public interface ContratoService extends CrudService<Contrato,Long> {
    List<PuestoEmpleadoDTO> getPuestosActuales();}
