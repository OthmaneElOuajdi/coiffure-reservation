package be.salon.coiffurereservation.entity;

/**
 * Représente les différents statuts qu’un rendez-vous peut avoir
 * dans le système de réservation du salon de coiffure.
 *
 * <ul>
 *   <li>PENDING → Rendez-vous créé, mais pas encore confirmé</li>
 *   <li>CONFIRMED → Rendez-vous validé (paiement ou validation admin)</li>
 *   <li>CANCELLED → Rendez-vous annulé par le client ou le salon</li>
 *   <li>COMPLETED → Rendez-vous effectué avec succès</li>
 *   <li>NO_SHOW → Client absent, rendez-vous manqué</li>
 * </ul>
 */
public enum AppointmentStatus {

    /** En attente de confirmation par le staff ou après demande client. */
    PENDING,

    /** Confirmé par le personnel ou validation automatique après paiement. */
    CONFIRMED,

    /** Annulé par le client, ou administrativement par le salon. */
    CANCELLED,

    /** Le service a été réalisé et le rendez-vous clôturé. */
    COMPLETED,

    /** Le client ne s’est pas présenté au rendez-vous. */
    NO_SHOW
}
