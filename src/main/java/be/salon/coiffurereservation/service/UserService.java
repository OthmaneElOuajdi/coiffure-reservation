package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.User;
import be.salon.coiffurereservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des utilisateurs.
 * <p>
 * Ce service fournit des opérations CRUD et des méthodes de recherche
 * pour gérer les utilisateurs du système (clients, staff, admins).
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return User trouvé
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email adresse email
     * @return User trouvé
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupère tous les utilisateurs actifs.
     * Utilise la méthode findByActiveTrue() du repository.
     *
     * @return liste des utilisateurs actifs
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Récupère tous les utilisateurs ayant un rôle spécifique.
     * Utilise la méthode findByRole() du repository.
     *
     * @param role nom du rôle (ex: "ROLE_CLIENT", "ROLE_STAFF", "ROLE_ADMIN")
     * @return liste des utilisateurs avec ce rôle
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Récupère tous les utilisateurs actifs ayant un rôle spécifique.
     * Utilise la méthode findActiveByRole() du repository.
     *
     * @param role nom du rôle
     * @return liste des utilisateurs actifs avec ce rôle
     */
    public List<User> getActiveUsersByRole(String role) {
        return userRepository.findActiveByRole(role);
    }

    /**
     * Récupère tous les clients actifs.
     *
     * @return liste des clients actifs
     */
    public List<User> getActiveClients() {
        return getActiveUsersByRole("ROLE_CLIENT");
    }

    /**
     * Récupère tous les membres du staff actifs.
     *
     * @return liste du staff actif
     */
    public List<User> getActiveStaff() {
        return getActiveUsersByRole("ROLE_STAFF");
    }

    /**
     * Récupère tous les administrateurs actifs.
     *
     * @return liste des admins actifs
     */
    public List<User> getActiveAdmins() {
        return getActiveUsersByRole("ROLE_ADMIN");
    }

    /**
     * Récupère tous les clients (actifs ou non).
     *
     * @return liste de tous les clients
     */
    public List<User> getAllClients() {
        return getUsersByRole("ROLE_CLIENT");
    }

    /**
     * Récupère tous les membres du staff (actifs ou non).
     *
     * @return liste de tout le staff
     */
    public List<User> getAllStaff() {
        return getUsersByRole("ROLE_STAFF");
    }

    /**
     * Récupère tous les administrateurs (actifs ou non).
     *
     * @return liste de tous les admins
     */
    public List<User> getAllAdmins() {
        return getUsersByRole("ROLE_ADMIN");
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param id   identifiant de l'utilisateur
     * @param user nouvelles données
     * @return User mis à jour
     */
    @Transactional
    public User updateUser(UUID id, User user) {
        User existing = getUserById(id);

        if (user.getFirstName() != null) {
            existing.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existing.setLastName(user.getLastName());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }

        User updated = userRepository.save(existing);
        log.info("User updated: {}", id);
        return updated;
    }

    /**
     * Active un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User activé
     */
    @Transactional
    public User activateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(true);
        User activated = userRepository.save(user);
        log.info("User activated: {}", id);
        return activated;
    }

    /**
     * Désactive un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User désactivé
     */
    @Transactional
    public User deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(false);
        User deactivated = userRepository.save(user);
        log.info("User deactivated: {}", id);
        return deactivated;
    }

    /**
     * Vérifie si un email est déjà utilisé.
     *
     * @param email adresse email à vérifier
     * @return true si l'email existe déjà
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Compte le nombre total d'utilisateurs.
     *
     * @return nombre d'utilisateurs
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * Compte le nombre d'utilisateurs actifs.
     *
     * @return nombre d'utilisateurs actifs
     */
    public long countActiveUsers() {
        return getActiveUsers().size();
    }

    /**
     * Compte le nombre d'utilisateurs par rôle.
     *
     * @param role nom du rôle
     * @return nombre d'utilisateurs avec ce rôle
     */
    public long countUsersByRole(String role) {
        return getUsersByRole(role).size();
    }

    /**
     * Supprime un utilisateur (soft delete - désactivation recommandée).
     *
     * @param id identifiant de l'utilisateur
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("User deleted: {}", id);
    }
}
