package com.jcfp.tallererp.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "empleados")
@Getter
@Setter
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String documento;

    private String direccion;
    private String cp;
    private String poblacion;
    private String provincia;
    private String pais;
    private String email;
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "estado_civil_id")
    private EstadoCivil estadoCivil;
    private String numeroSeguridadSocial;
    @OneToMany(mappedBy = "empleado", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Contrato> contratos = new ArrayList<>();

    @Transient
    public Boolean getActivo() {
        return contratos.stream().anyMatch(Contrato::isActivo);
    }

    @Transient
    public Rol getRol() {
        return contratos.stream()
                .filter(Contrato::isActivo)
                .findFirst()
                .map(contrato -> contrato.getPuesto().getRol())
                .orElse(null);
    }

    @Transient
    public Contrato getContratoActivo() {
        return contratos.stream()
                .filter(Contrato::isActivo)
                .findFirst()
                .orElse(null);
    }
}