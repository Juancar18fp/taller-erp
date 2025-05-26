package com.jcfp.tallererp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "tecnicos")
@Getter
@Setter
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String especialidad;

}

