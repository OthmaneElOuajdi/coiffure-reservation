package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête permettant d’annuler un rendez-vous existant.
 *
 * <p>Ce DTO est utilisé côté client (frontend) pour envoyer la raison
 * d’annulation d’un rendez-vous à l’API. Il est ensuite traité
 * par le service {@link be.salon.coiffurereservation.service.AppointmentService}
 * afin de mettre à jour le statut du rendez-vous et d’enregistrer
 * les informations associées.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "reason": "Je ne peux pas venir ce jour-là"
 * }
 * </pre>
 *
 * <p>Ce champ est optionnel, mais recommandé pour le suivi et la communication avec le salon.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequest {

    /**
     * Raison fournie par le client lors de l’annulation du rendez-vous.
     * (ex. : "empêchement personnel", "maladie", "changement de plan").
     */
    private String reason;
}
