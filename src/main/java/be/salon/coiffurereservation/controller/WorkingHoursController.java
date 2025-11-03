package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.entity.WorkingHours;
import be.salon.coiffurereservation.service.WorkingHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur de gestion des horaires de travail du personnel.
 * <p>
 * Permet de configurer les horaires d'ouverture du salon par membre du staff.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/working-hours")
@RequiredArgsConstructor
@Tag(name = "Working Hours", description = "Gestion des horaires de travail")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    /**
     * Récupère tous les horaires de travail.
     *
     * @return liste de tous les horaires
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les horaires", description = "Retourne tous les horaires de travail.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<WorkingHours>> getAllWorkingHours() {
        return ResponseEntity.ok(workingHoursService.getAllWorkingHours());
    }

    /**
     * Récupère un horaire par son identifiant.
     *
     * @param id identifiant de l'horaire
     * @return WorkingHours trouvé
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un horaire", description = "Retourne un horaire de travail par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaire récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Horaire non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<WorkingHours> getWorkingHoursById(
            @Parameter(description = "Identifiant de l'horaire")
            @PathVariable UUID id) {
        return ResponseEntity.ok(workingHoursService.getWorkingHoursById(id));
    }

    /**
     * Récupère les horaires d'un membre du staff.
     *
     * @param staffId identifiant du membre du staff
     * @return liste des horaires de la semaine
     */
    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Récupérer les horaires d'un staff", description = "Retourne tous les horaires de travail d'un membre du personnel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<WorkingHours>> getStaffWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId) {
        return ResponseEntity.ok(workingHoursService.getStaffWorkingHours(staffId));
    }

    /**
     * Récupère les horaires d'un staff pour un jour spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return WorkingHours trouvé ou 404
     */
    @GetMapping("/staff/{staffId}/day/{dayOfWeek}")
    @Operation(summary = "Récupérer les horaires d'un jour", description = "Retourne les horaires d'un staff pour un jour spécifique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Horaires non définis pour ce jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<WorkingHours> getStaffWorkingHoursForDay(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Jour de la semaine (1 = lundi, 7 = dimanche)")
            @PathVariable Integer dayOfWeek) {
        WorkingHours workingHours = workingHoursService.getStaffWorkingHoursForDay(staffId, dayOfWeek);
        if (workingHours == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(workingHours);
    }

    /**
     * Récupère les horaires de tous les employés pour un jour donné.
     *
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return liste des horaires
     */
    @GetMapping("/day/{dayOfWeek}")
    @Operation(summary = "Récupérer les horaires d'un jour", description = "Retourne les horaires de tous les employés pour un jour donné.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<WorkingHours>> getWorkingHoursForDay(
            @Parameter(description = "Jour de la semaine (1 = lundi, 7 = dimanche)")
            @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(workingHoursService.getWorkingHoursForDay(dayOfWeek));
    }

    /**
     * Récupère les horaires d'un staff pour un jour spécifique (enum).
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine (MONDAY, TUESDAY, etc.)
     * @return WorkingHours trouvé ou 404
     */
    @GetMapping("/staff/{staffId}/day-enum/{dayOfWeek}")
    @Operation(summary = "Récupérer les horaires d'un jour (enum)", description = "Retourne les horaires d'un staff pour un jour spécifique (format enum).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Horaires non définis pour ce jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<WorkingHours> getStaffWorkingHoursForDayEnum(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Jour de la semaine (MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)")
            @PathVariable DayOfWeek dayOfWeek) {
        WorkingHours workingHours = workingHoursService.getStaffWorkingHoursForDay(staffId, dayOfWeek);
        if (workingHours == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(workingHours);
    }

    /**
     * Récupère les horaires de tous les employés pour un jour donné (enum).
     *
     * @param dayOfWeek jour de la semaine (MONDAY, TUESDAY, etc.)
     * @return liste des horaires
     */
    @GetMapping("/day-enum/{dayOfWeek}")
    @Operation(summary = "Récupérer les horaires d'un jour (enum)", description = "Retourne les horaires de tous les employés pour un jour donné (format enum).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<WorkingHours>> getWorkingHoursForDayEnum(
            @Parameter(description = "Jour de la semaine (MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)")
            @PathVariable DayOfWeek dayOfWeek) {
        return ResponseEntity.ok(workingHoursService.getWorkingHoursForDay(dayOfWeek));
    }

    /**
     * Définit les horaires de travail pour un jour.
     *
     * @param staffId    identifiant du membre du staff
     * @param dayOfWeek  jour de la semaine (1 = lundi, 7 = dimanche)
     * @param startTime  heure de début
     * @param endTime    heure de fin
     * @param breakStart heure de début de pause (optionnel)
     * @param breakEnd   heure de fin de pause (optionnel)
     * @return WorkingHours créé ou mis à jour
     */
    @PostMapping("/staff/{staffId}/day/{dayOfWeek}")
    @Operation(summary = "Définir les horaires d'un jour", description = "Crée ou met à jour les horaires de travail d'un staff pour un jour.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Horaires créés avec succès"),
            @ApiResponse(responseCode = "200", description = "Horaires mis à jour avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<WorkingHours> setWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Jour de la semaine (1 = lundi, 7 = dimanche)")
            @PathVariable Integer dayOfWeek,
            @Parameter(description = "Heure de début, ex: 09:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @Parameter(description = "Heure de fin, ex: 18:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @Parameter(description = "Heure de début de pause, ex: 12:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime breakStart,
            @Parameter(description = "Heure de fin de pause, ex: 13:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime breakEnd) {
        WorkingHours workingHours = workingHoursService.setWorkingHours(staffId, dayOfWeek, startTime, endTime, breakStart, breakEnd);
        return ResponseEntity.status(HttpStatus.CREATED).body(workingHours);
    }

    /**
     * Définit les mêmes horaires pour toute la semaine.
     *
     * @param staffId   identifiant du membre du staff
     * @param startTime heure de début
     * @param endTime   heure de fin
     * @return statut de succès
     */
    @PostMapping("/staff/{staffId}/weekly")
    @Operation(summary = "Définir les horaires hebdomadaires", description = "Définit les mêmes horaires pour tous les jours de la semaine.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires définis avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, String>> setWeeklyWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Heure de début, ex: 09:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @Parameter(description = "Heure de fin, ex: 18:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        workingHoursService.setWeeklyWorkingHours(staffId, startTime, endTime);
        return ResponseEntity.ok(java.util.Map.of("message", "Weekly working hours set successfully"));
    }

    /**
     * Définit les horaires pour les jours ouvrables (lundi à vendredi).
     *
     * @param staffId   identifiant du membre du staff
     * @param startTime heure de début
     * @param endTime   heure de fin
     * @return statut de succès
     */
    @PostMapping("/staff/{staffId}/weekdays")
    @Operation(summary = "Définir les horaires des jours ouvrables", description = "Définit les horaires pour les jours ouvrables (lundi à vendredi).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires définis avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, String>> setWeekdayWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Heure de début, ex: 09:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @Parameter(description = "Heure de fin, ex: 18:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        workingHoursService.setWeekdayWorkingHours(staffId, startTime, endTime);
        return ResponseEntity.ok(java.util.Map.of("message", "Weekday working hours set successfully"));
    }

    /**
     * Copie les horaires d'un staff vers un autre.
     *
     * @param sourceStaffId identifiant du staff source
     * @param targetStaffId identifiant du staff cible
     * @return statut de succès
     */
    @PostMapping("/copy")
    @Operation(summary = "Copier les horaires", description = "Copie les horaires de travail d'un staff vers un autre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horaires copiés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, String>> copyWorkingHours(
            @Parameter(description = "Identifiant du staff source")
            @RequestParam UUID sourceStaffId,
            @Parameter(description = "Identifiant du staff cible")
            @RequestParam UUID targetStaffId) {
        workingHoursService.copyWorkingHours(sourceStaffId, targetStaffId);
        return ResponseEntity.ok(java.util.Map.of("message", "Working hours copied successfully"));
    }

    /**
     * Vérifie si un staff travaille un jour donné.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine
     * @return true si le staff travaille ce jour
     */
    @GetMapping("/staff/{staffId}/day/{dayOfWeek}/is-working")
    @Operation(summary = "Vérifier si un staff travaille", description = "Vérifie si un membre du staff travaille un jour donné.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Boolean>> isStaffWorkingOnDay(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Jour de la semaine (1 = lundi, 7 = dimanche)")
            @PathVariable Integer dayOfWeek) {
        boolean isWorking = workingHoursService.isStaffWorkingOnDay(staffId, dayOfWeek);
        return ResponseEntity.ok(java.util.Map.of("isWorking", isWorking));
    }

    /**
     * Supprime les horaires d'un jour spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine
     * @return statut de succès
     */
    @DeleteMapping("/staff/{staffId}/day/{dayOfWeek}")
    @Operation(summary = "Supprimer les horaires d'un jour", description = "Supprime les horaires de travail d'un staff pour un jour spécifique.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horaires supprimés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId,
            @Parameter(description = "Jour de la semaine (1 = lundi, 7 = dimanche)")
            @PathVariable Integer dayOfWeek) {
        workingHoursService.deleteWorkingHours(staffId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime tous les horaires d'un staff.
     *
     * @param staffId identifiant du membre du staff
     * @return statut de succès
     */
    @DeleteMapping("/staff/{staffId}")
    @Operation(summary = "Supprimer tous les horaires d'un staff", description = "Supprime tous les horaires de travail d'un membre du personnel.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horaires supprimés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteAllStaffWorkingHours(
            @Parameter(description = "Identifiant du membre du staff")
            @PathVariable UUID staffId) {
        workingHoursService.deleteAllStaffWorkingHours(staffId);
        return ResponseEntity.noContent().build();
    }
}
