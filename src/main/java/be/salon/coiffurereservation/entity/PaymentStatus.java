package be.salon.coiffurereservation.entity;

/**
 * Représente le cycle de vie d'un paiement dans le système.
 *
 * <ul>
 *   <li>{@link #PENDING} → Transaction initiée, mais pas encore confirmée</li>
 *   <li>{@link #SUCCEEDED} → Paiement validé par le prestataire</li>
 *   <li>{@link #FAILED} → Échec lors du traitement du paiement</li>
 *   <li>{@link #REFUNDED} → Remboursement partiel ou total effectué</li>
 * </ul>
 *
 * Ces statuts permettent à l'application de gérer correctement :
 * - l'accès au service (confirmation du rendez-vous)
 * - l'annulation/remboursement
 * - la gestion des retours de Stripe ou autre PSP
 */
public enum PaymentStatus {

    /** Paiement en attente de confirmation par le prestataire. */
    PENDING,

    /** Paiement validé avec succès et fonds transférés. */
    SUCCEEDED,

    /** Paiement refusé ou erreur de traitement. */
    FAILED,

    /** Paiement remboursé par le salon ou à la demande du client. */
    REFUNDED
}
