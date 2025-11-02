package be.salon.coiffurereservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Requête utilisée pour créer une session de paiement (checkout) pour un rendez-vous.
 *
 * <p>Ce DTO est envoyé par le client lorsqu’il souhaite régler un rendez-vous
 * via la plateforme de paiement (ex. : Stripe). Le backend crée alors
 * une session sécurisée et renvoie les informations nécessaires
 * à la redirection du client vers l’interface de paiement.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "appointmentId": "b1a2c3d4-e5f6-7890-1234-56789abcdef0"
 * }
 * </pre>
 *
 * <p>Validation :</p>
 * <ul>
 *   <li><b>@NotNull</b> — l’identifiant du rendez-vous est obligatoire.</li>
 * </ul>
 *
 * @see be.salon.coiffurereservation.service.PaymentService#createCheckoutSession(CreateCheckoutRequest, String)
 * @see be.salon.coiffurereservation.dto.CheckoutResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCheckoutRequest {

    /** Identifiant unique du rendez-vous concerné par le paiement (UUID). */
    @NotNull(message = "Appointment ID is required")
    private UUID appointmentId;
}
