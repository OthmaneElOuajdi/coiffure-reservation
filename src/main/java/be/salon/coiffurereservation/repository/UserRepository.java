package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des utilisateurs {@link User}.
 * <p>
 * Fournit des méthodes de recherche par email, rôle et statut actif.
 * Ces données sont utilisées pour :
 * <ul>
 *     <li>L’authentification et la gestion des comptes</li>
 *     <li>L’administration du personnel et des clients</li>
 *     <li>Le contrôle d’accès basé sur les rôles</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Recherche un utilisateur par son adresse email (identifiant unique).
     *
     * @param email adresse email
     * @return un {@link Optional} contenant l’utilisateur, s’il existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe déjà avec une adresse email donnée.
     *
     * @param email adresse email
     * @return true si l’adresse est déjà utilisée
     */
    boolean existsByEmail(String email);

    /**
     * Retourne la liste de tous les utilisateurs actifs.
     *
     * @return liste des utilisateurs actifs
     */
    List<User> findByActiveTrue();

    /**
     * Retourne tous les utilisateurs ayant un rôle spécifique (actifs ou non).
     *
     * @param role nom du rôle (ex : "ADMIN", "STAFF", "CLIENT")
     * @return liste des utilisateurs possédant ce rôle
     */
    @Query("""
           SELECT u FROM User u
           WHERE :role MEMBER OF u.roles
           """)
    List<User> findByRole(String role);

    /**
     * Retourne uniquement les utilisateurs actifs possédant un rôle spécifique.
     *
     * @param role nom du rôle (ex : "STAFF", "ADMIN")
     * @return liste des utilisateurs actifs avec ce rôle
     */
    @Query("""
           SELECT u FROM User u
           WHERE u.active = true
             AND :role MEMBER OF u.roles
           """)
    List<User> findActiveByRole(String role);
}
