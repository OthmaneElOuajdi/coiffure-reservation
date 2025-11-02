package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Requête utilisée pour mettre à jour un rendez-vous existant.
 *
 * <p>Ce DTO est envoyé par le frontend lorsque l’utilisateur (ou un administrateur)
 * souhaite modifier un rendez-vous déjà créé, par exemple pour changer de service,
 * de membre du personnel, d’horaire ou ajouter une note.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "serviceId": "c8f2a5a7-3c23-4b91-8c76-3c4b7b32b8d2",
 *   "staffId": "f5b1a8d1-81c3-4c2a-9b7e-7f4f6e7f7b2a",
 *   "startTime": "2025-11-03T14:30:00",
 *   "notes": "Je préfère une coupe légèrement plus courte que la dernière fois."
 * }
 * </pre>
 *
 * <p>Les champs sont facultatifs : seuls ceux fournis dans la requête seront mis à jour.
 * Les validations et restrictions (comme les délais de modification) sont gérées dans
 * {@link be.salon.coiffurereservation.service.AppointmentService}.</p>
 *
 * @see be.salon.coiffurereservation.service.AppointmentService
 * @see be.salon.coiffurereservation.entity.Appointment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    /** Identifiant du service choisi (facultatif). */
    private UUID serviceId;

    /** Identifiant du membre du personnel souhaité (facultatif). */
    private UUID staffId;

    /** Nouvelle date et heure de début du rendez-vous (facultatif). */
    private LocalDateTime startTime;

    /** Notes ou commentaires de l’utilisateur concernant le rendez-vous. */
    private String notes;
}
