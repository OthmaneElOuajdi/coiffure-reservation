package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.StaffMemberDto;
import be.salon.coiffurereservation.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur public permettant de consulter les membres du personnel du salon.
 *
 * <p>
 * Ce contrôleur expose des endpoints accessibles sans authentification
 * afin de permettre aux clients de voir le personnel disponible et leurs compétences.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Tag(name = "Personnel", description = "Consultation des membres du personnel du salon")
public class StaffController {

    private final StaffService staffService;

    /**
     * Récupère la liste de tous les membres du personnel actuellement actifs.
     *
     * @return une liste de {@link StaffMemberDto} représentant les employés actifs.
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les membres du personnel actifs (public)",
            description = "Retourne la liste complète du personnel actif pouvant être sélectionné pour un rendez-vous."
    )
    public ResponseEntity<List<StaffMemberDto>> getAllActiveStaff() {
        return ResponseEntity.ok(staffService.getAllActiveStaff());
    }

    /**
     * Récupère un membre du personnel à partir de son identifiant unique.
     *
     * @param id identifiant du membre du personnel.
     * @return le {@link StaffMemberDto} correspondant à cet identifiant.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un membre du personnel par ID",
            description = "Retourne les informations détaillées d’un membre du personnel à partir de son identifiant."
    )
    public ResponseEntity<StaffMemberDto> getStaffById(
            @Parameter(description = "Identifiant du membre du personnel")
            @PathVariable UUID id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    /**
     * Récupère la liste des membres du personnel possédant une compétence spécifique.
     *
     * <p>
     * Exemple : <code>/api/v1/staff/by-skill/coloration</code> retournera
     * tous les employés ayant la compétence "coloration".
     * </p>
     *
     * @param skill nom de la compétence recherchée.
     * @return une liste de {@link StaffMemberDto} ayant la compétence demandée.
     */
    @GetMapping("/by-skill/{skill}")
    @Operation(
            summary = "Récupérer les membres du personnel par compétence",
            description = "Retourne les membres du personnel correspondant à une compétence spécifique (ex. : coiffure, coloration, manucure...)."
    )
    public ResponseEntity<List<StaffMemberDto>> getStaffBySkill(
            @Parameter(description = "Nom de la compétence recherchée (ex : coiffure, coloration, manucure)")
            @PathVariable String skill) {
        return ResponseEntity.ok(staffService.getStaffBySkill(skill));
    }
}
