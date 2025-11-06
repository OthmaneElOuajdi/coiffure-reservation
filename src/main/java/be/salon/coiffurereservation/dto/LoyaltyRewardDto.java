package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant l'état des points de fidélité et des récompenses disponibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyRewardDto {
    
    /** Points actuels du client */
    private Integer currentPoints;
    
    /** Points nécessaires pour la prochaine récompense */
    private Integer pointsNeeded;
    
    /** Pourcentage de progression vers la prochaine récompense (0-100) */
    private Integer progressPercentage;
    
    /** Nombre de services gratuits disponibles */
    private Integer availableFreeServices;
    
    /** Message descriptif */
    private String message;
}
