package com.jcfp.tallererp.security;

import com.jcfp.tallererp.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmpleadoDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private static final List<String> PARAM_TOKEN_ENDPOINTS = Arrays.asList(
            "/export", "/template", "/test/token"
    );

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/",
            "/index.html",
            "/auth",
            "/actuator/health",
            "/favicon.ico",
            "/static",
            "/assets",
            "/icons",
            "/css",
            "/js",
            "/img"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        boolean shouldSkip = PUBLIC_ENDPOINTS.stream().anyMatch(publicPath ->
                path.equals(publicPath) || path.startsWith(publicPath + "/")
        );

        logger.info("=== FILTRO CHECK: {} - Saltar: {} ===", path, shouldSkip);

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getDniFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Usuario autenticado: {} para endpoint: {}", username, request.getRequestURI());
            } else {
                logger.warn("Token JWT inválido o no encontrado para: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("No se puede establecer la autenticación del usuario: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String requestPath = request.getRequestURI();

        boolean isCsvEndpoint = PARAM_TOKEN_ENDPOINTS.stream()
                .anyMatch(requestPath::contains);

        if (isCsvEndpoint) {
            String paramToken = request.getParameter("token");
            if (StringUtils.hasText(paramToken)) {
                logger.info("Token encontrado en parámetro para endpoint CSV: {}", requestPath);
                return paramToken;
            }
        }

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.info("Token encontrado en header Authorization para: {}", requestPath);
            return token;
        }

        logger.debug("No se encontró token JWT válido para: {}", requestPath);
        return null;
    }}