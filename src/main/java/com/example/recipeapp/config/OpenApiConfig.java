package com.example.recipeapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Recetify API",
                version = "v1",
                description = "API de recetas, ingredientes, ratings y autenticación",
                contact = @Contact(name = "Luca")
        ),
        // Todos los endpoints requieren JWT por defecto
        security = @SecurityRequirement(name = "bearerAuth"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local"),
                @Server(url = "https://TU-DOMINIO-RAILWAY", description = "Producción")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class OpenApiConfig {
        // Solo anotaciones
}