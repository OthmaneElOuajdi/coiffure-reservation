package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Représente un paiement associé à un rendez-vous dans le système.
 *
 * <p>Conçu pour supporter différents prestataires (Stripe par défaut),
 * avec une structure flexible permettant de stocker des informations
 * complémentaires en JSONB (Supabase/PostgreSQL).</p>
 *
 * <p>Chaque paiement est attaché à un {@link Appointment} unique et possède
 * un statut évolutif afin de suivre son cycle de vie :</p>
 *
 * <ul>
 *   <li>PENDING → en attente de confirmation du prestataire</li>
 *   <li>SUCCEEDED → transaction validée</li>
 *   <li>FAILED → échec du paiement</li>
 *   <li>REFUNDED → remboursement partiel ou total effectué</li>
 * </ul>
 */
@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "provider", "provider_payment_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    /** Identifiant unique du paiement (UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Rendez-vous auquel ce paiement se rattache. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /** Prestataire de paiement (Stripe par défaut). */
    @Column(nullable = false, length = 32)
    @Builder.Default
    private String provider = "STRIPE";

    /** ID de paiement fourni par le prestataire (ex: Stripe charge ID). */
    @Column(name = "provider_payment_id", length = 128)
    private String providerPaymentId;

    /** ID de session fourni par le prestataire (utile pour Stripe Checkout). */
    @Column(name = "provider_session_id", length = 128)
    private String providerSessionId;

    /** Montant payé en centimes (ex: 1299 = 12,99€). */
    @Column(name = "amount_cents", nullable = false)
    private Integer amountCents;

    /** Devise du paiement (EUR par défaut). */
    @Column(nullable = false, length = 8)
    @Builder.Default
    private String currency = "EUR";

    /** Statut du paiement (PENDING, SUCCEEDED, FAILED, REFUNDED...). */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Données complémentaires fournies par le prestataire
     * (ex: reçus, détails carte, logs d'erreur…).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private Map<String, Object> metadata;

    /** Date de création du paiement. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Dernière mise à jour du paiement. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---------------------------------------
    // Méthodes métier
    // ---------------------------------------

    /** Marque le paiement comme réussi. */
    public void markSucceeded() {
        this.status = PaymentStatus.SUCCEEDED;
    }
}
