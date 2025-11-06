package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Représente un utilisateur du système (client ou membre du staff).
 *
 * <p>L'email sert d'identifiant unique pour la connexion.</p>
 *
 * <p>Les rôles définissent les permissions d'accès :</p>
 * <ul>
 *   <li>ROLE_CLIENT → prise de rendez-vous</li>
 *   <li>ROLE_STAFF → gestion des rendez-vous assignés</li>
 *   <li>ROLE_ADMIN → accès global + gestion du personnel</li>
 * </ul>
 *
 * <p>Le mot de passe stocké est toujours
 * un hash sécurisé (ex: BCrypt), jamais en clair.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Identifiant unique de l’utilisateur (UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Adresse email unique servant d’identifiant de connexion. */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** Hash du mot de passe (ex: BCrypt) — jamais stocker en clair ! */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Prénom de l’utilisateur (optionnel). */
    @Column(name = "first_name", length = 80)
    private String firstName;

    /** Nom de famille de l’utilisateur (optionnel). */
    @Column(name = "last_name", length = 80)
    private String lastName;

    /** Téléphone de contact du client/staff. */
    @Column(length = 32)
    private String phone;

    /**
     * Rôles attribués à l’utilisateur.
     * Stockés sous forme de chaînes ex: "ROLE_ADMIN".
     * Utilisé par Spring Security pour les autorisations.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    /** Indique si l’adresse email a été vérifiée (gestion confirmation). */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    /** Token de vérification d'email (généré lors de l'inscription). */
    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    /** Date d'expiration du token de vérification. */
    @Column(name = "email_verification_token_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    /** Permet de bloquer un compte sans le supprimer. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Points de fidélité accumulés par le client. */
    @Column(name = "loyalty_points", nullable = false)
    @Builder.Default
    private Integer loyaltyPoints = 0;

    /** Chemin vers la photo de profil de l'utilisateur. */
    @Column(name = "profile_picture")
    private String profilePicture;

    /** Date à laquelle la suppression du compte est programmée (null si pas de suppression prévue). */
    @Column(name = "deletion_scheduled_at")
    private LocalDateTime deletionScheduledAt;

    /** Date de création du compte utilisateur. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date de dernière modification du compte. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // -----------------------------------------------------------------
    // Méthodes métier utilitaires
    // -----------------------------------------------------------------

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role.toUpperCase());
    }
}
