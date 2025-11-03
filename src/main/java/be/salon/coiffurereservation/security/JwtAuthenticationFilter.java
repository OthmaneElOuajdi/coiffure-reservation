package be.salon.coiffurereservation.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtre d’authentification JWT personnalisé pour Spring Security.
 *
 * <p>Ce filtre intercepte chaque requête HTTP (via {@link OncePerRequestFilter}),
 * vérifie la présence d’un en-tête {@code Authorization: Bearer <token>} et, si le
 * token est valide, crée une authentification {@link UsernamePasswordAuthenticationToken}
 * insérée dans le {@link SecurityContextHolder}.</p>
 *
 * <p>Ce mécanisme permet une authentification sans état (stateless),
 * typique des API REST sécurisées par JWT.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Utilitaire JWT pour la validation et l’extraction des claims. */
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Vérifie la présence d’un token JWT dans le header Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Extraction des informations du token
                String email = jwtUtil.extractEmail(token);
                List<String> roles = jwtUtil.extractRoles(token);

                // Si aucune authentification n’est encore présente dans le contexte
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Injection de l’utilisateur authentifié dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Authenticated user '{}' with roles {}", email, roles);
                }

            } catch (JWTVerificationException ex) {
                log.warn("Invalid JWT token: {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("Unexpected error during JWT authentication", ex);
            }
        }

        // Poursuit la chaîne des filtres
        filterChain.doFilter(request, response);
    }
}
