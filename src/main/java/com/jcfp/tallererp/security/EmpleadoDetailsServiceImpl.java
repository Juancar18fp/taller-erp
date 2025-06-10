package com.jcfp.tallererp.security;

import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoDetailsServiceImpl implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;
    @Autowired
    public EmpleadoDetailsServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByDocumentoForAuthentication(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado con DNI: " + dni));

        return EmpleadoPrincipal.create(empleado);
    }
}
