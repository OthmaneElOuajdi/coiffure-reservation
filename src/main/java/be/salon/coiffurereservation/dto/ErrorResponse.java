package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Représente la réponse standard d’erreur renvoyée par l’API.
 *
 * <p>Cette classe est utilisée par le gestionnaire global des exceptions
 * pour formater les erreurs de manière uniforme dans toutes les réponses HTTP.
 * Elle permet de fournir des informations détaillées sur la cause de l’erreur,
 * le statut HTTP associé et le chemin de la requête concernée.</p>
 *
 * <h3>Structure JSON typique :</h3>
 * <pre>
 * {
 *   "timestamp": "2025-10-29T14:35:42.152",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed",
 *   "path": "/api/appointments",
 *   "validationErrors": {
 *     "startTime": "Start time must be in the future",
 *     "serviceId": "Service ID is required"
 *   }
 * }
 * </pre>
 *
 * <p>Le champ <code>validationErrors</code> est optionnel et
 * uniquement présent en cas d’erreurs de validation.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** Date et heure où l’erreur s’est produite. */
    private LocalDateTime timestamp;

    /** Code de statut HTTP (ex. : 400, 404, 500). */
    private Integer status;

    /** Libellé de l’erreur HTTP (ex. : "Bad Request", "Not Found"). */
    private String error;

    /** Message d’erreur descriptif. */
    private String message;

    /** Chemin de l’endpoint ayant généré l’erreur. */
    private String path;

    /** Détails des erreurs de validation (clé = champ, valeur = message). */
    private Map<String, String> validationErrors;

    /**
     * Crée une instance simplifiée d’erreur sans détails de validation.
     *
     * @param status  le code de statut HTTP
     * @param error   le libellé d’erreur HTTP
     * @param message le message d’erreur
     * @param path    le chemin de la requête concernée
     * @return une instance {@link ErrorResponse} prête à être renvoyée
     */
    public static ErrorResponse of(Integer status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
