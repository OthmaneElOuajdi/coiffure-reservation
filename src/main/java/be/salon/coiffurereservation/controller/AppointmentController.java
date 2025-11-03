package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.AppointmentDto;
import be.salon.coiffurereservation.dto.CancelAppointmentRequest;
import be.salon.coiffurereservation.dto.CreateAppointmentRequest;
import be.salon.coiffurereservation.dto.UpdateAppointmentRequest;
import be.salon.coiffurereservation.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur permettant aux utilisateurs de gérer leurs rendez-vous :
 * <ul>
 *     <li>Création d’un rendez-vous,</li>
 *     <li>Consultation de leurs propres rendez-vous,</li>
 *     <li>Mise à jour d’un rendez-vous,</li>
 *     <li>Annulation d’un rendez-vous.</li>
 * </ul>
 *
 * <p>Toutes les routes nécessitent une authentification via JWT (schéma <b>bearerAuth</b>).</p>
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous clients")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Crée un nouveau rendez-vous pour l’utilisateur connecté.
     *
     * @param request         les informations nécessaires à la création du rendez-vous.
     * @param authentication  l’objet d’authentification contenant les informations de l’utilisateur.
     * @return le rendez-vous créé.
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau rendez-vous", description = "Permet à l’utilisateur connecté de réserver un rendez-vous.")
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        AppointmentDto appointment = appointmentService.createAppointment(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    /**
     * Récupère tous les rendez-vous de l’utilisateur actuellement connecté.
     *
     * @param authentication l’objet d’authentification contenant l’utilisateur connecté.
     * @return une liste de {@link AppointmentDto} correspondant aux rendez-vous de l’utilisateur.
     */
    @GetMapping("/mine")
    @Operation(summary = "Récupérer mes rendez-vous", description = "Retourne tous les rendez-vous de l’utilisateur actuellement connecté.")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AppointmentDto> appointments = appointmentService.getUserAppointments(userEmail);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Récupère un rendez-vous par son identifiant (uniquement si celui-ci appartient à l’utilisateur connecté).
     *
     * @param id             identifiant du rendez-vous.
     * @param authentication l’objet d’authentification contenant l’utilisateur connecté.
     * @return le {@link AppointmentDto} correspondant.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rendez-vous par ID", description = "Retourne les détails d’un rendez-vous spécifique appartenant à l’utilisateur.")
    public ResponseEntity<AppointmentDto> getAppointmentById(
            @Parameter(description = "Identifiant du rendez-vous") @PathVariable UUID id,
            Authentication authentication) {

        String userEmail = authentication.getName();
        AppointmentDto appointment = appointmentService.getAppointmentById(id, userEmail);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Met à jour un rendez-vous existant appartenant à l’utilisateur connecté.
     *
     * @param id             identifiant du rendez-vous à modifier.
     * @param request        nouvelles informations à appliquer.
     * @param authentication l’objet d’authentification contenant l’utilisateur connecté.
     * @return le {@link AppointmentDto} mis à jour.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour un rendez-vous", description = "Permet à l’utilisateur de modifier la date, l’heure ou le service d’un rendez-vous existant.")
    public ResponseEntity<AppointmentDto> updateAppointment(
            @Parameter(description = "Identifiant du rendez-vous") @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        AppointmentDto appointment = appointmentService.updateAppointment(id, request, userEmail);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Annule un rendez-vous appartenant à l’utilisateur connecté.
     *
     * @param id             identifiant du rendez-vous à annuler.
     * @param request        optionnel : raison ou commentaire d’annulation.
     * @param authentication l’objet d’authentification contenant l’utilisateur connecté.
     * @return une réponse vide avec le statut HTTP 204.
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler un rendez-vous", description = "Permet à l’utilisateur d’annuler un rendez-vous existant.")
    public ResponseEntity<Void> cancelAppointment(
            @Parameter(description = "Identifiant du rendez-vous à annuler") @PathVariable UUID id,
            @RequestBody(required = false) CancelAppointmentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        if (request == null) {
            request = new CancelAppointmentRequest();
        }
        appointmentService.cancelAppointment(id, request, userEmail);
        return ResponseEntity.noContent().build();
    }
}
