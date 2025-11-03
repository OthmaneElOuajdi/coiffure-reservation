package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.SlotDto;
import be.salon.coiffurereservation.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur permettant de consulter les créneaux horaires disponibles
 * pour un service et un membre du personnel donnés.
 *
 * <p>
 * Ce contrôleur est accessible publiquement afin que les clients puissent
 * visualiser les créneaux libres avant de réserver un rendez-vous.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
@Tag(name = "Créneaux horaires", description = "Consultation des créneaux disponibles pour la réservation")
public class SlotController {

    private final SlotService slotService;

    /**
     * Récupère la liste des créneaux horaires disponibles pour une date donnée,
     * un service spécifique et un membre du personnel.
     *
     * <p>
     * Cette méthode est utilisée par le frontend pour afficher les disponibilités
     * d’un employé à une date précise selon le service sélectionné.
     * </p>
     *
     * @param date      la date du jour concerné (format ISO : yyyy-MM-dd).
     * @param serviceId l’identifiant du service demandé.
     * @param staffId   l’identifiant du membre du personnel.
     * @return une liste de {@link SlotDto} représentant les créneaux disponibles.
     */
    @GetMapping("/available")
    @Operation(
            summary = "Récupérer les créneaux disponibles (public)",
            description = "Retourne la liste des créneaux horaires disponibles pour une date, un service et un employé donnés."
    )
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @Parameter(description = "Date du jour (format ISO : yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Identifiant du service")
            @RequestParam UUID serviceId,
            @Parameter(description = "Identifiant du membre du personnel")
            @RequestParam UUID staffId) {

        List<SlotDto> slots = slotService.getAvailableSlots(date, serviceId, staffId);
        return ResponseEntity.ok(slots);
    }
}
