package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.entity.SalonSetting;
import be.salon.coiffurereservation.service.SalonSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur de gestion des paramètres du salon.
 * <p>
 * Permet de configurer les paramètres globaux du salon.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Gestion des paramètres du salon")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class SalonSettingController {

    private final SalonSettingService salonSettingService;

    /**
     * Récupère tous les paramètres du salon.
     *
     * @return liste de tous les paramètres
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les paramètres", description = "Retourne tous les paramètres configurables du salon.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<SalonSetting>> getAllSettings() {
        return ResponseEntity.ok(salonSettingService.getAllSettings());
    }

    /**
     * Récupère un paramètre par sa clé.
     *
     * @param key clé du paramètre
     * @return SalonSetting trouvé
     */
    @GetMapping("/{key}")
    @Operation(summary = "Récupérer un paramètre", description = "Retourne un paramètre par sa clé.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètre récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Paramètre non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<SalonSetting> getSetting(
            @Parameter(description = "Clé du paramètre")
            @PathVariable String key) {
        return ResponseEntity.ok(salonSettingService.getSetting(key));
    }

    /**
     * Récupère la valeur d'un paramètre.
     *
     * @param key clé du paramètre
     * @return valeur du paramètre
     */
    @GetMapping("/{key}/value")
    @Operation(summary = "Récupérer la valeur d'un paramètre", description = "Retourne uniquement la valeur d'un paramètre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Valeur récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Paramètre non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, String>> getSettingValue(
            @Parameter(description = "Clé du paramètre")
            @PathVariable String key) {
        String value = salonSettingService.getSettingValue(key);
        return ResponseEntity.ok(java.util.Map.of("value", value));
    }

    /**
     * Récupère un paramètre booléen.
     *
     * @param key clé du paramètre
     * @return valeur booléenne
     */
    @GetMapping("/{key}/boolean")
    @Operation(summary = "Récupérer un paramètre booléen", description = "Retourne la valeur booléenne d'un paramètre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Valeur récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Boolean>> getBooleanSetting(
            @Parameter(description = "Clé du paramètre")
            @PathVariable String key) {
        boolean value = salonSettingService.getBooleanSetting(key);
        return ResponseEntity.ok(java.util.Map.of("value", value));
    }

    /**
     * Récupère un paramètre entier.
     *
     * @param key          clé du paramètre
     * @param defaultValue valeur par défaut
     * @return valeur entière
     */
    @GetMapping("/{key}/int")
    @Operation(summary = "Récupérer un paramètre entier", description = "Retourne la valeur entière d'un paramètre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Valeur récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, Integer>> getIntSetting(
            @Parameter(description = "Clé du paramètre")
            @PathVariable String key,
            @Parameter(description = "Valeur par défaut")
            @RequestParam(defaultValue = "0") int defaultValue) {
        int value = salonSettingService.getIntSetting(key, defaultValue);
        return ResponseEntity.ok(java.util.Map.of("value", value));
    }

    /**
     * Crée ou met à jour un paramètre.
     *
     * @param key         clé du paramètre
     * @param value       valeur du paramètre
     * @param description description optionnelle
     * @return SalonSetting créé ou mis à jour
     */
    @PostMapping
    @Operation(summary = "Créer ou mettre à jour un paramètre", description = "Crée un nouveau paramètre ou met à jour un existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paramètre créé avec succès"),
            @ApiResponse(responseCode = "200", description = "Paramètre mis à jour avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<SalonSetting> setSetting(
            @Parameter(description = "Clé du paramètre") @RequestParam String key,
            @Parameter(description = "Valeur du paramètre") @RequestParam String value,
            @Parameter(description = "Description du paramètre") @RequestParam(required = false) String description) {
        SalonSetting setting;
        if (description != null) {
            setting = salonSettingService.setSetting(key, value, description);
        } else {
            setting = salonSettingService.setSetting(key, value);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(setting);
    }

    /**
     * Supprime un paramètre.
     *
     * @param key clé du paramètre à supprimer
     * @return statut de succès
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "Supprimer un paramètre", description = "Supprime un paramètre existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paramètre supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Paramètre non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteSetting(
            @Parameter(description = "Clé du paramètre")
            @PathVariable String key) {
        salonSettingService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }

    /**
     * Initialise les paramètres par défaut.
     *
     * @return statut de succès
     */
    @PostMapping("/initialize")
    @Operation(summary = "Initialiser les paramètres par défaut", description = "Crée les paramètres par défaut du salon s'ils n'existent pas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres initialisés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<java.util.Map<String, String>> initializeDefaultSettings() {
        salonSettingService.initializeDefaultSettings();
        return ResponseEntity.ok(java.util.Map.of("message", "Default settings initialized successfully"));
    }
}
