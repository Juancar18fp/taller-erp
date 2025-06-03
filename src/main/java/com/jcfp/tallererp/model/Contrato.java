package com.jcfp.tallererp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name="contratos")
@Getter
@Setter
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaContratacion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFinalizacion;
    @ManyToOne
    @JoinColumn(name = "tipo_contrato_id", nullable = false)
    private TipoContrato tipoContrato;
    @ManyToOne
    @JoinColumn(name = "jornada_laboral_id", nullable = false)
    private JornadaLaboral jornadaLaboral;
    private int salario;
    private String numeroCuenta;
    private boolean activo;
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    @JsonBackReference
    private Empleado empleado;
}
