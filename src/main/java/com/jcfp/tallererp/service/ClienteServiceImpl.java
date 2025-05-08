package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Cliente;
import com.jcfp.tallererp.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long, ClienteRepository> implements ClienteService {
    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }
}