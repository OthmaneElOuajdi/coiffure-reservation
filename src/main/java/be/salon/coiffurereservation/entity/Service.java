package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un service/prestation proposé par le salon de coiffure.
 *
 * <p>Chaque service possède :</p>
 * <ul>
 *   <li>un nom (ex : Coupe Femme, Brushing, Coloration)</li>
 *   <li>Une durée estimée en minutes</li>
 *   <li>Un prix défini en centimes (pour éviter les erreurs d’arrondi)</li>
 *   <li>Un ordre d’affichage dans la liste des services</li>
 *   <li>Un statut actif ou non pour masquer temporairement la prestation</li>
 * </ul>
 *
 * Il est recommandé d’utiliser un DTO côté API pour éviter d’exposer
 * des champs internes (comme displayOrder).
 */
@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    /** Identifiant unique de la prestation. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nom visible du service (ex : “Coupe homme”). */
    @Column(nullable = false, length = 120)
    private String name;

    /** Détails marketing ou techniques de la prestation. */
    @Column(length = 2000)
    private String description;

    /** Durée prévue de la prestation, en minutes. */
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /** Prix exprimé en centimes (ex : 1999 → 19,99€). */
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    /** Indique si le service peut être réservé par les clients. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Ordre de tri pour l’affichage dans l’interface. */
    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    /** Points de fidélité gagnés lors de la réservation de ce service. */
    @Column(name = "loyalty_points")
    @Builder.Default
    private Integer loyaltyPoints = 0;

    /** Timestamp de création du service. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp de dernière mise à jour. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // ---------------------------------------
    // Méthodes utilitaires
    // ---------------------------------------

    /**
     * Retourne le prix en euros.
     *
     * @return prix exprimé en EUR
     */
    public Double getPriceEuros() {
        return priceCents != null ? priceCents / 100.0 : 0.0;
    }
}
