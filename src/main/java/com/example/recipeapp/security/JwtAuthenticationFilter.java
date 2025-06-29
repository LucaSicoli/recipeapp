package com.example.recipeapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * NO filtrar:
     *  - /api/auth/**
     *  - /images/**
     *  - GET a /recipes/**
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // auth y estáticas
        if (path.startsWith("/api/auth/") || path.startsWith("/images/")) {
            return true;
        }

        // GET públicas de recetas
        if ("GET".equalsIgnoreCase(method)) {
            if (path.equals("/recipes") ||
                    path.equals("/recipes/summary") ||
                    path.matches("/recipes/\\d+$") ||
                    path.matches("/recipes/\\d+/full$") ||
                    path.matches("/recipes/\\d+/full$") ||
                    path.startsWith("/recipes/estado/") ) {
                return true;
            }
            // **NO** devolvemos true para /recipes/drafts ni /recipes/saved
        }

        // GET públicas de ratings
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/ratings")) {
            return true;
        }

        // resto: sí filtrar (requieren JWT)
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Si llegamos aquí, es una ruta protegida: buscar JWT
        String jwt = parseJwt(request);
        if (jwt == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"error\": \"No se encontró el token JWT en la petición.\"}");
            return;
        }

        try {
            if (jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Error al validar JWT", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"error\": \"JWT inválido: " + e.getMessage() + "\"}");
            return;
        }

        // Token válido → permitimos continuar
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}