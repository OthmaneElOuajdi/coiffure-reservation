package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Définit les heures de travail d’un membre du personnel pour un jour donné.
 *
 * <p>Chaque enregistrement correspond à un jour de la semaine et
 * précise les heures de début, de fin et de pause éventuelle.</p>
 *
 * <ul>
 *   <li>{@link #dayOfWeek} suit la convention Java : 1 = Lundi, 7 = Dimanche</li>
 *   <li>{@link #breakStart} et {@link #breakEnd} sont optionnels</li>
 *   <li>Chaque membre du staff ne peut avoir qu’une ligne par jour
 *       (grâce à la contrainte d’unicité staff_id + day_of_week).</li>
 * </ul>
 */
@Entity
@Table(
        name = "working_hours",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"staff_id", "day_of_week"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkingHours {

    /** Identifiant unique de la ligne d’horaire. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Membre du personnel concerné par ces horaires. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffMember staffMember;

    /**
     * Jour de la semaine (1 = lundi, 7 = dimanche).
     * Peut être mappé à {@link java.time.DayOfWeek}.
     */
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    /** Heure de début de la journée de travail. */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** Heure de fin de la journée de travail. */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** Début de la pause (optionnel). */
    @Column(name = "break_start")
    private LocalTime breakStart;

    /** Fin de la pause (optionnel). */
    @Column(name = "break_end")
    private LocalTime breakEnd;

    /** Date de création de l’enregistrement (auto-générée). */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // -------------------------------------------------------------
    // Méthodes utilitaires métier
    // -------------------------------------------------------------

    /**
     * Vérifie si une pause est définie.
     *
     * @return true si breakStart et breakEnd sont renseignés.
     */
    public boolean hasBreak() {
        return breakStart != null && breakEnd != null;
    }
}
