package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.entity.User;
import be.salon.coiffurereservation.dto.auth.AuthResponse;
import be.salon.coiffurereservation.dto.auth.LoginRequest;
import be.salon.coiffurereservation.dto.auth.RegisterRequest;
import be.salon.coiffurereservation.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur d’authentification permettant :
 * <ul>
 *     <li>L'inscription de nouveaux clients,</li>
 *     <li>La connexion d’utilisateurs existants,</li>
 *     <li>Et la récupération des informations de l’utilisateur actuellement connecté.</li>
 * </ul>
 *
 * <p>Les endpoints renvoient un token JWT à utiliser pour les appels sécurisés.</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'inscription, connexion et profil utilisateur")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscrit un nouveau client et renvoie un token JWT.
     *
     * @param request les informations du client à enregistrer (email, mot de passe, nom, etc.)
     * @return un objet {@link AuthResponse} contenant le token JWT et les informations de l’utilisateur.
     */
    @PostMapping("/register")
    @Operation(summary = "Inscrire un nouveau client", description = "Permet à un nouveau client de créer un compte et d’obtenir un token JWT.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Connecte un utilisateur existant et renvoie un token JWT.
     *
     * @param request les informations de connexion (email et mot de passe).
     * @return un objet {@link AuthResponse} contenant le token JWT et les informations de l’utilisateur.
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion d’un utilisateur", description = "Permet à un utilisateur existant de se connecter et d’obtenir un token JWT.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les informations du client actuellement connecté.
     *
     * @param authentication l’objet d’authentification Spring Security contenant l’utilisateur courant.
     * @return les informations de l’utilisateur (id, email, nom, téléphone, rôles, etc.)
     */
    @GetMapping("/me")
    @Operation(summary = "Récupérer les informations du compte connecté",
            description = "Retourne les informations de l’utilisateur actuellement connecté à partir du token JWT.")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User user = authService.getCurrentUser(email);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("phone", user.getPhone());
        response.put("roles", user.getRoles());
        response.put("emailVerified", user.getEmailVerified());

        return ResponseEntity.ok(response);
    }

    /**
     * Valide un token JWT.
     *
     * @param token token JWT à valider (envoyé dans le header Authorization)
     * @return statut de validité du token
     */
    @PostMapping("/validate")
    @Operation(summary = "Valider un token JWT", description = "Vérifie si un token JWT est valide et non expiré.")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        // Extraire le token du header "Bearer <token>"
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        boolean isValid = authService.validateToken(jwtToken);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchit un token JWT.
     *
     * @param token token JWT actuel (envoyé dans le header Authorization)
     * @return nouveau token JWT
     */
    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir un token JWT", description = "Génère un nouveau token JWT à partir d'un token encore valide.")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            // Extraire le token du header "Bearer <token>"
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            String newToken = authService.refreshToken(jwtToken);

            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }
}
