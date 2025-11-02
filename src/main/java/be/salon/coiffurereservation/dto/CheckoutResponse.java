package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse renvoyée au client après la création d’une session de paiement.
 *
 * <p>Ce DTO est utilisé principalement dans le cadre de l’intégration avec Stripe.
 * Lorsqu’un client initie un paiement, le backend crée une session
 * via l’API Stripe et renvoie les informations nécessaires
 * pour rediriger l’utilisateur vers l’interface de paiement sécurisée.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "sessionId": "cs_test_a1b2c3d4e5f6g7h8",
 *   "url": "<a href="https://checkout.stripe.com/pay/cs_test_a1b2c3...">https://checkout.stripe.com/pay/cs_test_a1b2c3...</a>"
 * }
 * </pre>
 *
 * <p>Le frontend doit utiliser le champ <code>url</code> pour rediriger
 * automatiquement l’utilisateur vers la page de paiement Stripe.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    /** Identifiant unique de la session Stripe créée pour ce paiement. */
    private String sessionId;

    /** URL sécurisée Stripe permettant au client de procéder au paiement. */
    private String url;
}
