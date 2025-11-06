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

    /** Adresse e-mail de l'utilisateur (doit être unique et d'un domaine belge reconnu). */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|outlook\\.com|hotmail\\.com|yahoo\\.com|live\\.be|skynet\\.be|proximus\\.be|telenet\\.be|scarlet\\.be|voo\\.be|orange\\.be)$",
            message = "Email must be from a recognized Belgian provider (gmail.com, outlook.com, hotmail.com, yahoo.com, live.be, skynet.be, proximus.be, telenet.be, scarlet.be, voo.be, orange.be)"
    )
    private String email;

    /** Mot de passe sécurisé (min 8 caractères, majuscule, minuscule, chiffre, symbole). */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
    )
    private String password;

    /** Prénom de l'utilisateur (lettres uniquement, pas de chiffres ni symboles). */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 80, message = "First name must be between 2 and 80 characters")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ\\s'-]+$",
            message = "First name must contain only letters, spaces, hyphens, and apostrophes"
    )
    private String firstName;

    /** Nom de famille de l'utilisateur (lettres uniquement, pas de chiffres ni symboles). */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 80, message = "Last name must be between 2 and 80 characters")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ\\s'-]+$",
            message = "Last name must contain only letters, spaces, hyphens, and apostrophes"
    )
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
