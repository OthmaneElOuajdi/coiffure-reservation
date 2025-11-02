package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.Payment;
import be.salon.coiffurereservation.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des paiements {@link Payment}.
 * <p>
 * Fournit des méthodes pour rechercher les paiements :
 * <ul>
 *     <li>Par rendez-vous associé</li>
 *     <li>Par identifiant du fournisseur (Stripe, etc.)</li>
 *     <li>Par statut (PENDING, SUCCEEDED, REFUNDED...)</li>
 *     <li>Par utilisateur</li>
 * </ul>
 * <p>
 * Ces méthodes sont utilisées notamment pour le suivi des transactions,
 * la validation des paiements, et la génération de rapports financiers.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Recherche un paiement lié à un rendez-vous spécifique.
     *
     * @param appointmentId identifiant du rendez-vous
     * @return paiement correspondant, s’il existe
     */
    Optional<Payment> findByAppointmentId(UUID appointmentId);

    /**
     * Recherche un paiement à partir du fournisseur et de son identifiant de paiement externe.
     * <p>
     * Par exemple : (provider="STRIPE", providerPaymentId="pi_1234").
     *
     * @param provider          nom du fournisseur (Stripe, PayPal, etc.)
     * @param providerPaymentId identifiant unique du paiement côté fournisseur
     * @return paiement correspondant, s’il existe
     */
    Optional<Payment> findByProviderAndProviderPaymentId(String provider, String providerPaymentId);

    /**
     * Recherche un paiement via l’identifiant de session du fournisseur
     * (utile pour Stripe Checkout ou les paiements en attente).
     *
     * @param sessionId identifiant de session externe
     * @return paiement correspondant, s’il existe
     */
    Optional<Payment> findByProviderSessionId(String sessionId);

    /**
     * Liste tous les paiements correspondant à un statut donné.
     *
     * @param status statut du paiement (PENDING, SUCCEEDED, FAILED, etc.)
     * @return liste des paiements avec ce statut
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Liste les paiements associés à un utilisateur donné,
     * en se basant sur la relation entre le rendez-vous et l’utilisateur.
     *
     * @param userId identifiant de l’utilisateur
     * @return liste des paiements de l’utilisateur
     */
    List<Payment> findByAppointmentUserId(UUID userId);
}
