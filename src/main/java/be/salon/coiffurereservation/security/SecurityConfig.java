package be.salon.coiffurereservation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration centrale de la sécurité Spring Security.
 *
 * <p>Cette configuration met en place un système d’authentification
 * sans état (JWT), gère les autorisations d’accès aux endpoints REST,
 * et enregistre les filtres personnalisés nécessaires à l’authentification.</p>
 *
 * <h3>Fonctionnalités clés :</h3>
 * <ul>
 *   <li>Authentification stateless via JWT</li>
 *   <li>Intégration du filtre {@link JwtAuthenticationFilter}</li>
 *   <li>Support des annotations {@code @PreAuthorize}</li>
 *   <li>Encodage de mot de passe avec BCrypt</li>
 *   <li>Gestion fine des accès via {@link HttpSecurity}</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Définit la chaîne de filtres de sécurité Spring.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation de CSRF (API stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Activation du CORS avec la configuration fournie
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Pas de session (JWT = stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Règles d’autorisation selon les routes
                .authorizeHttpRequests(auth -> auth
                        // Authentification / inscription
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Ressources publiques
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/staff/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/slots/**").permitAll()
                        .requestMatchers("/api/v1/payments/stripe/webhook").permitAll()

                        // Swagger / API docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Tests temporaires (dev)
                        .requestMatchers("/api/v1/test/**").permitAll()

                        // Actuator (health public, reste admin)
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Endpoints clients/admins
                        .requestMatchers("/api/v1/appointments/mine/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/appointments").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/appointments/**").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/api/v1/payments/checkout").hasAnyRole("CLIENT", "ADMIN")

                        // Espace d’administration
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Toute autre requête doit être authentifiée
                        .anyRequest().authenticated()
                )

                // Enregistre notre provider + filtre JWT
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provider d'authentification basé sur les utilisateurs de la base (DAO).
     */
    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * Expose le {@link AuthenticationManager} pour l’injection dans d’autres composants.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Définit l’encodeur de mots de passe (BCrypt, force 10).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
