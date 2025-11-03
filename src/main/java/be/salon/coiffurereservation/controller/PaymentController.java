package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.CheckoutResponse;
import be.salon.coiffurereservation.dto.CreateCheckoutRequest;
import be.salon.coiffurereservation.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur responsable de la gestion des paiements via <b>Stripe</b>.
 *
 * <p>Il permet :</p>
 * <ul>
 *     <li>La création d'une session de paiement Stripe (checkout)</li>
 *     <li>Le traitement des événements de paiement via le webhook Stripe</li>
 * </ul>
 *
 * <p>Toutes les opérations de création de paiement nécessitent une authentification via JWT.</p>
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Paiements", description = "Gestion des paiements via Stripe")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    /**
     * Crée une session de paiement Stripe pour un rendez-vous donné.
     *
     * <p>
     * Cette méthode génère une session Checkout Stripe associée à un utilisateur
     * et à un rendez-vous. Le client est ensuite redirigé vers l’interface de paiement Stripe.
     * </p>
     *
     * @param request        les informations nécessaires pour créer la session de paiement (montant, rendez-vous, etc.)
     * @param authentication l’objet d’authentification contenant l’adresse email de l’utilisateur connecté.
     * @return une réponse contenant l’URL de redirection vers la page de paiement Stripe.
     * @throws StripeException en cas d’erreur lors de la création de la session.
     */
    @PostMapping("/checkout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Créer une session de paiement Stripe",
            description = "Génère une session Checkout Stripe pour l’utilisateur connecté afin de régler un rendez-vous."
    )
    public ResponseEntity<CheckoutResponse> createCheckout(
            @Valid @RequestBody CreateCheckoutRequest request,
            Authentication authentication) throws StripeException {

        String userEmail = authentication.getName();
        CheckoutResponse response = paymentService.createCheckoutSession(request, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Gère le webhook Stripe pour recevoir et traiter les événements de paiement.
     *
     * <p>
     * Stripe envoie automatiquement les événements relatifs aux paiements (paiement réussi, échec, expiration, etc.)
     * à cette URL configurée côté Stripe.
     * </p>
     *
     * @param payload   le contenu JSON envoyé par Stripe.
     * @param sigHeader l’en-tête contenant la signature Stripe pour vérifier l’authenticité du message.
     * @return une réponse HTTP confirmant la bonne réception de l’événement.
     */
    @PostMapping("/stripe/webhook")
    @Operation(
            summary = "Webhook Stripe pour les événements de paiement",
            description = "Reçoit et traite les notifications Stripe (paiement réussi, session expirée, etc.)."
    )
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // Vérifie la signature du webhook
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Signature invalide pour le webhook Stripe", e);
            return ResponseEntity.status(400).body("Signature invalide");
        }

        // Déléguer le traitement au service
        paymentService.handleWebhook(payload, sigHeader);

        // Traitement des événements envoyés par Stripe
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    String appointmentId = session.getMetadata().get("appointmentId");
                    paymentService.markPaymentSucceeded(UUID.fromString(appointmentId), session.getId());
                    log.info("Paiement réussi pour le rendez-vous : {}", appointmentId);
                }
                break;

            case "checkout.session.expired":
                log.info("Session de paiement expirée");
                break;

            default:
                log.info("Événement Stripe non géré : {}", event.getType());
        }

        return ResponseEntity.ok("Success");
    }

    /**
     * Récupère un paiement par fournisseur et identifiant de paiement.
     *
     * @param provider          nom du fournisseur (ex: "STRIPE")
     * @param providerPaymentId identifiant du paiement côté fournisseur
     * @return Payment trouvé
     */
    @GetMapping("/provider/{provider}/{providerPaymentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer un paiement par fournisseur", description = "Retourne un paiement par son fournisseur et son ID externe.")
    public ResponseEntity<be.salon.coiffurereservation.entity.Payment> getPaymentByProviderAndPaymentId(
            @PathVariable String provider,
            @PathVariable String providerPaymentId) {
        return ResponseEntity.ok(paymentService.getPaymentByProviderAndPaymentId(provider, providerPaymentId));
    }

    /**
     * Récupère tous les paiements d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des paiements de l'utilisateur
     */
    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer les paiements d'un utilisateur", description = "Retourne tous les paiements d'un utilisateur.")
    public ResponseEntity<java.util.List<be.salon.coiffurereservation.entity.Payment>> getUserPayments(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }

    /**
     * Récupère tous les paiements en attente.
     *
     * @return liste des paiements PENDING
     */
    @GetMapping("/pending")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer les paiements en attente", description = "Retourne tous les paiements en attente.")
    public ResponseEntity<java.util.List<be.salon.coiffurereservation.entity.Payment>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }

    /**
     * Récupère tous les paiements réussis.
     *
     * @return liste des paiements SUCCEEDED
     */
    @GetMapping("/succeeded")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer les paiements réussis", description = "Retourne tous les paiements réussis.")
    public ResponseEntity<java.util.List<be.salon.coiffurereservation.entity.Payment>> getSucceededPayments() {
        return ResponseEntity.ok(paymentService.getSucceededPayments());
    }

    /**
     * Récupère tous les paiements échoués.
     *
     * @return liste des paiements FAILED
     */
    @GetMapping("/failed")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer les paiements échoués", description = "Retourne tous les paiements échoués.")
    public ResponseEntity<java.util.List<be.salon.coiffurereservation.entity.Payment>> getFailedPayments() {
        return ResponseEntity.ok(paymentService.getFailedPayments());
    }

    /**
     * Récupère le paiement associé à un rendez-vous.
     *
     * @param appointmentId identifiant du rendez-vous
     * @return Payment trouvé ou 404 si aucun paiement
     */
    @GetMapping("/appointment/{appointmentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer le paiement d'un rendez-vous", description = "Retourne le paiement associé à un rendez-vous.")
    public ResponseEntity<be.salon.coiffurereservation.entity.Payment> getPaymentByAppointmentId(
            @PathVariable UUID appointmentId) {
        be.salon.coiffurereservation.entity.Payment payment = paymentService.getPaymentByAppointmentId(appointmentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}
