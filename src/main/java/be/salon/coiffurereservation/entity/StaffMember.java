package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Représente un membre du personnel du salon de coiffure
 * (coiffeur, coloriste, barbier, manager...).
 *
 * <p>Chaque membre peut posséder :</p>
 * <ul>
 *     <li>Des informations personnelles (nom, email, téléphone, photo)</li>
 *     <li>Un ordre d'affichage dans l'interface</li>
 *     <li>Un ensemble de compétences (services maîtrisés)</li>
 *     <li>Un statut actif/inactif (pour désactiver sans supprimer)</li>
 * </ul>
 *
 * <p>Les compétences sont stockées comme chaînes libres via {@code @ElementCollection},
 * ce qui permet de gérer des services simples sans relation complexe.
 * Une table de liaison Service ⇄ StaffMember pourra être ajoutée plus tard
 * si tu veux un modèle plus strict.</p>
 */
@Entity
@Table(name = "staff_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffMember {

    /** Identifiant unique du membre du personnel. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Prénom du membre du staff. */
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    /** Nom de famille du membre du staff. */
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    /** Adresse e-mail de contact interne. */
    @Column(length = 255)
    private String email;

    /** Numéro de téléphone professionnel. */
    @Column(length = 32)
    private String phone;

    /** Description ou biographie affichée dans l'application. */
    @Column(length = 2000)
    private String bio;

    /** URL vers une photo du membre du personnel. */
    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    /**
     * Ensemble des compétences déclarées du membre du staff
     * (ex : "Coupe homme", "Coloration", "Balayage").
     * Utilisée pour le matching avec les services disponibles.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "staff_skills", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "skill")
    private Set<String> skills;

    /** Indique si le membre du staff peut recevoir des rendez-vous. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Ordre d'affichage dans les interfaces clients. */
    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    /** Date de création de la fiche staff. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date de dernière mise à jour. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------
    // Méthodes utilitaires métier
    // ----------------------------------------------------------

    /** Retourne le nom complet pour affichage marketing. */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
