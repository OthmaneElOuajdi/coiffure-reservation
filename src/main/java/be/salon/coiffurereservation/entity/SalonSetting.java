package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Représente un paramètre configurable du salon.
 *
 * <p>Cette entité permet de modifier facilement le comportement du système
 * sans redéployer l'application. Elle fonctionne sous forme de paires clé/valeur.</p>
 *
 * Exemples de paramètres possibles :
 * <ul>
 *   <li>OPENING_HOUR_TIME = "09:00"</li>
 *   <li>DEFAULT_RESERVATION_DURATION = "00:30"</li>
 *   <li>ENABLE_PAYMENTS = "true"</li>
 * </ul>
 *
 * Les clés doivent être uniques.
 */
@Entity
@Table(name = "salon_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonSetting {

    /** Nom unique de la configuration (ex : OPENING_HOUR_TIME). */
    @Id
    @Column(name = "setting_key", length = 100)
    private String key;

    /** Valeur du paramètre (stockée sous forme de texte libre). */
    @Column(nullable = false, length = 1000)
    private String value;

    /** Description optionnelle du paramètre (but, format, admin notes). */
    @Column(length = 500)
    private String description;

    /** Date de dernière mise à jour. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
