package com.jcfp.tallererp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity(name = "ordenes")
@Getter
@Setter
public class OrdenTrabajo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigoOrden;
    @ManyToOne
    private Empleado empleadoAsignado;
    @ManyToOne
    private Vehiculo vehiculo;
    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<ArticuloUsado> articulosUsados;
    @ManyToOne
    private EstadoOrden estadoOrden;
    private LocalDate fechaOrden;
    private LocalDate fechaInicio;
    private LocalDate fechaFinalizacion;
    private LocalDate fechaPago;
    private boolean pagada;
    private String observaciones;
    private double total;
}
