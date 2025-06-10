package com.jcfp.tallererp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcfp.tallererp.entity.OrdenTrabajo;
import com.jcfp.tallererp.security.EmpleadoDetailsServiceImpl;
import com.jcfp.tallererp.service.OrdenTrabajoService;
import com.jcfp.tallererp.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenTrabajoController.class)
public class OrdenTrabajoControllerTest {

    private static final Logger log = LoggerFactory.getLogger(OrdenTrabajoControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdenTrabajoService ordenTrabajoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private EmpleadoDetailsServiceImpl empleadoDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdenTrabajo ordenTrabajoMock;

    @BeforeEach
    void setUp() {
        log.info("=== CONFIGURANDO TESTS DE CONTROLADOR CON SEGURIDAD ===");
        log.info("Preparando MockMvc, seguridad y datos de prueba para tests REST");

        ordenTrabajoMock = new OrdenTrabajo();
        ordenTrabajoMock.setId(1L);
        ordenTrabajoMock.setCodigoOrden("OT-20240315-001");

        log.info("Setup completado - OrdenTrabajo mock creada con ID: {}", ordenTrabajoMock.getId());
        log.info("Tests configurados con simulación de usuarios autenticados");
        log.info("==========================================");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void deberiaGenerarSiguienteNumeroComoAdministrador() throws Exception {
        log.info("TEST 1: ENDPOINT ESPECÍFICO CON ROL ADMINISTRADOR");
        log.info("Usuario simulado: admin con rol ADMINISTRADOR");
        log.info("Probando: /ordenes/siguiente-numero con autenticación");

        log.info("CASO 1: Administrador solicita siguiente número");
        String fechaValida = "2024-03-15";
        int numeroEsperado = 7;

        when(ordenTrabajoService.obtenerSiguienteNumeroOrden(LocalDate.parse(fechaValida)))
                .thenReturn(numeroEsperado);
        log.info("Mock configurado - Fecha: {}, Siguiente número: {}", fechaValida, numeroEsperado);

        mockMvc.perform(get("/ordenes/siguiente-numero")
                        .param("fecha", fechaValida))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.siguienteNumero").value(7))
                .andExpect(jsonPath("$.fecha").value(fechaValida));

        log.info("CASO 1 OK - Administrador accede correctamente");

        log.info("CASO 2: Error de servicio manejado correctamente");
        String fechaProblematica = "2024-01-01";

        when(ordenTrabajoService.obtenerSiguienteNumeroOrden(LocalDate.parse(fechaProblematica)))
                .thenThrow(new RuntimeException("Error simulado de BD"));
        log.info("Mock configurado para lanzar excepción");

        mockMvc.perform(get("/ordenes/siguiente-numero")
                        .param("fecha", fechaProblematica))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al generar número de orden"))
                .andExpect(jsonPath("$.message").value("Error simulado de BD"));

        log.info("CASO 2 OK - Error manejado correctamente con JSON de respuesta");

        verify(ordenTrabajoService).obtenerSiguienteNumeroOrden(LocalDate.parse(fechaValida));
        verify(ordenTrabajoService).obtenerSiguienteNumeroOrden(LocalDate.parse(fechaProblematica));
        log.info("TEST 1 COMPLETADO - Administrador puede usar endpoint específico");
    }

    @Test
    @WithMockUser(username = "tecnico01", authorities = {"ROLE_TECNICO"})
    void deberiaTecnicoAccederAEndpointNumeracion() throws Exception {
        log.info("TEST ADICIONAL: TÉCNICO accede a endpoint de numeración");
        log.info("Usuario simulado: tecnico01 con rol TECNICO");

        String fechaValida = "2024-03-15";
        when(ordenTrabajoService.obtenerSiguienteNumeroOrden(LocalDate.parse(fechaValida)))
                .thenReturn(3);

        mockMvc.perform(get("/ordenes/siguiente-numero")
                        .param("fecha", fechaValida))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.siguienteNumero").value(3));

        log.info("TÉCNICO también puede acceder - Roles múltiples funcionando");
    }

    @Test
    @WithMockUser(username = "recepcionista", authorities = {"ROLE_RECEPCIONISTA"})
    void deberiaRealizarCrudComoRecepcionista() throws Exception {
        log.info("TEST 2: OPERACIONES CRUD CON ROL RECEPCIONISTA");
        log.info("Usuario simulado: recepcionista con rol RECEPCIONISTA");
        log.info("Probando: GET → POST → PUT con autenticación");

        log.info("OPERACIÓN 1: GET /ordenes/1 (Buscar por ID autenticado)");
        when(ordenTrabajoService.findById(1L)).thenReturn(Optional.of(ordenTrabajoMock));
        log.info("Mock configurado para retornar orden existente");

        mockMvc.perform(get("/ordenes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoOrden").value("OT-20240315-001"));

        log.info("GET BY ID exitoso - Recepcionista accede correctamente");

        log.info("OPERACIÓN 2: POST /ordenes (Crear nueva orden autenticado)");
        OrdenTrabajo nuevaOrden = new OrdenTrabajo();
        nuevaOrden.setCodigoOrden("OT-20240315-005");

        OrdenTrabajo ordenCreada = new OrdenTrabajo();
        ordenCreada.setId(5L);
        ordenCreada.setCodigoOrden("OT-20240315-005");

        when(ordenTrabajoService.create(any(OrdenTrabajo.class))).thenReturn(ordenCreada);
        log.info("Mock configurado para CREATE operation");

        mockMvc.perform(post("/ordenes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaOrden)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.codigoOrden").value("OT-20240315-005"));

        log.info("POST CREATE exitoso - Nueva orden creada por recepcionista");

        log.info("OPERACIÓN 3: PUT /ordenes/1 (Actualizar orden autenticado)");
        OrdenTrabajo ordenActualizada = new OrdenTrabajo();
        ordenActualizada.setId(1L);
        ordenActualizada.setCodigoOrden("OT-20240315-001-UPDATED");

        when(ordenTrabajoService.findById(1L)).thenReturn(Optional.of(ordenTrabajoMock));
        when(ordenTrabajoService.update(eq(1L), any(OrdenTrabajo.class))).thenReturn(ordenActualizada);
        log.info("Mock configurado para UPDATE operation");

        mockMvc.perform(put("/ordenes/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoOrden").value("OT-20240315-001-UPDATED"));

        log.info("PUT UPDATE exitoso - Orden actualizada por recepcionista");

        verify(ordenTrabajoService, times(2)).findById(1L);
        verify(ordenTrabajoService).create(any(OrdenTrabajo.class));
        verify(ordenTrabajoService).update(eq(1L), any(OrdenTrabajo.class));

        log.info("Todas las operaciones CRUD autenticadas verificadas");
        log.info("TEST 2 COMPLETADO - CRUD con seguridad funciona perfectamente");
    }



    @Test
    void deberiaBloquearAccesoSinAutenticacion() throws Exception {
        log.info("TEST 4: CONTROL DE ACCESO SIN AUTENTICACIÓN");
        log.info("Probando que endpoints requieren autenticación");

        log.info("INTENTO 1: GET /ordenes/1 sin autenticación");

        mockMvc.perform(get("/ordenes/1"))
                .andExpect(status().isUnauthorized());

        log.info("BLOQUEO OK - Status 401 sin autenticación");

        log.info("INTENTO 2: POST /ordenes sin autenticación");
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setCodigoOrden("HACK-ATTEMPT");

        mockMvc.perform(post("/ordenes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orden)))
                .andExpect(status().isUnauthorized());

        log.info("BLOQUEO OK - POST también bloqueado sin auth");

        verify(ordenTrabajoService, never()).findById(any());
        verify(ordenTrabajoService, never()).create(any());

        log.info("SEGURIDAD VERIFICADA - Servicios protegidos correctamente");
        log.info("TEST 3 COMPLETADO - Control de acceso funcionando");
    }
}