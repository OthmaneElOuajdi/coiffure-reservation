package be.salon.coiffurereservation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête d’inscription d’un nouvel utilisateur.
 *
 * <p>Ce DTO est utilisé lorsque le client souhaite créer un nouveau compte
 * sur la plateforme. Il contient les informations nécessaires à la création
 * d’un utilisateur dans le système d’authentification.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "email": "client@exemple.com",
 *   "password": "MotDePasse123!",
 *   "firstName": "Marie",
 *   "lastName": "Dupont",
 *   "phone": "+32475123456"
 * }
 * </pre>
 *
 * <p>Le champ <code>phone</code> est facultatif, mais lorsqu’il est fourni,
 * il doit respecter le format belge (+32 suivi de 9 chiffres).</p>
 *
 * <p>Après une inscription réussie, le serveur renvoie une
 * {@link be.salon.coiffurereservation.dto.auth.AuthResponse}
 * contenant le jeton JWT et les informations du compte créé.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Adresse e-mail de l’utilisateur (doit être unique). */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Mot de passe choisi par l’utilisateur (minimum 8 caractères). */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /** Prénom de l’utilisateur. */
    @NotBlank(message = "First name is required")
    @Size(max = 80, message = "First name must not exceed 80 characters")
    private String firstName;

    /** Nom de famille de l’utilisateur. */
    @NotBlank(message = "Last name is required")
    @Size(max = 80, message = "Last name must not exceed 80 characters")
    private String lastName;

    /**
     * Numéro de téléphone belge au format international.
     * <p>Format attendu : <code>+32XXXXXXXXX</code> (ex. : +32475123456)</p>
     */
    @Pattern(
            regexp = "^\\+32[0-9]{9}$",
            message = "Invalid Belgian phone number (format: +32XXXXXXXXX)"
    )
    private String phone;
}
