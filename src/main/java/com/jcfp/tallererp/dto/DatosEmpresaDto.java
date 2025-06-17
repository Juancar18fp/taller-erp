package com.jcfp.tallererp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DatosEmpresaDto {
    private Long id;
    private String nombre;
    private String razonSocial;
    private String cif;
    private String numeroRegistro;
    private String direccion;
    private String codigoPostal;
    private String poblacion;
    private String provincia;
    private String pais;
    private String telefono;
    private String telefonoSecundario;
    private String email;
    private String emailSecundario;
    private String sitioWeb;
}

