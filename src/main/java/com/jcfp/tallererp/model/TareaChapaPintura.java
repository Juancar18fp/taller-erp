package com.jcfp.tallererp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "tareas_chapa_pintura")
@Getter
@Setter
public class TareaChapaPintura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private Double horas;
    private Double costo;

    @ManyToOne
    private Tecnico tecnico;

    @ManyToOne
    private OrdenTrabajo ordenTrabajo;

}
