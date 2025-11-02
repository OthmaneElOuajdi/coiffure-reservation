package be.salon.coiffurereservation.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Réponse renvoyée au client après une authentification réussie
 * (inscription ou connexion).
 *
 * <p>Ce DTO contient le jeton JWT d’accès ainsi que les
 * informations principales de l’utilisateur connecté.
 * Il est utilisé par le frontend pour stocker le token
 * et afficher les données du profil utilisateur.</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "email": "client@exemple.com",
 *   "firstName": "Marie",
 *   "lastName": "Dupont",
 *   "roles": ["ROLE_CLIENT"]
 * }
 * </pre>
 *
 * <p>Le champ <code>token</code> doit être transmis dans l’en-tête
 * <code>Authorization</code> de chaque requête sécurisée sous la forme :
 * <code>Bearer &lt;token&gt;</code>.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** Jeton JWT d’authentification permettant d’accéder aux endpoints sécurisés. */
    private String token;

    /** Adresse e-mail de l’utilisateur authentifié. */
    private String email;

    /** Prénom de l’utilisateur. */
    private String firstName;

    /** Nom de famille de l’utilisateur. */
    private String lastName;

    /** Liste des rôles attribués à l’utilisateur (ex. : ROLE_CLIENT, ROLE_ADMIN). */
    private List<String> roles;
}
