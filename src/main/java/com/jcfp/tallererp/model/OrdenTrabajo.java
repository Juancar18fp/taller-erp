package com.jcfp.tallererp.model;

import com.jcfp.tallererp.util.EstadoOrden;
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

}
