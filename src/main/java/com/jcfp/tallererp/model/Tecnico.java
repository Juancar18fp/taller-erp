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
    @Column()
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

}

