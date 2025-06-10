package com.jcfp.tallererp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "puestos")
@Getter
@Setter
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
}
