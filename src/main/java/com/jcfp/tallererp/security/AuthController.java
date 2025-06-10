package com.jcfp.tallererp.security;

import com.jcfp.tallererp.dto.LoginRequest;
import com.jcfp.tallererp.dto.LoginResponse;
import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;

    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getDni(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            EmpleadoPrincipal empleadoPrincipal = (EmpleadoPrincipal) authentication.getPrincipal();
            String[] permisos = getPermisosPorRol(empleadoPrincipal.getRol());

            LoginResponse.RolDto rolDto = new LoginResponse.RolDto(
                    empleadoPrincipal.getRol().getId(),
                    empleadoPrincipal.getRol().getNombre()
            );

            return ResponseEntity.ok(new LoginResponse(jwt,
                    empleadoPrincipal.getId(),
                    empleadoPrincipal.getUsuario(),
                    empleadoPrincipal.getNombre(),
                    empleadoPrincipal.getEmail(),
                       rolDto,
                    permisos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Credenciales inválidas\"}");
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("{\"message\": \"Sesión cerrada exitosamente\"}");
    }

    private String[] getPermisosPorRol(Rol rol) {
        if (rol == null) {
            return new String[]{};
        }

        String nombreRol = rol.getNombre();
        return switch (nombreRol) {
            case "ADMINISTRADOR" ->
                    new String[]{"ordenes", "clientes", "vehiculos", "articulos", "empleados", "configuracion"};
            case "TECNICO" -> new String[]{"ordenes"};
            case "RECEPCIONISTA" -> new String[]{"ordenes", "clientes", "vehiculos"};
            case "ALMACEN" -> new String[]{"articulos"};
            default -> new String[]{};
        };
    }
}
