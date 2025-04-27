package com.jcfp.tallererp.service;

import com.jcfp.tallererp.repository.ClienteRepository;
import com.jcfp.tallererp.model.Cliente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodos(String filtro) {
        if (filtro != null && !filtro.isEmpty()) {
            // Búsqueda por nombre o email que contenga el filtro
            return clienteRepository.buscarPorFiltro(filtro.toLowerCase());
        }
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}