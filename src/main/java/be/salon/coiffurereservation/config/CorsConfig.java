package be.salon.coiffurereservation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS (Cross-Origin Resource Sharing) globale.
 *
 * <p>Permet aux requêtes provenant du front-end (ex: React, Vue, Angular)
 * d'accéder à l'API Spring Boot tout en appliquant des règles de sécurité
 * définies (origines autorisées, méthodes HTTP, entêtes, etc.).</p>
 *
 * <p>Les origines autorisées sont définies dans le fichier
 * <code>application.properties</code> ou <code>.env</code>
 * via la clé <b>app.frontend.url</b>.</p>
 */
@Slf4j
@Configuration
public class CorsConfig {

    /**
     * URL(s) du front-end autorisées à accéder à l’API.
     * Exemple :
     * <pre>
     * app.frontend.url=http://localhost:5173,http://localhost:3000
     * </pre>
     */
    @Value("${app.frontend.url}")
    private String frontendUrls;

    /**
     * Configure les règles CORS globales pour l’ensemble des endpoints REST.
     *
     * @return un {@link CorsConfigurationSource} appliqué à toutes les routes.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permet plusieurs origines (séparées par des virgules dans les properties)
        List<String> allowedOrigins = Arrays.asList(frontendUrls.split(","));
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // durée de mise en cache des règles CORS (1h)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("✅ Configuration CORS initialisée : {}", allowedOrigins);
        return source;
    }
}
