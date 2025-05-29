package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Cliente;

import java.util.List;

public interface ClienteService extends CrudService<Cliente,Long> {
    List<Cliente> findByName (String name);
}
