package be.salon.coiffurereservation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête d’authentification utilisée lors de la connexion d’un utilisateur.
 *
 * <p>Ce DTO transporte les informations d’identification nécessaires
 * pour vérifier l’identité d’un utilisateur et générer un jeton JWT.
 * Il est généralement envoyé au backend via une requête
 * <code>POST /api/auth/login</code>.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "email": "client@exemple.com",
 *   "password": "MotDePasse123!"
 * }
 * </pre>
 *
 * <p>Les deux champs sont obligatoires. Si la combinaison e-mail / mot de passe
 * est valide, une réponse {@link AuthResponse} est renvoyée.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Adresse e-mail associée au compte utilisateur. */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Mot de passe de l’utilisateur. */
    @NotBlank(message = "Password is required")
    private String password;
}
