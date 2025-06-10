package com.jcfp.tallererp.entity;

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
    @Column(nullable = false)
    private String nombre;
    private String titular;
    @Column(unique = true, nullable = false)
    private String documento;
    private String direccion;
    private String cp;
    private String poblacion;
    private String provincia;
    private String pais;
    private String email;
    private String telefono;
}
