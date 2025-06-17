package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.CambiarPasswordRequest;
import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.repository.EmpleadoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl extends BaseServiceImpl<Empleado, Long, EmpleadoRepository> implements EmpleadoService {

    private final EmpleadoRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Empleado> findByFilterParam(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        return repository.findByNombreContainingIgnoreCase(filter);
    }

    @Override
    public Empleado create(Empleado empleado) {
        if (repository.existsByDocumento(empleado.getDocumento())) {
            throw new RuntimeException("Ya existe un empleado con el documento: " + empleado.getDocumento());
        }

        if (empleado.getPassword() != null && !empleado.getPassword().isEmpty()) {
            empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
        }
        return super.create(empleado);
    }

    @Override
    @Transactional
    public Empleado update(Long id, Empleado empleado) {
        Empleado empleadoExistente = findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        if (empleado.getPassword() == null || empleado.getPassword().trim().isEmpty()) {
            empleado.setPassword(empleadoExistente.getPassword());
        } else {
            empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
        }

        return repository.save(empleado);
    }

    @Transactional
    public void actualizarContratosEmpleado(Long empleadoId, Long contratoActivoId) {
        Optional<Empleado> empleadoOpt = repository.findById(empleadoId);

        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();

            empleado.getContratos().forEach(contrato -> contrato.setActivo(false));

            if (contratoActivoId != null) {
                empleado.getContratos().stream()
                        .filter(contrato -> contrato.getId().equals(contratoActivoId))
                        .findFirst()
                        .ifPresent(contrato -> contrato.setActivo(true));
            }

            repository.save(empleado);
        }
    }

    public List<Empleado> getEmpleadosActivos() {
        List<Empleado> empleados = repository.findAll();
        List<Empleado> activos = empleados.stream()
                .filter(Empleado::getActivo)
                .toList();

        activos.forEach(empleado -> empleado.setPassword(null));
        return activos;
    }

    public List<Empleado> getEmpleadosInactivos() {
        List<Empleado> empleados = repository.findAll();
        List<Empleado> inactivos = empleados.stream()
                .filter(empleado -> !empleado.getActivo())
                .toList();

        inactivos.forEach(empleado -> empleado.setPassword(null));
        return inactivos;
    }

    public List<Empleado> getEmpleadosPorEstado(String estado) {
        if ("activos".equals(estado)) {
            return getEmpleadosActivos();
        } else if ("inactivos".equals(estado)) {
            return getEmpleadosInactivos();
        }
        return findAll();
    }

    @Override
    public List<Empleado> findAll() {
        List<Empleado> empleados = super.findAll();
        empleados.forEach(empleado -> empleado.setPassword(null));
        return empleados;
    }
    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        Empleado empleado = findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        if (!passwordEncoder.matches(request.getPasswordActual(), empleado.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        empleado.setPassword(passwordEncoder.encode(request.getPasswordNueva()));

        repository.save(empleado);
    }
}