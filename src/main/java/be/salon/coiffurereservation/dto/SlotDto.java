package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un créneau horaire disponible (ou non) pour une réservation.
 *
 * <p>Ce DTO est utilisé par le frontend pour afficher les disponibilités
 * d’un membre du personnel à une date donnée, dans le cadre de la prise
 * de rendez-vous en ligne.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "startTime": "2025-11-02T14:00:00",
 *   "endTime": "2025-11-02T14:30:00",
 *   "staffId": "1a2b3c4d-5e6f-7081-920a-bcdef1234567",
 *   "staffName": "Sophie Martin",
 *   "available": true
 * }
 * </pre>
 *
 * <p>Les créneaux sont généralement calculés par le service {@link be.salon.coiffurereservation.service.SlotService}
 * à partir des horaires de travail ({@link be.salon.coiffurereservation.entity.WorkingHours}),
 * des congés ({@link be.salon.coiffurereservation.entity.Holiday})
 * et des rendez-vous déjà confirmés ({@link be.salon.coiffurereservation.entity.Appointment}).</p>
 *
 * @see be.salon.coiffurereservation.service.SlotService
 * @see be.salon.coiffurereservation.entity.WorkingHours
 * @see be.salon.coiffurereservation.entity.Appointment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {

    /** Date et heure de début du créneau. */
    private LocalDateTime startTime;

    /** Date et heure de fin du créneau. */
    private LocalDateTime endTime;

    /** Identifiant du membre du personnel concerné. */
    private UUID staffId;

    /** Nom complet du membre du personnel. */
    private String staffName;

    /** Indique si le créneau est disponible pour une réservation. */
    private Boolean available;
}
