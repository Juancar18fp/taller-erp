package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Cliente;
import com.jcfp.tallererp.repository.ClienteRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long, ClienteRepository> implements ClienteService {
    private final ClienteRepository clienteRepository;
    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> findByFilterParam(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        return clienteRepository.findByNombreContainingIgnoreCase(filter);
    }
}