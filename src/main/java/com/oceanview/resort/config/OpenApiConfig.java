package com.oceanview.resort.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 / Swagger configuration for the Ocean View Resort API.
 *
 * <p>Provides centralized API documentation metadata including title,
 * description, version, contact information, and JWT security scheme.
 * The Swagger UI is accessible at {@code /swagger-ui.html}.</p>
 *
 * <h3>Security:</h3>
 * <p>Configures a Bearer JWT authentication scheme so that authenticated
 * endpoints can be tested directly from the Swagger UI by providing a
 * valid access token.</p>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ocean View Resort API",
                version = "1.0.0",
                description = "REST API for the Ocean View Resort Room Reservation System. " +
                        "Provides endpoints for managing reservations, rooms, bills, payments, " +
                        "user authentication, loyalty points, and reporting.",
                contact = @Contact(
                        name = "Ocean View Resort",
                        email = "admin@oceanviewresort.com",
                        url = "https://oceanviewresort.com"
                ),
                license = @License(
                        name = "Cardiff Metropolitan University – CIS6003",
                        url = "https://www.cardiffmet.ac.uk"
                )
        ),
        servers = {
                @Server(url = "/oceanview", description = "Local Development Server")
        },
        security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "Enter your JWT access token (without the 'Bearer ' prefix). " +
                "Obtain a token via POST /api/auth/login.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // All configuration is annotation-driven.
}
