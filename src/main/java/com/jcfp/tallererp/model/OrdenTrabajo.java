package com.jcfp.tallererp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "ordenes")
@Getter
@Setter
public class OrdenTrabajo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private EstadoOrden estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicioTrabajo;
    private LocalDateTime fechaFinalizacion;
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL)
    private List<TareaChapaPintura> tareas;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL)
    private List<MaterialUtilizado> materiales;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private boolean pagado;
    private String metodoPago;
    private LocalDateTime fechaPago;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnicoPrincipal;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
