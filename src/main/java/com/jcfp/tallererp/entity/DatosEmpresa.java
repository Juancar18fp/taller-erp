package com.jcfp.tallererp.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity(name = "datos_empresa")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DatosEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    private String razonSocial;
    @Column( nullable = false)
    private String cif;
    private String numeroRegistro;
    @Column(nullable = false)
    private String direccion;
    @Column(nullable = false)
    private String codigoPostal;
    @Column(nullable = false)
    private String poblacion;
    @Column(nullable = false)
    private String provincia;
    @Column(nullable = false)
    private String pais;
    @Column( nullable = false)
    private String telefono;
    private String telefonoSecundario;
    @Column(nullable = false)
    private String email;
    private String emailSecundario;
    private String sitioWeb;
    @Column(nullable = false)
    private Boolean activo = true;
}