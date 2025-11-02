package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Représente un membre du personnel du salon (ex. : coiffeur, coloriste, esthéticienne, etc.).
 *
 * <p>Ce DTO est utilisé pour afficher les informations publiques et internes
 * d’un membre du staff, notamment son profil, ses compétences et son statut d’activité.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "id": "b1a2c3d4-e5f6-7890-abcd-ef1234567890",
 *   "firstName": "Sophie",
 *   "lastName": "Martin",
 *   "fullName": "Sophie Martin",
 *   "email": "sophie.martin@salon.be",
 *   "phone": "+32470123456",
 *   "bio": "Coiffeuse spécialisée dans les coupes courtes et les colorations naturelles.",
 *   "photoUrl": "https://cdn.salon.be/images/staff/sophie.jpg",
 *   "skills": ["Coupe", "Coloration", "Brushing"],
 *   "active": true,
 *   "displayOrder": 1
 * }
 * </pre>
 *
 * <p>Ce DTO correspond à l’entité {@link be.salon.coiffurereservation.entity.StaffMember}
 * et est principalement manipulé par le service {@link be.salon.coiffurereservation.service.StaffService}.</p>
 *
 * @see be.salon.coiffurereservation.entity.StaffMember
 * @see be.salon.coiffurereservation.service.StaffService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffMemberDto {

    /** Identifiant unique du membre du personnel. */
    private UUID id;

    /** Prénom du membre du personnel. */
    private String firstName;

    /** Nom de famille du membre du personnel. */
    private String lastName;

    /** Nom complet du membre du personnel (concaténé à partir du prénom et du nom). */
    private String fullName;

    /** Adresse e-mail professionnelle du membre du personnel. */
    private String email;

    /** Numéro de téléphone professionnel (optionnel). */
    private String phone;

    /** Courte biographie ou présentation du membre du staff. */
    private String bio;

    /** URL de la photo de profil du membre du personnel. */
    private String photoUrl;

    /** Ensemble des compétences ou prestations maîtrisées (ex. : “Coloration”, “Balayage”). */
    private Set<String> skills;

    /** Indique si le membre du staff est actif et disponible pour des rendez-vous. */
    private Boolean active;

    /** Ordre d’affichage dans la liste (1 = premier). */
    private Integer displayOrder;
}
