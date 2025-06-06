package com.jcfp.tallererp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private Long empleadoId;
    private String dni;
    private String nombre;
    private String email;
    private RolDto rol;
    private String[] permisos;

    public LoginResponse(String jwt, Long id, String dni, String nombre, String email, RolDto rol, String[] permisos) {
        this.token = jwt;
        this.empleadoId = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.permisos = permisos;
    }
    public static class RolDto {
        private Long id;
        private String nombre;

        public RolDto() {}

        public RolDto(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

    }
}
