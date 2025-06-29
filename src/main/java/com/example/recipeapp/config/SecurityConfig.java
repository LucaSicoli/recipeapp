package com.example.recipeapp.config;

import com.example.recipeapp.security.CustomAuthenticationEntryPoint;
import com.example.recipeapp.security.CustomUserDetailsService;
import com.example.recipeapp.security.JwtAuthenticationFilter;
import com.example.recipeapp.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define el PasswordEncoder; en este caso, uno que no encripta (sólo para pruebas)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // Registra el DaoAuthenticationProvider usando tu UserDetailsService y el encoder definido
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Define el filtro JWT inyectando JwtUtil y tu CustomUserDetailsService mediante constructor
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
    }

    // Configura el SecurityFilterChain, registrando el provider y el filtro JWT
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authProvider,
                                                   JwtAuthenticationFilter jwtFilter,
                                                   CustomAuthenticationEntryPoint entryPoint) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // login / reset siguen públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        // imágenes públicas
                        .requestMatchers("/images/**").permitAll()
                        // permitimos que TODOS (alumnos o visitantes) puedan hacer GET a /recipes/**
                        .requestMatchers(HttpMethod.GET,
                                "/recipes",                    // listado general
                                "/recipes/summary",            // resumen home
                                "/recipes/{id:\\d+}",          // detalle con promedio
                                "/recipes/{id:\\d+}/full",     // detalle con creador
                                "/recipes/estado/**"           // filtrado por estado
                        ).permitAll()

                        // estas REQUIEREN JWT:
                        .requestMatchers(HttpMethod.GET,
                                "/recipes/drafts",             // tus borradores
                                "/recipes/saved",
                                "/recipes/{id:\\\\d+}/draft/full"// tus guardadas
                        ).authenticated()

                        .requestMatchers(HttpMethod.GET, "/recipes/created").authenticated()

                        // ratings públicos
                        .requestMatchers(HttpMethod.GET, "/ratings/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/ratings/count/me").authenticated()
                        // resto de métodos (POST, PUT, DELETE) sí requieren JWT
                        .anyRequest().authenticated()
                )

                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    // Exponemos el AuthenticationManager para inyectarlo en otros componentes si es necesario
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*")); // o la IP de tu app
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
