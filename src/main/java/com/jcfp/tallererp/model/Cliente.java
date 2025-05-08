package com.jcfp.tallererp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "clientes")
@Getter
@Setter
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String titular;
    @Column(unique = true)
    private String documento;
    private String direccion;
    private String cp;
    private String poblacion;
    private String provincia;
    private String pais;
    private String email;
    private String telefono;
}
