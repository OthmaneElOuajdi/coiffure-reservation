package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Représente un rendez-vous ({@code Appointment}) entre un client et un membre du personnel du salon.
 * <p>
 * Cette entité contient toutes les informations nécessaires à la gestion d’une réservation :
 * <ul>
 *   <li>Le client concerné ({@link User})</li>
 *   <li>Le membre du staff assigné ({@link StaffMember})</li>
 *   <li>Le service/prestation associé ({@link Service})</li>
 *   <li>Le créneau horaire (début et fin)</li>
 *   <li>Le statut du rendez-vous ({@link AppointmentStatus})</li>
 * </ul>
 * <p>
 * Des méthodes métier permettent de modifier ou d’interroger l’état du rendez-vous (ex. confirmer, annuler, compléter...).
 * </p>
 */
@Entity
@Table(
        name = "appointment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "start_time"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    /**
     * Identifiant unique du rendez-vous (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Client ayant pris le rendez-vous.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Membre du personnel (coiffeur, esthéticien, etc.) assigné à ce rendez-vous.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffMember staffMember;

    /**
     * Type de service ou de prestation associé au rendez-vous.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    /**
     * Date du rendez-vous.
     */
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /**
     * Heure de début du rendez-vous.
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Heure de fin prévue du rendez-vous.
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Statut actuel du rendez-vous.
     * <p>
     * Exemple : {@code PENDING}, {@code CONFIRMED}, {@code COMPLETED}, {@code CANCELLED}, {@code NO_SHOW}.
     * <p>
     * Mappé comme une chaîne de caractères (VARCHAR) pour éviter les conflits de types SQL spécifiques.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    /**
     * Notes internes ou remarques facultatives concernant le rendez-vous.
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Raison fournie en cas d'annulation du rendez-vous.
     */
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    /**
     * Date et heure de l'annulation (si applicable).
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * Date de création de l’enregistrement (gérée automatiquement par Hibernate).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière mise à jour de l’enregistrement (gérée automatiquement par Hibernate).
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // -------------------------------------------------
    // ---------------- MÉTHODES MÉTIER ----------------
    // -------------------------------------------------

    /**
     * Vérifie si le rendez-vous ne peut PAS être annulé.
     * <p>
     * Seuls les rendez-vous en attente ({@code PENDING}) ou confirmés ({@code CONFIRMED})
     * peuvent être annulés. Les autres statuts ne permettent pas l'annulation.
     *
     * @return {@code true} si l'annulation n'est PAS autorisée, sinon {@code false}.
     */
    public boolean cannotBeCancelled() {
        return status != AppointmentStatus.PENDING && status != AppointmentStatus.CONFIRMED;
    }

    /**
     * Annule le rendez-vous en précisant une raison d'annulation.
     *
     * @param reason raison de l'annulation (ex. "Client absent", "Annulation par le salon")
     */
    public void cancel(String reason) {
        this.status = AppointmentStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * Confirme le rendez-vous (passe le statut à {@code CONFIRMED}).
     */
    public void confirm() {
        this.status = AppointmentStatus.CONFIRMED;
    }
}
