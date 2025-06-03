package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.Rol;
import com.jcfp.tallererp.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class RolServiceImpl implements RolService{
    private final RolRepository rolRepository;
    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }
    @Override
    public Page<Rol> findAllFiltered(String filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Rol create(Rol entity) {
        return null;
    }

    @Override
    public Rol update(Long aLong, Rol entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
