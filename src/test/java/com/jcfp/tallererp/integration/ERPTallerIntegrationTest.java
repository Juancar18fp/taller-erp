package com.jcfp.tallererp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcfp.tallererp.dto.LoginRequest;
import com.jcfp.tallererp.entity.*;
import com.jcfp.tallererp.repository.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.defer-datasource-initialization=false",
        "logging.level.org.springframework.test=INFO",
        "logging.level.org.hibernate.SQL=INFO"
})
public class ERPTallerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ERPTallerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private OrdenTrabajoRepository ordenTrabajoRepository;

    @Autowired
    private ArticuloRepository articuloRepository;


    @Autowired
    private EstadoOrdenRepository estadoOrdenRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private TipoContratoRepository tipoContratoRepository;

    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Long clienteId;
    private Long vehiculoId;
    private Long estadoOrdenId;

    @BeforeAll
    static void setUpClass() {
        log.info("=== INICIALIZANDO SUITE DE TESTS INTEGRALES ===");
    }

    @BeforeEach
    void setUp() {
        log.info("=== CONFIGURANDO TEST INDIVIDUAL ===");

        try {
            recuperarDatosExistentes();

            if (estadoOrdenId == null || clienteId == null || vehiculoId == null) {
                log.info("Creando datos maestros faltantes...");
                crearDatosMaestrosCompletos();
                crearEmpleadosConContratos();
                log.info("Datos maestros completados exitosamente");
            } else {
                log.info("Reutilizando datos existentes: Cliente={}, Vehiculo={}, Estado={}",
                        clienteId, vehiculoId, estadoOrdenId);
            }

        } catch (Exception e) {
            log.error("Error en setUp: ", e);
            throw new RuntimeException("Error configurando test", e);
        }
    }

    @Test
    @Order(1)
    void deberiaAutenticarseConJWTRealYAccederConRoles() throws Exception {
        log.info("=== TEST 1: AUTENTICACIÓN JWT + AUTORIZACIÓN ===");

        log.info("Intentando login como ADMINISTRADOR...");
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setDni("12345678A");
        adminLogin.setPassword("admin123");

        MvcResult loginResult = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rol.nombre").value("ADMINISTRADOR"))
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();
        log.info("✓ Token ADMIN obtenido correctamente");

        log.info("Intentando login como TÉCNICO...");
        LoginRequest tecnicoLogin = new LoginRequest();
        tecnicoLogin.setDni("87654321B");
        tecnicoLogin.setPassword("tecnico123");

        MvcResult tecnicoResult = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tecnicoLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol.nombre").value("TECNICO"))
                .andReturn();

        String tecnicoResponse = tecnicoResult.getResponse().getContentAsString();
        String tokenTecnico = objectMapper.readTree(tecnicoResponse).get("token").asText();
        log.info("✓ Token TECNICO obtenido correctamente");

        log.info("Verificando permisos ADMINISTRADOR...");
        mockMvc.perform(get("/ordenes")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
        log.info("✓ ADMIN accede a órdenes correctamente");

        log.info("Verificando permisos limitados TÉCNICO...");
        mockMvc.perform(get("/ordenes")
                        .header("Authorization", "Bearer " + tokenTecnico))
                .andExpect(status().isOk());
        log.info("✓ TÉCNICO accede a órdenes correctamente");

        log.info("=== TEST 1 COMPLETADO EXITOSAMENTE ===");
    }

    @Test
    @Order(2)
    @Transactional
    void deberiaCrearOrdenTrabajoCompletaConRelacionesReales() throws Exception {
        log.info("=== TEST 2: ORDEN TRABAJO COMPLETA ===");

        if (tokenAdmin == null) {
            log.info("Obteniendo token para test...");
            LoginRequest adminLogin = new LoginRequest();
            adminLogin.setDni("12345678A");
            adminLogin.setPassword("admin123");

            MvcResult loginResult = mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminLogin)))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = loginResult.getResponse().getContentAsString();
            tokenAdmin = objectMapper.readTree(response).get("token").asText();
        }

        log.info("Creando orden trabajo...");
        OrdenTrabajo nuevaOrden = new OrdenTrabajo();
        nuevaOrden.setCodigoOrden("OT-" + System.currentTimeMillis());
        nuevaOrden.setObservaciones("Cambio aceite y filtros - TEST");
        nuevaOrden.setFechaOrden(LocalDate.now());
        nuevaOrden.setPagada(false);
        nuevaOrden.setTotal(150.50);

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow();
        EstadoOrden estadoOrden = estadoOrdenRepository.findById(estadoOrdenId).orElseThrow();

        nuevaOrden.setVehiculo(vehiculo);
        nuevaOrden.setEstadoOrden(estadoOrden);
        nuevaOrden.setArticulosUsados(new ArrayList<>());

        MvcResult createResult = mockMvc.perform(post("/ordenes")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaOrden)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        Long ordenId = objectMapper.readTree(createResponse).get("id").asLong();
        log.info("✓ Orden creada con ID: {}", ordenId);

        OrdenTrabajo ordenPersistida = ordenTrabajoRepository.findById(ordenId).orElseThrow();
        assertThat(ordenPersistida.getVehiculo().getId()).isEqualTo(vehiculoId);
        assertThat(ordenPersistida.getTotal()).isEqualTo(150.50);

        log.info("=== TEST 2 COMPLETADO EXITOSAMENTE ===");
    }

    @Test
    @Order(3)
    void deberiaManejarErroresYValidacionesReales() throws Exception {
        log.info("=== TEST 3: MANEJO DE ERRORES ===");

        if (tokenAdmin == null) {
            LoginRequest adminLogin = new LoginRequest();
            adminLogin.setDni("12345678A");
            adminLogin.setPassword("admin123");

            MvcResult loginResult = mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminLogin)))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = loginResult.getResponse().getContentAsString();
            tokenAdmin = objectMapper.readTree(response).get("token").asText();
        }

        log.info("Probando validaciones...");

        mockMvc.perform(post("/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        log.info("✓ Validación de autenticación funcionando");

        OrdenTrabajo ordenMinima = new OrdenTrabajo();
        ordenMinima.setArticulosUsados(new ArrayList<>());
        ordenMinima.setPagada(false);
        ordenMinima.setTotal(0.0);

        mockMvc.perform(post("/ordenes")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenMinima)))
                .andExpect(status().isCreated());
        log.info("✓ Creación de orden con datos mínimos funcionando");

        mockMvc.perform(get("/ordenes/99999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
        log.info("✓ Manejo de errores 404 funcionando");

        log.info("=== TEST 3 COMPLETADO EXITOSAMENTE ===");
    }


    private void recuperarDatosExistentes() {
        if (estadoOrdenRepository.count() > 0 && estadoOrdenId == null) {
            estadoOrdenId = estadoOrdenRepository.findAll().get(0).getId();
            log.info("Estado orden recuperado: {}", estadoOrdenId);
        }

        if (clienteRepository.count() > 0 && clienteId == null) {
            clienteId = clienteRepository.findAll().get(0).getId();
            log.info("Cliente recuperado: {}", clienteId);
        }

        if (vehiculoRepository.count() > 0 && vehiculoId == null) {
            vehiculoId = vehiculoRepository.findAll().get(0).getId();
            log.info("Vehículo recuperado: {}", vehiculoId);
        }
    }

    private void crearDatosMaestrosCompletos() {
        log.info("Iniciando creación de datos maestros...");

        if (estadoOrdenRepository.count() == 0) {
            EstadoOrden pendiente = new EstadoOrden();
            pendiente.setNombre("PENDIENTE");
            estadoOrdenRepository.save(pendiente);
            estadoOrdenId = pendiente.getId();

            EstadoOrden enProgreso = new EstadoOrden();
            enProgreso.setNombre("EN_PROGRESO");
            estadoOrdenRepository.save(enProgreso);

            EstadoOrden completada = new EstadoOrden();
            completada.setNombre("COMPLETADA");
            estadoOrdenRepository.save(completada);

            log.info("Estados de orden creados");
        }

        Rol adminRole = rolRepository.findByNombre("ADMINISTRADOR")
                .orElseGet(() -> {
                    Rol newRole = new Rol();
                    newRole.setNombre("ADMINISTRADOR");
                    return rolRepository.save(newRole);
                });

        Rol tecnicoRole = rolRepository.findByNombre("TECNICO")
                .orElseGet(() -> {
                    Rol newRole = new Rol();
                    newRole.setNombre("TECNICO");
                    return rolRepository.save(newRole);
                });

        Rol recepcionistaRole = rolRepository.findByNombre("RECEPCIONISTA")
                .orElseGet(() -> {
                    Rol newRole = new Rol();
                    newRole.setNombre("RECEPCIONISTA");
                    return rolRepository.save(newRole);
                });

        rolRepository.flush();
        log.info("✓ Roles verificados/creados");

        List<Puesto> puestosExistentes = puestoRepository.findAll();
        log.info("Puestos existentes al inicio: {}", puestosExistentes.size());
        puestosExistentes.forEach(p ->
                log.info("  - Puesto existente: {} | Rol: {}", p.getNombre(), p.getRol().getNombre())
        );

        boolean existeAdmin = puestosExistentes.stream()
                .anyMatch(p -> "ADMINISTRADOR".equals(p.getRol().getNombre()));

        if (!existeAdmin) {
            Puesto puestoAdmin = new Puesto();
            puestoAdmin.setNombre("Administrador");
            puestoAdmin.setRol(adminRole);
            puestoRepository.save(puestoAdmin);
            log.info("✓ Puesto ADMINISTRADOR creado");
        }

        boolean existeTecnico = puestosExistentes.stream()
                .anyMatch(p -> "TECNICO".equals(p.getRol().getNombre()));

        if (!existeTecnico) {
            Puesto puestoTecnico = new Puesto();
            puestoTecnico.setNombre("Técnico Automotriz");
            puestoTecnico.setRol(tecnicoRole);
            puestoRepository.save(puestoTecnico);
            log.info("✓ Puesto TECNICO creado");
        }

        boolean existeRecepcionista = puestosExistentes.stream()
                .anyMatch(p -> "RECEPCIONISTA".equals(p.getRol().getNombre()));

        if (!existeRecepcionista) {
            Puesto puestoRecepcionista = new Puesto();
            puestoRecepcionista.setNombre("Recepcionista");
            puestoRecepcionista.setRol(recepcionistaRole);
            puestoRepository.save(puestoRecepcionista);
            log.info("✓ Puesto RECEPCIONISTA creado");
        }

        puestoRepository.flush();

        List<Puesto> puestosFinal = puestoRepository.findAll();
        log.info("✓ Puestos verificados/creados - Total: {}", puestosFinal.size());
        puestosFinal.forEach(p ->
                log.info("  - Puesto final: {} | Rol: {}", p.getNombre(), p.getRol().getNombre())
        );

        if (tipoContratoRepository.count() == 0) {
            TipoContrato indefinido = new TipoContrato();
            indefinido.setNombre("INDEFINIDO");
            tipoContratoRepository.save(indefinido);

            TipoContrato temporal = new TipoContrato();
            temporal.setNombre("TEMPORAL");
            tipoContratoRepository.save(temporal);

            log.info("Tipos de contrato creados");
        }

        if (jornadaLaboralRepository.count() == 0) {
            JornadaLaboral completa = new JornadaLaboral();
            completa.setNombre("COMPLETA");
            jornadaLaboralRepository.save(completa);

            JornadaLaboral parcial = new JornadaLaboral();
            parcial.setNombre("PARCIAL");
            jornadaLaboralRepository.save(parcial);

            log.info("Jornadas laborales creadas");
        }

        if (clienteRepository.count() == 0) {
            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente Test Integral");
            cliente.setDocumento("11111111A");
            cliente.setTelefono("666666666");
            cliente.setEmail("cliente@test.com");
            cliente.setDireccion("Calle Test 123");
            cliente.setCp("28001");
            cliente.setPoblacion("Madrid");
            cliente.setProvincia("Madrid");
            cliente.setPais("España");
            cliente = clienteRepository.save(cliente);
            clienteId = cliente.getId();

            log.info("✓ Cliente creado con ID: {}", clienteId);
        }

        Marca toyota;
        if (marcaRepository.count() == 0) {
            toyota = new Marca();
            toyota.setNombre("Toyota");
            marcaRepository.save(toyota);

            Modelo corolla = new Modelo();
            corolla.setNombre("Corolla");
            corolla.setMarca(toyota);
            modeloRepository.save(corolla);

            log.info("Marcas y modelos creados");
        } else {
            toyota = marcaRepository.findAll().get(0);
        }

        if (vehiculoRepository.count() == 0) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setMatricula("1234ABC");
            vehiculo.setMatriculacion(2020);
            vehiculo.setCliente(clienteRepository.findById(clienteId).orElseThrow());
            vehiculo.setMarca(toyota);
            vehiculo.setModelo(modeloRepository.findAll().get(0));
            vehiculo = vehiculoRepository.save(vehiculo);
            vehiculoId = vehiculo.getId();

            log.info("Vehículo creado con ID: {}", vehiculoId);
        }

        if (articuloRepository.count() == 0) {
            Articulo aceite = new Articulo();
            aceite.setDescripcion("Aceite Motor 5W30 - Test");
            aceite.setPrecio(25.50);
            aceite.setStock(50);
            aceite.setProveedor("Repsol Test");
            articuloRepository.save(aceite);

            Articulo filtro = new Articulo();
            filtro.setDescripcion("Filtro de Aceite - Test");
            filtro.setPrecio(12.50);
            filtro.setStock(30);
            filtro.setProveedor("Mann Filter Test");
            articuloRepository.save(filtro);

            log.info("✓ Artículos creados");
        }

        log.info("Datos maestros completados exitosamente");
    }

    private void crearEmpleadosConContratos() {
        log.info("Creando empleados con contratos...");

        List<Puesto> todosPuestos = puestoRepository.findAll();
        log.info("Puestos disponibles antes de crear empleados: {}", todosPuestos.size());
        todosPuestos.forEach(p ->
                log.info("  - ID: {}, Nombre: {}, Rol: {}",
                        p.getId(), p.getNombre(), p.getRol().getNombre())
        );

        if (empleadoRepository.existsByDocumento("12345678A") &&
                empleadoRepository.existsByDocumento("87654321B")) {
            log.info("Empleados ya existen, saltando creación");
            return;
        }

        // Empleado ADMINISTRADOR
        if (!empleadoRepository.existsByDocumento("12345678A")) {
            log.info("Creando empleado ADMINISTRADOR...");

            Empleado admin = new Empleado();
            admin.setDocumento("12345678A");
            admin.setNombre("Admin Test");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setTelefono("666111222");
            admin.setFechaNacimiento(LocalDate.of(1985, 3, 15));
            admin.setDireccion("Calle Admin 1");
            admin.setCp("28002");
            admin.setPoblacion("Madrid");
            admin.setProvincia("Madrid");
            admin.setPais("España");
            admin.setContratos(new ArrayList<>());
            admin = empleadoRepository.save(admin);

            Puesto puestoAdmin = puestoRepository.findAll().stream()
                    .filter(p -> "ADMINISTRADOR".equals(p.getRol().getNombre()))
                    .findFirst()
                    .orElseThrow(() -> {
                        String disponibles = todosPuestos.stream()
                                .map(p -> p.getNombre() + "(" + p.getRol().getNombre() + ")")
                                .collect(Collectors.joining(", "));
                        return new RuntimeException(
                                "No se encontró puesto ADMINISTRADOR. Disponibles: " + disponibles
                        );
                    });

            TipoContrato tipoContrato = tipoContratoRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró tipo de contrato"));

            JornadaLaboral jornada = jornadaLaboralRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró jornada laboral"));

            Contrato contratoAdmin = new Contrato();
            contratoAdmin.setEmpleado(admin);
            contratoAdmin.setFechaContratacion(LocalDate.now().minusYears(2));
            contratoAdmin.setSalario(45000);
            contratoAdmin.setNumeroCuenta("ES1111111111111111111111");
            contratoAdmin.setActivo(true);
            contratoAdmin.setPuesto(puestoAdmin);
            contratoAdmin.setTipoContrato(tipoContrato);
            contratoAdmin.setJornadaLaboral(jornada);
            contratoRepository.save(contratoAdmin);

            log.info("✓ Empleado ADMIN creado con puesto: {}", puestoAdmin.getNombre());
        }

        // Empleado TÉCNICO
        if (!empleadoRepository.existsByDocumento("87654321B")) {
            log.info("Creando empleado TÉCNICO...");

            Empleado tecnico = new Empleado();
            tecnico.setDocumento("87654321B");
            tecnico.setNombre("Técnico Test");
            tecnico.setEmail("tecnico@test.com");
            tecnico.setPassword(passwordEncoder.encode("tecnico123"));
            tecnico.setTelefono("666222333");
            tecnico.setFechaNacimiento(LocalDate.of(1990, 7, 22));
            tecnico.setDireccion("Calle Técnico 2");
            tecnico.setCp("28003");
            tecnico.setPoblacion("Madrid");
            tecnico.setProvincia("Madrid");
            tecnico.setPais("España");
            tecnico.setContratos(new ArrayList<>());
            tecnico = empleadoRepository.save(tecnico);

            log.info("Buscando puesto TECNICO...");
            Puesto puestoTecnico = puestoRepository.findAll().stream()
                    .peek(p -> log.info("Evaluando puesto: {} con rol: {}",
                            p.getNombre(), p.getRol().getNombre()))
                    .filter(p -> "TECNICO".equals(p.getRol().getNombre()))
                    .findFirst()
                    .orElseThrow(() -> {
                        String disponibles = todosPuestos.stream()
                                .map(p -> p.getNombre() + "(" + p.getRol().getNombre() + ")")
                                .collect(Collectors.joining(", "));
                        return new RuntimeException(
                                "No se encontró puesto TECNICO. Disponibles: " + disponibles
                        );
                    });

            TipoContrato tipoContrato = tipoContratoRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró tipo de contrato"));

            JornadaLaboral jornada = jornadaLaboralRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró jornada laboral"));

            Contrato contratoTecnico = new Contrato();
            contratoTecnico.setEmpleado(tecnico);
            contratoTecnico.setFechaContratacion(LocalDate.now().minusYears(1));
            contratoTecnico.setSalario(30000);
            contratoTecnico.setNumeroCuenta("ES2222222222222222222222");
            contratoTecnico.setActivo(true);
            contratoTecnico.setPuesto(puestoTecnico);
            contratoTecnico.setTipoContrato(tipoContrato);
            contratoTecnico.setJornadaLaboral(jornada);
            contratoRepository.save(contratoTecnico);

            log.info("✓ Empleado TÉCNICO creado con puesto: {}", puestoTecnico.getNombre());
        }

        log.info("Empleados con contratos completados exitosamente");
    }
}