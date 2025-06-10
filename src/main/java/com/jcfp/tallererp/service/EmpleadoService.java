package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.util.CrudService;

import java.util.List;

public interface EmpleadoService extends CrudService<Empleado, Long> {
    List<Empleado> findByFilterParam(String filter);
    void actualizarContratosEmpleado(Long empleadoId, Long contratoActivoId);
    List<Empleado> getEmpleadosActivos();
    List<Empleado> getEmpleadosInactivos();
    List<Empleado> getEmpleadosPorEstado(String estado);
}