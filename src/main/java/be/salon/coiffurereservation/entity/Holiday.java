package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un jour férié ou une période d’indisponibilité.
 * Deux usages possibles :
 * <ul>
 *   <li>Fermeture globale du salon (staffMember = null)</li>
 *   <li>Absence individuelle d’un membre du staff (congés, maladie, etc.)</li>
 * </ul>
 *
 * Si {@code isRecurring = true}, l’événement se répète chaque année
 * (ex : Noël, Jour de l’An).
 */
@Entity
@Table(name = "holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    /** Identifiant unique de la période de fermeture/absence. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Membre du personnel concerné par l’absence.
     * Null → fermeture générale du salon.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private StaffMember staffMember;

    /** Nom ou description de la période (ex : "Vacances", "Jour férié"). */
    @Column(length = 120)
    private String name;

    /** Date de début de l’indisponibilité. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Date de fin de l’indisponibilité (incluse). */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Indique si l’événement se répète annuellement.
     * Exemple : 25 décembre → Noël → isRecurring = true
     */
    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    /** Horodatage de création de l'événement. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
