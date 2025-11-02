package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Représente un service ou une prestation proposée par le salon (ex. : coupe, coloration, soin, etc.).
 *
 * <p>Ce DTO est utilisé pour exposer les informations d’un service vers le frontend
 * (liste, détails, création ou modification). Il correspond directement à l’entité
 * {@link be.salon.coiffurereservation.entity.Service}, sans inclure la logique interne ni les métadonnées techniques.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "id": "b5a6d1e2-3c4f-4d5e-9a10-111213141516",
 *   "name": "Coupe femme",
 *   "description": "Shampoing, coupe et brushing",
 *   "durationMinutes": 45,
 *   "priceCents": 2500,
 *   "priceEuros": 25.0,
 *   "active": true,
 *   "displayOrder": 1
 * }
 * </pre>
 *
 * <p>Ce DTO est principalement utilisé par le {@link be.salon.coiffurereservation.service.ServiceService}
 * et exposé via les endpoints REST correspondants.</p>
 *
 * @see be.salon.coiffurereservation.entity.Service
 * @see be.salon.coiffurereservation.service.ServiceService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {

    /** Identifiant unique du service (UUID). */
    private UUID id;

    /** Nom commercial du service (ex. : "Coupe homme", "Balayage"). */
    private String name;

    /** Description détaillée du service (contenu, étapes, options, etc.). */
    private String description;

    /** Durée estimée du service en minutes. */
    private Integer durationMinutes;

    /** Prix du service exprimé en centimes d’euro (stockage en base). */
    private Integer priceCents;

    /** Prix du service converti en euros (valeur calculée côté backend). */
    private Double priceEuros;

    /** Indique si le service est actuellement actif et réservable. */
    private Boolean active;

    /** Ordre d’affichage du service dans la liste (1 = en haut). */
    private Integer displayOrder;
}
