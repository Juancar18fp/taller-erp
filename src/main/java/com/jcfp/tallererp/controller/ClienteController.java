package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Cliente;
import com.jcfp.tallererp.service.ClienteService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController extends BaseController<Cliente, ClienteService> {

    @Autowired
    public ClienteController(ClienteService clienteService) {
        super(clienteService);
    }

}
