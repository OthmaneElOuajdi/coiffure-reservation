package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.ServiceDto;
import be.salon.coiffurereservation.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur public permettant d’accéder aux services disponibles dans le salon.
 *
 * <p>
 * Ce contrôleur expose uniquement des endpoints en lecture (GET) accessibles
 * sans authentification pour afficher la liste des services actifs et leurs détails.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Consultation des services du salon (accès public)")
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Récupère la liste de tous les services actuellement actifs dans le salon.
     *
     * @return une liste de {@link ServiceDto} représentant les services disponibles.
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les services actifs (public)",
            description = "Retourne la liste complète des services disponibles et actifs du salon."
    )
    public ResponseEntity<List<ServiceDto>> getAllActiveServices() {
        return ResponseEntity.ok(serviceService.getAllActiveServices());
    }

    /**
     * Récupère un service spécifique à partir de son identifiant unique.
     *
     * @param id identifiant unique du service à récupérer.
     * @return le {@link ServiceDto} correspondant à l’identifiant fourni.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un service par ID",
            description = "Retourne les détails d’un service spécifique à partir de son identifiant unique."
    )
    public ResponseEntity<ServiceDto> getServiceById(
            @Parameter(description = "Identifiant du service recherché") @PathVariable UUID id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }
}
