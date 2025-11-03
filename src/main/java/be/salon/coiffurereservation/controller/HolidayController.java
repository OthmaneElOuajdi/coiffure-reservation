package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.entity.Holiday;
import be.salon.coiffurereservation.service.HolidayService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur de gestion des jours fériés et congés.
 * <p>
 * Permet de gérer les fermetures du salon et les congés du personnel.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/holidays")
@RequiredArgsConstructor
@Tag(name = "Holidays", description = "Gestion des jours fériés et congés")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class HolidayController {

    private final HolidayService holidayService;

    /**
     * Récupère tous les jours fériés.
     *
     * @return liste de tous les congés
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les jours fériés", description = "Retourne tous les jours fériés et congés.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jours fériés récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    /**
     * Récupère un jour férié par son identifiant.
     *
     * @param id identifiant du congé
     * @return Holiday trouvé
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un jour férié", description = "Retourne un jour férié par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jour férié récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Jour férié non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Holiday> getHolidayById(
            @Parameter(description = "Identifiant du jour férié")
            @PathVariable UUID id) {
        return ResponseEntity.ok(holidayService.getHolidayById(id));
    }

    /**
     * Récupère les congés d'un membre du staff.
     *
     * @param staffId identifiant du membre du staff
     * @return liste des congés du staff
     */
    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Récupérer les congés d'un staff", description = "Retourne tous les congés d'un membre du personnel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Congés récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getStaffHolidays(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId) {
        return ResponseEntity.ok(holidayService.getStaffHolidays(staffId));
    }

    /**
     * Récupère les fermetures globales du salon.
     *
     * @return liste des jours fériés du salon
     */
    @GetMapping("/salon")
    @Operation(summary = "Récupérer les fermetures du salon", description = "Retourne toutes les fermetures globales du salon.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fermetures récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getSalonHolidays() {
        return ResponseEntity.ok(holidayService.getSalonHolidays());
    }

    /**
     * Récupère les jours fériés à venir.
     *
     * @return liste des congés futurs
     */
    @GetMapping("/upcoming")
    @Operation(summary = "Récupérer les jours fériés à venir", description = "Retourne tous les jours fériés futurs à partir d'aujourd'hui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jours fériés récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getUpcomingHolidays() {
        return ResponseEntity.ok(holidayService.getUpcomingHolidays());
    }

    /**
     * Récupère les jours fériés à venir à partir d'une date donnée.
     *
     * @param fromDate date de départ
     * @return liste des congés futurs
     */
    @GetMapping("/upcoming-from")
    @Operation(summary = "Récupérer les jours fériés à partir d'une date", description = "Retourne tous les jours fériés à partir d'une date donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jours fériés récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getUpcomingHolidaysFrom(
            @Parameter(description = "Date de départ, ex : 2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        return ResponseEntity.ok(holidayService.getUpcomingHolidaysFrom(fromDate));
    }

    /**
     * Récupère les congés applicables à un staff pour une date donnée.
     *
     * @param staffId identifiant du membre du staff
     * @param date    date à vérifier
     * @return liste des congés (globaux + personnels)
     */
    @GetMapping("/staff/{staffId}/on-date")
    @Operation(summary = "Vérifier les congés d'un staff à une date", description = "Retourne les congés applicables à un staff pour une date donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Congés récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<Holiday>> getHolidaysForStaffOnDate(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Date à vérifier, ex : 2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(holidayService.getHolidaysForStaffOnDate(staffId, date));
    }

    /**
     * Vérifie si le salon est fermé à une date donnée.
     *
     * @param date date à vérifier
     * @return true si le salon est fermé
     */
    @GetMapping("/salon/is-closed")
    @Operation(summary = "Vérifier si le salon est fermé", description = "Vérifie si le salon est fermé à une date donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Boolean>> isSalonClosed(
            @Parameter(description = "Date à vérifier, ex : 2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isClosed = holidayService.isSalonClosed(date);
        return ResponseEntity.ok(java.util.Map.of("closed", isClosed));
    }

    /**
     * Vérifie si un staff est en congé à une date donnée.
     *
     * @param staffId identifiant du membre du staff
     * @param date    date à vérifier
     * @return true si le staff est en congé
     */
    @GetMapping("/staff/{staffId}/is-on-holiday")
    @Operation(summary = "Vérifier si un staff est en congé", description = "Vérifie si un membre du staff est en congé à une date donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Boolean>> isStaffOnHoliday(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Date à vérifier, ex : 2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean onHoliday = holidayService.isStaffOnHoliday(staffId, date);
        return ResponseEntity.ok(java.util.Map.of("onHoliday", onHoliday));
    }

    /**
     * Crée un congé pour un membre du staff.
     *
     * @param staffId   identifiant du membre du staff
     * @param name      nom/description du congé
     * @param startDate date de début
     * @param endDate   date de fin
     * @return Holiday créé
     */
    @PostMapping("/staff/{staffId}")
    @Operation(summary = "Créer un congé pour un staff", description = "Crée un congé pour un membre du personnel.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Congé créé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Holiday> createStaffHoliday(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Nom du congé") @RequestParam String name,
            @Parameter(description = "Date de début, ex : 2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin, ex : 2025-11-07")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Holiday holiday = holidayService.createStaffHoliday(staffId, name, startDate, endDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(holiday);
    }

    /**
     * Crée une fermeture globale du salon.
     *
     * @param name        nom du jour férié
     * @param startDate   date de début
     * @param endDate     date de fin
     * @param isRecurring si le jour férié se répète chaque année
     * @return Holiday créé
     */
    @PostMapping("/salon")
    @Operation(summary = "Créer une fermeture du salon", description = "Crée une fermeture globale du salon.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fermeture créée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Holiday> createSalonHoliday(
            @Parameter(description = "Nom du jour férié") @RequestParam String name,
            @Parameter(description = "Date de début, ex : 2025-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin, ex : 2025-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Si le jour férié se répète chaque année")
            @RequestParam(defaultValue = "false") boolean isRecurring) {
        Holiday holiday = holidayService.createSalonHoliday(name, startDate, endDate, isRecurring);
        return ResponseEntity.status(HttpStatus.CREATED).body(holiday);
    }

    /**
     * Met à jour un jour férié existant.
     *
     * @param id      identifiant du congé
     * @param holiday nouvelles données
     * @return Holiday mis à jour
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un jour férié", description = "Met à jour un jour férié existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jour férié mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Jour férié non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Holiday> updateHoliday(
            @Parameter(description = "Identifiant du jour férié")
            @PathVariable UUID id,
            @Valid @RequestBody Holiday holiday) {
        Holiday updated = holidayService.updateHoliday(id, holiday);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un jour férié.
     *
     * @param id identifiant du congé à supprimer
     * @return statut de succès
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un jour férié", description = "Supprime un jour férié existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jour férié supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Jour férié non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteHoliday(
            @Parameter(description = "Identifiant du jour férié")
            @PathVariable UUID id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}
