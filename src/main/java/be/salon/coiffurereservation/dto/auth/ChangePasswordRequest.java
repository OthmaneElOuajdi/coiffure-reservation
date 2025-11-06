package be.salon.coiffurereservation.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête de changement de mot de passe.
 *
 * <p>Ce DTO est utilisé lorsque l'utilisateur souhaite modifier
 * son mot de passe. Il doit fournir son mot de passe actuel
 * pour des raisons de sécurité.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "currentPassword": "AncienMotDePasse123!",
 *   "newPassword": "NouveauMotDePasse456!"
 * }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    /** Mot de passe actuel de l'utilisateur (pour vérification). */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /** Nouveau mot de passe choisi par l'utilisateur (minimum 8 caractères). */
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;
}
