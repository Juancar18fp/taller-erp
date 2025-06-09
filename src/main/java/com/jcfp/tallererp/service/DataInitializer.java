package com.jcfp.tallererp.service;

import com.jcfp.tallererp.entity.*;
import com.jcfp.tallererp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PuestoRepository puestoRepository;
    private final ContratoRepository contratoRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final JornadaLaboralRepository jornadaLaboralRepository;
    private final EstadoCivilRepository estadoCivilRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            PuestoRepository puestoRepository,
            ContratoRepository contratoRepository,
            TipoContratoRepository tipoContratoRepository,
            JornadaLaboralRepository jornadaLaboralRepository,
            EstadoCivilRepository estadoCivilRepository,
            PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.puestoRepository = puestoRepository;
        this.contratoRepository = contratoRepository;
        this.tipoContratoRepository = tipoContratoRepository;
        this.jornadaLaboralRepository = jornadaLaboralRepository;
        this.estadoCivilRepository = estadoCivilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearDatosBasicos();
        crearRoles();
        crearPuestos();
        crearEmpleadosEjemplo();
    }

    private void crearDatosBasicos() {
        String[] estadosCiviles = {"Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a"};
        for (String nombre : estadosCiviles) {
            if (!estadoCivilRepository.existsByNombre(nombre)) {
                EstadoCivil estado = new EstadoCivil();
                estado.setNombre(nombre);
                estadoCivilRepository.save(estado);
                System.out.println("Estado civil creado: " + nombre);
            }
        }

        String[] tiposContrato = {"Indefinido", "Temporal", "Prácticas", "Formación"};
        for (String nombre : tiposContrato) {
            if (!tipoContratoRepository.existsByNombre(nombre)) {
                TipoContrato tipo = new TipoContrato();
                tipo.setNombre(nombre);
                tipoContratoRepository.save(tipo);
                System.out.println("Tipo de contrato creado: " + nombre);
            }
        }

        String[] jornadas = {"Completa", "Parcial", "Reducida", "Intensiva"};
        for (String nombre : jornadas) {
            if (!jornadaLaboralRepository.existsByNombre(nombre)) {
                JornadaLaboral jornada = new JornadaLaboral();
                jornada.setNombre(nombre);
                jornadaLaboralRepository.save(jornada);
                System.out.println("Jornada laboral creada: " + nombre);
            }
        }
    }

    private void crearRoles() {
        String[] nombreRoles = {"ADMINISTRADOR", "TECNICO", "RECEPCIONISTA", "ALMACEN"};
        for (String nombreRol : nombreRoles) {
            if (!rolRepository.existsByNombre(nombreRol)) {
                Rol rol = new Rol();
                rol.setNombre(nombreRol);
                rolRepository.save(rol);
                System.out.println("Rol creado: " + nombreRol);
            }
        }
    }

    private void crearPuestos() {
        crearPuestoConRol();
    }

    private void crearPuestoConRol() {
        if (!puestoRepository.existsByNombre("Administrador del Sistema")) {
            Rol rol = rolRepository.findByNombre("ADMINISTRADOR")
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + "ADMINISTRADOR"));

            Puesto puesto = new Puesto();
            puesto.setNombre("Administrador del Sistema");
            puesto.setRol(rol);
            puestoRepository.save(puesto);
            System.out.println("Puesto creado: " + "Administrador del Sistema" + " con rol " + "ADMINISTRADOR");
        }
    }


    private void crearEmpleadosEjemplo() {
        if (!empleadoRepository.existsByDocumento("admin") && empleadoRepository.countBy() == 0) {
            Empleado admin = new Empleado();
            admin.setDocumento("admin");
            admin.setNombre("Administrador");
            admin.setTelefono("666555444");
            admin.setCp("28001");
            EstadoCivil estadoCivil = estadoCivilRepository.findByNombre("Soltero/a")
                    .orElse(null);
            admin.setEstadoCivil(estadoCivil);
            admin.setPassword(passwordEncoder.encode("admin123"));
            Empleado empleadoGuardado = empleadoRepository.save(admin);
            crearContratoParaEmpleado(empleadoGuardado);
            System.out.println("Usuario administrador creado: DNI=admin, Password=admin123");
        }

    }

    private void crearContratoParaEmpleado(Empleado empleado) {
        Puesto puesto = puestoRepository.findByNombre("Administrador del Sistema")
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado: " + "Administrador del Sistema"));

        TipoContrato tipoContrato = tipoContratoRepository.findByNombre("Indefinido")
                .orElseThrow(() -> new RuntimeException("Tipo de contrato no encontrado"));

        JornadaLaboral jornada = jornadaLaboralRepository.findByNombre("Completa")
                .orElseThrow(() -> new RuntimeException("Jornada laboral no encontrada"));

        Contrato contrato = new Contrato();
        contrato.setEmpleado(empleado);
        contrato.setPuesto(puesto);
        contrato.setTipoContrato(tipoContrato);
        contrato.setJornadaLaboral(jornada);
        contrato.setFechaContratacion(LocalDate.now());
        contrato.setSalario(50000);
        contrato.setActivo(true);
        contrato.setNumeroCuenta("ES1234567890123456789012");

        contratoRepository.save(contrato);
        System.out.println("Contrato creado para " + empleado.getNombre() + " en puesto " + "Administrador del Sistema");
    }
}