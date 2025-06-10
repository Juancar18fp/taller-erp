package com.jcfp.tallererp.security;

import com.jcfp.tallererp.entity.Empleado;
import com.jcfp.tallererp.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoPrincipal implements UserDetails {
    private Long id;
    private String usuario;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
    private Boolean activo;

    public static EmpleadoPrincipal create(Empleado empleado) {
        return new EmpleadoPrincipal(
                empleado.getId(),
                empleado.getDocumento(),
                empleado.getNombre(),
                empleado.getEmail(),
                empleado.getPassword(),
                empleado.getRol(),
                empleado.getActivo()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
    }

    @Override
    public String getUsername() {
        return usuario;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
