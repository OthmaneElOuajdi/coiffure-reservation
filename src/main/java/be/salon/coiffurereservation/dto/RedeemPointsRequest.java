package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Requête pour échanger des points de fidélité contre un service gratuit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPointsRequest {
    
    /** Identifiant du service choisi pour la récompense */
    private UUID serviceId;
    
    /** Nombre de points à échanger (généralement 1000) */
    private Integer pointsToRedeem;
}
