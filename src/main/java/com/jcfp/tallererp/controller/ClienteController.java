package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.model.Cliente;
import com.jcfp.tallererp.service.ClienteService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController extends BaseController<Cliente, ClienteService> {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService, ClienteService clienteService1) {
        super(clienteService);
        this.clienteService = clienteService1;
    }
    @GetMapping(params = "search")
    public ResponseEntity<List<Cliente>> buscarClientes(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(clienteService.findByName(search));
        }
        return ResponseEntity.ok(clienteService.findAll());
    }
}
