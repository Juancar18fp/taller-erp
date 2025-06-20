package com.jcfp.tallererp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "articulos")
@Getter
@Setter
public class Articulo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private double precio;
    private int stock;
    private String proveedor;
}
