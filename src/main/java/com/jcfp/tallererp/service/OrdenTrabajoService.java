package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.util.CrudService;

import java.time.LocalDate;

public interface OrdenTrabajoService extends CrudService<OrdenTrabajo, Long> {
    int obtenerSiguienteNumeroOrden(LocalDate fecha);
}
