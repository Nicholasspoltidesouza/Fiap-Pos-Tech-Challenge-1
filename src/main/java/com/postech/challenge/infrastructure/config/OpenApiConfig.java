package com.postech.challenge.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Tech Challenge - Oficina Mecânica API",
                version = "1.0",
                description = "API REST para gerenciamento de oficina mecânica"
        ),
        servers = {
                @Server(url = "/", description = "Current host (Kong :8000 or API :8080)"),
                @Server(url = "http://localhost:8000", description = "Kong API Gateway"),
                @Server(url = "http://localhost:8080", description = "API direct")
        },
        security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
