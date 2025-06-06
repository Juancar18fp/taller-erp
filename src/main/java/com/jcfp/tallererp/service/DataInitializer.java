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
        String[] estadosCiviles = {"Soltero", "Casado", "Divorciado", "Viudo"};
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
        crearPuestoConRol("Administrador del Sistema", "ADMINISTRADOR");
        crearPuestoConRol("Mecánico Senior", "TECNICO");
        crearPuestoConRol("Mecánico Junior", "TECNICO");
        crearPuestoConRol("Recepcionista", "RECEPCIONISTA");
        crearPuestoConRol("Encargado de Almacén", "ALMACEN");
    }

    private void crearPuestoConRol(String nombrePuesto, String nombreRol) {
        if (!puestoRepository.existsByNombre(nombrePuesto)) {
            Rol rol = rolRepository.findByNombre(nombreRol)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

            Puesto puesto = new Puesto();
            puesto.setNombre(nombrePuesto);
            puesto.setRol(rol);
            puestoRepository.save(puesto);
            System.out.println("Puesto creado: " + nombrePuesto + " con rol " + nombreRol);
        }
    }


    private void crearEmpleadosEjemplo() {
        if (!empleadoRepository.existsByDocumento("admin")) {
            Empleado admin = new Empleado();
            admin.setDocumento("admin");
            admin.setNombre("Juan Pérez Administrador");
            admin.setEmail("admin@tallerpro.com");
            admin.setTelefono("666555444");
            admin.setFechaNacimiento(LocalDate.of(1985, 5, 15));
            admin.setDireccion("Calle Principal 123");
            admin.setCp("28001");
            admin.setPoblacion("Madrid");
            admin.setProvincia("Madrid");
            admin.setPais("España");
            EstadoCivil estadoCivil = estadoCivilRepository.findByNombre("Soltero")
                    .orElse(null);
            admin.setEstadoCivil(estadoCivil);
            admin.setPassword(passwordEncoder.encode("admin123"));

            Empleado empleadoGuardado = empleadoRepository.save(admin);
            crearContratoParaEmpleado(empleadoGuardado, "Administrador del Sistema", 50000);
            System.out.println("Usuario administrador creado: DNI=admin, Password=admin123");
        }

        if (!empleadoRepository.existsByDocumento("87654321B")) {
            Empleado tecnico = new Empleado();
            tecnico.setDocumento("87654321B");
            tecnico.setNombre("María García Técnico");
            tecnico.setEmail("tecnico@tallerpro.com");
            tecnico.setTelefono("666777888");
            tecnico.setFechaNacimiento(LocalDate.of(1990, 8, 20));
            tecnico.setPassword(passwordEncoder.encode("tecnico123"));

            EstadoCivil estadoCivil = estadoCivilRepository.findByNombre("Casado")
                    .orElse(null);
            tecnico.setEstadoCivil(estadoCivil);

            Empleado empleadoGuardado = empleadoRepository.save(tecnico);

            crearContratoParaEmpleado(empleadoGuardado, "Mecánico Senior", 35000);

            System.out.println("Usuario técnico creado: DNI=87654321B, Password=tecnico123");
        }
    }

    private void crearContratoParaEmpleado(Empleado empleado, String nombrePuesto, int salario) {
        Puesto puesto = puestoRepository.findByNombre(nombrePuesto)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado: " + nombrePuesto));

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
        contrato.setSalario(salario);
        contrato.setActivo(true);
        contrato.setNumeroCuenta("ES1234567890123456789012");

        contratoRepository.save(contrato);
        System.out.println("Contrato creado para " + empleado.getNombre() + " en puesto " + nombrePuesto);
    }
}