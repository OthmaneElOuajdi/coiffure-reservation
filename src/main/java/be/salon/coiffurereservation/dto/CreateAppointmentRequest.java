package be.salon.coiffurereservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Requête utilisée pour créer un nouveau rendez-vous dans le système.
 *
 * <p>Ce DTO est envoyé par le client (frontend) lors de la réservation
 * d’un créneau horaire pour un service donné auprès d’un membre du staff.
 * Les validations intégrées garantissent que toutes les informations nécessaires
 * sont présentes et que la date du rendez-vous est future.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "serviceId": "8b9d3d5c-12ef-4e68-b5b8-c3c132cfb79",
 *   "staffId": "a6c7e3d4-789b-4371-b8f2-5e23b4a60f88",
 *   "startTime": "2025-11-01T14:30:00",
 *   "notes": "Je préfère un shampoing avant la coupe"
 * }
 * </pre>
 *
 * <p>Les validations suivantes sont appliquées :</p>
 * <ul>
 *   <li><b>@NotNull</b> → Les identifiants de service, de staff et la date de début sont obligatoires</li>
 *   <li><b>@Future</b> → La date du rendez-vous doit être dans le futur</li>
 * </ul>
 *
 * @see be.salon.coiffurereservation.service.AppointmentService#createAppointment(CreateAppointmentRequest, String)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    /** Identifiant unique du service sélectionné (UUID). */
    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    /** Identifiant du membre du personnel (coiffeur, esthéticien, etc.). */
    @NotNull(message = "Staff member ID is required")
    private UUID staffId;

    /** Date et heure de début souhaitées pour le rendez-vous (doit être future). */
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    /** Notes optionnelles laissées par le client (ex. : préférences, instructions, etc.). */
    private String notes;
}
