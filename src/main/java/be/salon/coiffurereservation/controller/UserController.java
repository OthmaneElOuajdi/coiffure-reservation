package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.entity.User;
import be.salon.coiffurereservation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur de gestion des utilisateurs.
 * <p>
 * Permet aux administrateurs de gérer les comptes utilisateurs.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion des utilisateurs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les utilisateurs
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les utilisateurs", description = "Retourne tous les utilisateurs du système.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return User trouvé
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur", description = "Retourne un utilisateur par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email adresse email
     * @return User trouvé
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un utilisateur par email", description = "Retourne un utilisateur par son adresse email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Adresse email")
            @PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Récupère tous les utilisateurs actifs.
     *
     * @return liste des utilisateurs actifs
     */
    @GetMapping("/active")
    @Operation(summary = "Récupérer les utilisateurs actifs", description = "Retourne tous les utilisateurs actifs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    /**
     * Récupère tous les clients actifs.
     *
     * @return liste des clients actifs
     */
    @GetMapping("/clients/active")
    @Operation(summary = "Récupérer les clients actifs", description = "Retourne tous les clients actifs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clients récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getActiveClients() {
        return ResponseEntity.ok(userService.getActiveClients());
    }

    /**
     * Récupère tous les membres du staff actifs.
     *
     * @return liste du staff actif
     */
    @GetMapping("/staff/active")
    @Operation(summary = "Récupérer le staff actif", description = "Retourne tous les membres du staff actifs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getActiveStaff() {
        return ResponseEntity.ok(userService.getActiveStaff());
    }

    /**
     * Récupère tous les administrateurs actifs.
     *
     * @return liste des admins actifs
     */
    @GetMapping("/admins/active")
    @Operation(summary = "Récupérer les admins actifs", description = "Retourne tous les administrateurs actifs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admins récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getActiveAdmins() {
        return ResponseEntity.ok(userService.getActiveAdmins());
    }

    /**
     * Récupère tous les clients.
     *
     * @return liste de tous les clients
     */
    @GetMapping("/clients")
    @Operation(summary = "Récupérer tous les clients", description = "Retourne tous les clients (actifs et inactifs).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clients récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getAllClients() {
        return ResponseEntity.ok(userService.getAllClients());
    }

    /**
     * Récupère tous les membres du staff.
     *
     * @return liste de tout le staff
     */
    @GetMapping("/staff")
    @Operation(summary = "Récupérer tout le staff", description = "Retourne tous les membres du staff (actifs et inactifs).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getAllStaff() {
        return ResponseEntity.ok(userService.getAllStaff());
    }

    /**
     * Récupère tous les administrateurs.
     *
     * @return liste de tous les admins
     */
    @GetMapping("/admins")
    @Operation(summary = "Récupérer tous les admins", description = "Retourne tous les administrateurs (actifs et inactifs).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admins récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }

    /**
     * Met à jour un utilisateur.
     *
     * @param id   identifiant de l'utilisateur
     * @param user nouvelles données
     * @return User mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour les informations d'un utilisateur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<User> updateUser(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID id,
            @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    /**
     * Active un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User activé
     */
    @PostMapping("/{id}/activate")
    @Operation(summary = "Activer un utilisateur", description = "Active un compte utilisateur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur activé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<User> activateUser(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    /**
     * Désactive un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User désactivé
     */
    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un utilisateur", description = "Désactive un compte utilisateur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur désactivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<User> deactivateUser(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    /**
     * Vérifie si un email existe.
     *
     * @param email adresse email à vérifier
     * @return true si l'email existe
     */
    @GetMapping("/check-email")
    @Operation(summary = "Vérifier si un email existe", description = "Vérifie si une adresse email est déjà utilisée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Boolean>> emailExists(
            @Parameter(description = "Adresse email")
            @RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(java.util.Map.of("exists", exists));
    }

    /**
     * Compte le nombre total d'utilisateurs.
     *
     * @return nombre d'utilisateurs
     */
    @GetMapping("/count")
    @Operation(summary = "Compter les utilisateurs", description = "Retourne le nombre total d'utilisateurs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comptage effectué avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Long>> countUsers() {
        long count = userService.countUsers();
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * Compte le nombre d'utilisateurs actifs.
     *
     * @return nombre d'utilisateurs actifs
     */
    @GetMapping("/count/active")
    @Operation(summary = "Compter les utilisateurs actifs", description = "Retourne le nombre d'utilisateurs actifs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comptage effectué avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Long>> countActiveUsers() {
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * Compte le nombre d'utilisateurs par rôle.
     *
     * @param role nom du rôle
     * @return nombre d'utilisateurs avec ce rôle
     */
    @GetMapping("/count/role")
    @Operation(summary = "Compter les utilisateurs par rôle", description = "Retourne le nombre d'utilisateurs ayant un rôle spécifique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comptage effectué avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Long>> countUsersByRole(
            @Parameter(description = "Nom du rôle (ex: ROLE_CLIENT)")
            @RequestParam String role) {
        long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return statut de succès
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un compte utilisateur (recommandé: désactiver plutôt que supprimer).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
