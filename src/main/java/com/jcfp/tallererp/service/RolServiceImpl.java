package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.repository.RolRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl extends BaseServiceImpl<Rol, Long, RolRepository> implements RolService{
    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        super(rolRepository);
    }

    @Override
    public List<Rol> findByFilterParam(String filter) {
        return List.of();
    }
}
