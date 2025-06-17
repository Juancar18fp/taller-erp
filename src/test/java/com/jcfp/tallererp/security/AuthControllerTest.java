package com.jcfp.tallererp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcfp.tallererp.dto.LoginRequest;
import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private EmpleadoDetailsServiceImpl empleadoDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoPrincipal empleadoPrincipalAdmin;
    private EmpleadoPrincipal empleadoPrincipalTecnico;
    private Authentication authenticationMock;

    @BeforeEach
    void setUp() {
        log.info("=== CONFIGURANDO TESTS DE AUTENTICACIÓN Y SEGURIDAD ===");
        log.info("Preparando MockMvc, AuthManager y datos de prueba para tests JWT");

        Rol rolAdmin = new Rol("ADMINISTRADOR");
        rolAdmin.setId(1L);

        empleadoPrincipalAdmin = new EmpleadoPrincipal(
                1L,
                "12345678A",
                "Juan Pérez",
                "admin@tallererp.com",
                "admin123",
                rolAdmin,
                true
        );
        log.info("Mock ADMINISTRADOR configurado - DNI: {}, Rol: {}",
                empleadoPrincipalAdmin.getUsuario(), rolAdmin.getNombre());

        Rol rolTecnico = new Rol("TECNICO");
        rolTecnico.setId(2L);

        empleadoPrincipalTecnico = new EmpleadoPrincipal(
                2L,                      // id
                "87654321B",            // usuario (DNI)
                "Carlos López",         // nombre
                "carlos@tallererp.com", // email
                "tecnico123",           // password
                rolTecnico,             // rol
                true                    // activo
        );
        log.info("Mock TÉCNICO configurado - DNI: {}, Rol: {}",
                empleadoPrincipalTecnico.getUsuario(), rolTecnico.getNombre());

        authenticationMock = mock(Authentication.class);

        log.info("Setup completado - Authentication mock creado");
        log.info("Tests configurados con simulación de login JWT");
        log.info("==========================================");
    }

    @Test
    @WithMockUser
    void deberiaAutenticarAdministradorCorrectamente() throws Exception {
        log.info("TEST 1: LOGIN EXITOSO CON ROL ADMINISTRADOR");
        log.info("Usuario simulado: admin con DNI 12345678A");
        log.info("Probando: /auth/signin con credenciales válidas");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setDni("12345678A");
        loginRequest.setPassword("admin123");
        log.info("LoginRequest creado - DNI: {}, Password: [HIDDEN]", loginRequest.getDni());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(empleadoPrincipalAdmin);
        when(jwtUtils.generateJwtToken(authenticationMock)).thenReturn("jwt-token-admin-valido");
        log.info("Mocks configurados - AuthManager retornará admin principal");
        log.info("JWT generado simulado: jwt-token-admin-valido");

        log.info("EJECUTANDO: POST /auth/signin con credenciales de administrador");
        mockMvc.perform(post("/auth/signin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.token").value("jwt-token-admin-valido"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.empleadoId").value(1))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("admin@tallererp.com"))

                .andExpect(jsonPath("$.rol.id").value(1))
                .andExpect(jsonPath("$.rol.nombre").value("ADMINISTRADOR"))

                .andExpect(jsonPath("$.permisos").isArray())
                .andExpect(jsonPath("$.permisos.length()").value(6))
                .andExpect(jsonPath("$.permisos[0]").value("ordenes"))
                .andExpect(jsonPath("$.permisos[1]").value("clientes"))
                .andExpect(jsonPath("$.permisos[2]").value("vehiculos"))
                .andExpect(jsonPath("$.permisos[3]").value("articulos"))
                .andExpect(jsonPath("$.permisos[4]").value("empleados"))
                .andExpect(jsonPath("$.permisos[5]").value("configuracion"));

        log.info("LOGIN EXITOSO - Token JWT generado correctamente");
        log.info("Rol verificado: ADMINISTRADOR con 6 permisos completos");
        log.info("Response JSON validado - todos los campos presentes");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authenticationMock);
        log.info("Mocks verificados - AuthManager y JwtUtils llamados correctamente");
        log.info("TEST 1 COMPLETADO - Login de administrador funcionando");
    }

    @Test
    @WithMockUser
    void deberiaAutenticarTecnicoConPermisosLimitados() throws Exception {
        log.info("TEST 2: LOGIN EXITOSO CON ROL TÉCNICO");
        log.info("Usuario simulado: tecnico con DNI 87654321B");
        log.info("Probando: /auth/signin con credenciales técnico");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setDni("87654321B");
        loginRequest.setPassword("tecnico123");
        log.info("LoginRequest técnico creado - DNI: {}", loginRequest.getDni());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(empleadoPrincipalTecnico);
        when(jwtUtils.generateJwtToken(authenticationMock)).thenReturn("jwt-token-tecnico-valido");
        log.info("Mocks configurados - AuthManager retornará técnico principal");
        log.info("JWT técnico simulado: jwt-token-tecnico-valido");

        log.info("EJECUTANDO: POST /auth/signin con credenciales de técnico");
        mockMvc.perform(post("/auth/signin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.token").value("jwt-token-tecnico-valido"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.empleadoId").value(2))
                .andExpect(jsonPath("$.dni").value("87654321B"))
                .andExpect(jsonPath("$.nombre").value("Carlos López"))
                .andExpect(jsonPath("$.email").value("carlos@tallererp.com"))
                .andExpect(jsonPath("$.rol.id").value(2))
                .andExpect(jsonPath("$.rol.nombre").value("TECNICO"))

                .andExpect(jsonPath("$.permisos").isArray())
                .andExpect(jsonPath("$.permisos.length()").value(1))
                .andExpect(jsonPath("$.permisos[0]").value("ordenes"));

        log.info("LOGIN TÉCNICO EXITOSO - Token JWT generado correctamente");
        log.info("Rol verificado: TÉCNICO con permisos limitados (solo ordenes)");
        log.info("Diferencia confirmada: Admin=6 permisos, Técnico=1 permiso");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authenticationMock);
        log.info("Mocks verificados - Flujo de autenticación técnico correcto");
        log.info("TEST 2 COMPLETADO - Login técnico con permisos restringidos OK");
    }

    @Test
    @WithMockUser
    void deberiaRechazarCredencialesInvalidas() throws Exception {
        log.info("TEST 3: MANEJO DE CREDENCIALES INVÁLIDAS");
        log.info("Usuario simulado: usuario inexistente");
        log.info("Probando: /auth/signin con credenciales erróneas");

        LoginRequest loginRequestInvalido = new LoginRequest();
        loginRequestInvalido.setDni("99999999Z");
        loginRequestInvalido.setPassword("password-incorrecto");
        log.info("LoginRequest inválido creado - DNI: {}, Password: [INVALID]",
                loginRequestInvalido.getDni());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));
        log.info("Mock configurado para lanzar BadCredentialsException");

        log.info("EJECUTANDO: POST /auth/signin con credenciales INCORRECTAS");
        mockMvc.perform(post("/auth/signin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("{\"error\": \"Credenciales inválidas\"}"));

        log.info("ERROR MANEJADO CORRECTAMENTE - Status 400 Bad Request");
        log.info("Response type: text/plain con mensaje de error JSON");
        log.info("Seguridad funcionando: credenciales incorrectas rechazadas");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any());
        log.info("Mocks verificados - AuthManager llamado, JWT NO generado");
        log.info("TEST 3 COMPLETADO - Manejo de errores de autenticación OK");
    }

    @Test
    @WithMockUser
    void deberiaRealizarLogoutCorrectamente() throws Exception {
        log.info("TEST 4: PROCESO DE LOGOUT/CIERRE DE SESIÓN");
        log.info("Usuario simulado: usuario autenticado");
        log.info("Probando: /auth/signout para cerrar sesión");

        log.info("EJECUTANDO: POST /auth/signout");
        mockMvc.perform(post("/auth/signout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("{\"message\": \"Sesión cerrada exitosamente\"}"));

        log.info("LOGOUT EXITOSO - Status 200 OK");
        log.info("Response type: text/plain con mensaje de confirmación");
        log.info("Sesión cerrada correctamente - mensaje JSON confirmado");
        log.info("TEST 4 COMPLETADO - Proceso de logout funcionando");
    }
}