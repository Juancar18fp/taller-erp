package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> obtenerTodos(String filtro);
    Optional<Cliente> obtenerPorId(Long id);
    Cliente guardar(Cliente cliente);
    void eliminar(Long id);
}
