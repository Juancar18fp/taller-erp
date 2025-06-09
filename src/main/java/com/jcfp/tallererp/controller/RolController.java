package com.jcfp.tallererp.controller;

import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.service.RolService;
import com.jcfp.tallererp.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RolController extends BaseController<Rol, RolService> {

    @Autowired
    public RolController(RolService service) {
        super(service);
    }

}
