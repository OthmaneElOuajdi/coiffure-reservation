package be.salon.coiffurereservation.dto;

import be.salon.coiffurereservation.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un rendez-vous (appointment) sous forme de DTO
 * pour la communication entre le backend et le frontend.
 *
 * <p>Ce DTO est utilisé pour transférer les informations essentielles
 * sur un rendez-vous sans exposer directement l'entité JPA {@link be.salon.coiffurereservation.entity.Appointment}.</p>
 *
 * <h3>Contenu :</h3>
 * <ul>
 *   <li>Identifiants de l’utilisateur, du membre du staff et du service concerné</li>
 *   <li>Horaires du rendez-vous (début et fin)</li>
 *   <li>Statut actuel du rendez-vous ({@link AppointmentStatus})</li>
 *   <li>Informations sur la prestation (nom, durée, prix)</li>
 *   <li>Notes et informations d’annulation le cas échéant</li>
 * </ul>
 *
 * <p>Ce modèle est principalement renvoyé par les endpoints REST,
 * ou reçu lors de mises à jour partielles côté client.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    /** Identifiant unique du rendez-vous. */
    private UUID id;

    /** Identifiant de l’utilisateur ayant pris le rendez-vous. */
    private UUID userId;

    /** Adresse e-mail du client. */
    private String userEmail;

    /** Nom complet du client. */
    private String userName;

    /** Identifiant du membre du personnel assigné. */
    private UUID staffId;

    /** Nom complet du membre du personnel. */
    private String staffName;

    /** Identifiant du service associé. */
    private UUID serviceId;

    /** Nom de la prestation ou du service. */
    private String serviceName;

    /** Durée du service en minutes. */
    private Integer serviceDuration;

    /** Prix du service en centimes d’euro (ex. : 1500 = 15,00 €). */
    private Integer servicePriceCents;

    /** Date et heure de début du rendez-vous. */
    private LocalDateTime startTime;

    /** Date et heure de fin du rendez-vous. */
    private LocalDateTime endTime;

    /** Statut du rendez-vous (PENDING, CONFIRMED, CANCELLED, etc.). */
    private AppointmentStatus status;

    /** Notes ou commentaires associés au rendez-vous. */
    private String notes;

    /** Raison de l’annulation (si applicable). */
    private String cancellationReason;

    /** Date et heure de l’annulation (si applicable). */
    private LocalDateTime cancelledAt;

    /** Date de création du rendez-vous. */
    private LocalDateTime createdAt;

    /** Date de dernière mise à jour. */
    private LocalDateTime updatedAt;

    /** URL de paiement Stripe (pour les RDV en attente de paiement). */
    private String stripePaymentUrl;
}
