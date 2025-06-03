package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.PuestoEmpleadoDTO;
import com.jcfp.tallererp.model.Contrato;
import com.jcfp.tallererp.repository.ContratoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratoServiceImpl extends BaseServiceImpl<Contrato, Long, ContratoRepository> implements ContratoService {
   private final ContratoRepository repository;
    @Autowired
    public ContratoServiceImpl(ContratoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public List<PuestoEmpleadoDTO> getPuestosActuales() {
        return repository.findByActivoTrue().stream()
                .map(c -> new PuestoEmpleadoDTO(c.getEmpleado().getId(), c.getPuesto().getNombre()))
                .collect(Collectors.toList());
    }
}
