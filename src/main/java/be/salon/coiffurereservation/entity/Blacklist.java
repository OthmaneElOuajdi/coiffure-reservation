package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente une entrée dans la liste noire (blacklist).
 * Contient les emails et numéros de téléphone bannis du système.
 * 
 * <p>Lorsqu'un utilisateur est bloqué, son email et son téléphone
 * sont ajoutés à cette liste pour empêcher toute réinscription.</p>
 */
@Entity
@Table(name = "blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blacklist {

    /** Identifiant unique de l'entrée blacklist (UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Email banni (peut être null si seul le téléphone est banni). */
    @Column(length = 255)
    private String email;

    /** Numéro de téléphone banni (peut être null si seul l'email est banni). */
    @Column(length = 32)
    private String phone;

    /** Raison du bannissement. */
    @Column(length = 500)
    private String reason;

    /** ID de l'utilisateur banni (pour référence). */
    @Column(name = "banned_user_id")
    private UUID bannedUserId;

    /** ID de l'admin qui a effectué le bannissement. */
    @Column(name = "banned_by_admin_id")
    private UUID bannedByAdminId;

    /** Date de création de l'entrée blacklist. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
