package be.salon.coiffurereservation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI / Swagger pour l'application "Salon Reservation".
 *
 * <p>Cette configuration permet de générer automatiquement la documentation
 * interactive de l'API via Swagger UI et d'ajouter la sécurité JWT.</p>
 *
 * <p>Accessible après démarrage :
 * <ul>
 *   <li><b>/swagger-ui.html</b> — interface Swagger UI</li>
 *   <li><b>/v3/api-docs</b> — schéma OpenAPI JSON</li>
 * </ul>
 * </p>
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:1.0.0}")
    private String apiVersion;

    @Value("${app.base-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        log.info("✅ Initialisation de la configuration OpenAPI (version {}, base URL : {})", apiVersion, serverUrl);

        return new OpenAPI()
                .info(new Info()
                        .title("Salon Reservation API")
                        .version(apiVersion)
                        .description("REST API for the Belgian hair salon reservation platform.")
                        .contact(new Contact()
                                .name("Salon Support")
                                .email("support@salon.be")
                                .url("https://salon.be"))
                        .license(new License()
                                .name("Proprietary License")
                                .url("https://salon.be/license")))
                // Définition du serveur par défaut
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Default server environment")
                ))
                // Définition du schéma de sécurité (JWT Bearer)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Authentication via JWT Bearer Token")));
    }
}
