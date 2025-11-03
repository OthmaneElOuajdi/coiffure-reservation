package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.AppointmentDto;
import be.salon.coiffurereservation.dto.ServiceDto;
import be.salon.coiffurereservation.dto.StaffMemberDto;
import be.salon.coiffurereservation.dto.StatsDto;
import be.salon.coiffurereservation.service.AppointmentService;
import be.salon.coiffurereservation.service.ServiceService;
import be.salon.coiffurereservation.service.StaffService;
import be.salon.coiffurereservation.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur d'administration permettant la gestion :
 * <ul>
 *     <li>Des services proposés,</li>
 *     <li>Du personnel,</li>
 *     <li>Des rendez-vous,</li>
 *     <li>Et des statistiques hebdomadaires.</li>
 * </ul>
 *
 * <p>Toutes les routes de ce contrôleur nécessitent le rôle <b>ADMIN</b>.</p>
 * <p>Sécurité : authentification via JWT (schéma <b>bearerAuth</b>).</p>
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Points d'accès d'administration du salon")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ServiceService serviceService;
    private final StaffService staffService;
    private final AppointmentService appointmentService;
    private final StatsService statsService;
    private final be.salon.coiffurereservation.service.AuditService auditService;

    // ========= Services =========

    /**
     * Récupère la liste de tous les services (actifs et inactifs).
     *
     * @return une liste de {@link ServiceDto}.
     */
    @GetMapping("/services")
    @Operation(summary = "Récupérer tous les services", description = "Retourne tous les services, y compris ceux désactivés.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Services récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    /**
     * Crée un nouveau service.
     *
     * @param dto les informations du service à créer.
     * @return le {@link ServiceDto} nouvellement créé.
     */
    @PostMapping("/services")
    @Operation(summary = "Créer un service", description = "Ajoute un nouveau service au catalogue.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service créé"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceDto dto) {
        ServiceDto created = serviceService.createService(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Met à jour un service existant.
     *
     * @param id  identifiant du service à mettre à jour.
     * @param dto données à mettre à jour.
     * @return le {@link ServiceDto} mis à jour.
     */
    @PutMapping("/services/{id}")
    @Operation(summary = "Mettre à jour un service", description = "Modifie les informations d’un service existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service mis à jour"),
            @ApiResponse(responseCode = "404", description = "Service introuvable"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ServiceDto> updateService(
            @Parameter(description = "Identifiant du service") @PathVariable UUID id,
            @Valid @RequestBody ServiceDto dto) {
        ServiceDto updated = serviceService.updateService(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un service par son identifiant.
     *
     * @param id identifiant du service à supprimer.
     * @return une réponse vide avec le statut 204.
     */
    @DeleteMapping("/services/{id}")
    @Operation(summary = "Supprimer un service", description = "Supprime un service existant à partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service supprimé"),
            @ApiResponse(responseCode = "404", description = "Service introuvable"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteService(@Parameter(description = "Identifiant du service") @PathVariable UUID id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // ========= Personnel =========

    /**
     * Récupère la liste de tout le personnel (actif et inactif).
     *
     * @return une liste de {@link StaffMemberDto}.
     */
    @GetMapping("/staff")
    @Operation(summary = "Récupérer tout le personnel", description = "Retourne la liste complète du personnel, actif ou non.")
    public ResponseEntity<List<StaffMemberDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    /**
     * Crée un nouveau membre du personnel.
     *
     * @param dto les informations du membre à créer.
     * @return le {@link StaffMemberDto} créé.
     */
    @PostMapping("/staff")
    @Operation(summary = "Créer un membre du personnel", description = "Ajoute un nouveau membre du personnel.")
    public ResponseEntity<StaffMemberDto> createStaff(@Valid @RequestBody StaffMemberDto dto) {
        StaffMemberDto created = staffService.createStaff(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Met à jour les informations d’un membre du personnel.
     *
     * @param id  identifiant du membre.
     * @param dto données à mettre à jour.
     * @return le {@link StaffMemberDto} mis à jour.
     */
    @PutMapping("/staff/{id}")
    @Operation(summary = "Mettre à jour un membre du personnel", description = "Modifie les informations d’un membre du personnel.")
    public ResponseEntity<StaffMemberDto> updateStaff(
            @Parameter(description = "Identifiant du membre du personnel") @PathVariable UUID id,
            @Valid @RequestBody StaffMemberDto dto) {
        StaffMemberDto updated = staffService.updateStaff(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un membre du personnel par son identifiant.
     *
     * @param id identifiant du membre à supprimer.
     * @return une réponse vide avec le statut 204.
     */
    @DeleteMapping("/staff/{id}")
    @Operation(summary = "Supprimer un membre du personnel", description = "Supprime un membre du personnel existant.")
    public ResponseEntity<Void> deleteStaff(@Parameter(description = "Identifiant du membre du personnel") @PathVariable UUID id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    // ========= Rendez-vous =========

    /**
     * Récupère tous les rendez-vous dans un intervalle de dates donné.
     *
     * @param start date et heure de début (format ISO-8601).
     * @param end   date et heure de fin (format ISO-8601).
     * @return une liste de {@link AppointmentDto}.
     */
    @GetMapping("/appointments")
    @Operation(summary = "Récupérer les rendez-vous par période", description = "Retourne tous les rendez-vous entre deux dates données.")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDateRange(
            @Parameter(description = "Date/heure de début au format ISO, ex : 2025-10-30T09:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Date/heure de fin au format ISO, ex : 2025-10-30T18:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<AppointmentDto> appointments = appointmentService.getAppointmentsByDateRange(start, end);
        return ResponseEntity.ok(appointments);
    }

    // ========= Statistiques =========

    /**
     * Récupère les statistiques hebdomadaires pour une période donnée.
     *
     * @param startOfWeek début de la semaine (format ISO-8601).
     * @param endOfWeek   fin de la semaine (format ISO-8601).
     * @return un objet {@link StatsDto} contenant les statistiques.
     */
    @GetMapping("/stats/weekly")
    @Operation(summary = "Récupérer les statistiques hebdomadaires", description = "Retourne les statistiques du salon sur une période d'une semaine.")
    public ResponseEntity<StatsDto> getWeeklyStats(
            @Parameter(description = "Début de la semaine, ex : 2025-10-27T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startOfWeek,
            @Parameter(description = "Fin de la semaine, ex : 2025-11-02T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endOfWeek) {

        StatsDto stats = statsService.getWeeklyStats(startOfWeek, endOfWeek);
        return ResponseEntity.ok(stats);
    }

    // ========= Audit Logs =========

    /**
     * Récupère les 100 derniers logs d'audit.
     *
     * @return liste des logs d'audit récents
     */
    @GetMapping("/audit/recent")
    @Operation(summary = "Récupérer les logs d'audit récents", description = "Retourne les 100 derniers événements d'audit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<be.salon.coiffurereservation.entity.AuditLog>> getRecentAuditLogs() {
        return ResponseEntity.ok(auditService.getRecentAuditLogs());
    }

    /**
     * Récupère les logs d'audit d'un utilisateur spécifique.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des logs d'audit de l'utilisateur
     */
    @GetMapping("/audit/user/{userId}")
    @Operation(summary = "Récupérer les logs d'un utilisateur", description = "Retourne tous les événements d'audit d'un utilisateur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<be.salon.coiffurereservation.entity.AuditLog>> getUserAuditLogs(
            @Parameter(description = "Identifiant de l'utilisateur")
            @PathVariable UUID userId) {
        return ResponseEntity.ok(auditService.getUserAuditLogs(userId));
    }

    /**
     * Récupère les logs d'audit par type d'action.
     *
     * @param action type d'action (ex: "USER_LOGIN", "APPOINTMENT_CREATED")
     * @return liste des logs d'audit pour cette action
     */
    @GetMapping("/audit/action/{action}")
    @Operation(summary = "Récupérer les logs par action", description = "Retourne tous les événements d'audit d'un type d'action spécifique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<be.salon.coiffurereservation.entity.AuditLog>> getAuditLogsByAction(
            @Parameter(description = "Type d'action (ex: USER_LOGIN, APPOINTMENT_CREATED)")
            @PathVariable String action) {
        return ResponseEntity.ok(auditService.getAuditLogsByAction(action));
    }

    /**
     * Récupère les logs d'audit dans une période donnée.
     *
     * @param start date/heure de début
     * @param end   date/heure de fin
     * @return liste des logs d'audit dans la période
     */
    @GetMapping("/audit/period")
    @Operation(summary = "Récupérer les logs par période", description = "Retourne tous les événements d'audit dans une période donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<be.salon.coiffurereservation.entity.AuditLog>> getAuditLogsBetween(
            @Parameter(description = "Date/heure de début, ex : 2025-10-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Date/heure de fin, ex : 2025-10-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getAuditLogsBetween(start, end));
    }

    /**
     * Enregistre un événement d'audit système.
     *
     * @param action     type d'action
     * @param entityType type d'entité
     * @param entityId   identifiant de l'entité
     * @return statut de succès
     */
    @PostMapping("/audit/system")
    @Operation(summary = "Enregistrer un événement système", description = "Crée un log d'audit pour une action système.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Log créé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> logSystemAction(
            @Parameter(description = "Type d'action") @RequestParam String action,
            @Parameter(description = "Type d'entité") @RequestParam String entityType,
            @Parameter(description = "Identifiant de l'entité") @RequestParam UUID entityId,
            @Parameter(description = "Détails additionnels") @RequestParam(required = false) java.util.Map<String, Object> details) {
        auditService.logSystemAction(action, entityType, entityId, details);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
