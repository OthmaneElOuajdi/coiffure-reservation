package be.salon.coiffurereservation.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête de mise à jour du profil utilisateur.
 *
 * <p>Ce DTO est utilisé lorsque l'utilisateur souhaite modifier
 * ses informations personnelles (prénom, nom, téléphone).</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "firstName": "Marie",
 *   "lastName": "Dupont",
 *   "phone": "+32475123456"
 * }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

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
