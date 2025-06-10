package com.jcfp.tallererp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "articulos_usados")
@Getter
@Setter
public class ArticuloUsado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Articulo articulo;
    private int cantidad;
    @ManyToOne
    @JsonBackReference
    private OrdenTrabajo ordenTrabajo;
}
