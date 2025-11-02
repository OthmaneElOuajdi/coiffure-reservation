package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.*;
import be.salon.coiffurereservation.dto.SlotDto;
import be.salon.coiffurereservation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service de calcul des créneaux disponibles pour la prise de rendez-vous.
 * <p>
 * Règles principales :
 * <ul>
 *   <li>Respect des horaires de travail du membre du staff (avec pause éventuelle)</li>
 *   <li>Exclusion des jours de congé</li>
 *   <li>Vérification des conflits avec les rendez-vous déjà planifiés</li>
 *   <li>Respect d'un délai minimal avant réservation ({@code minAdvanceHours})</li>
 *   <li>Création de créneaux par pas de {@code slotIntervalMinutes}</li>
 * </ul>
 * Les résultats peuvent être mis en cache par date/service/staff.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlotService {

    private final ServiceRepository serviceRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final HolidayService holidayService;
    private final AppointmentRepository appointmentRepository;

    /** Pas de génération entre deux slots (en minutes). */
    @Value("${app.booking.slot-interval-minutes:30}")
    private Integer slotIntervalMinutes;

    /** Délai minimal (en heures) entre maintenant et le début d'un rendez-vous réservable. */
    @Value("${app.booking.min-advance-hours:1}")
    private Integer minAdvanceHours;

    /**
     * Calcule la liste des créneaux disponibles pour un service et un membre du staff à une date donnée.
     * <p>
     * Étapes :
     * <ol>
     *   <li>Vérifie l'existence du service et du staff, et que le staff est actif</li>
     *   <li>Exclut la date si le staff est en congé</li>
     *   <li>Récupère les horaires de travail du jour, sinon renvoie vide</li>
     *   <li>Génère des créneaux par pas de {@code slotIntervalMinutes} couvrant la durée du service</li>
     *   <li>Filtre les créneaux en conflit avec les rendez-vous existants</li>
     *   <li>Applique le délai minimal avant réservation</li>
     * </ol>
     *
     * @param date      date ciblée
     * @param serviceId identifiant du service
     * @param staffId   identifiant du membre du staff
     * @return liste de créneaux disponibles (triés chronologiquement)
     * @throws IllegalArgumentException si service ou staff introuvable
     */
    @Cacheable(value = "availableSlots", key = "#date + '-' + #serviceId + '-' + #staffId")
    public List<SlotDto> getAvailableSlots(LocalDate date, UUID serviceId, UUID staffId) {
        be.salon.coiffurereservation.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service non trouvé"));

        StaffMember staff = staffMemberRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Membre du staff non trouvé"));

        if (!staff.getActive()) {
            return new ArrayList<>();
        }

        if (holidayService.isStaffOnHoliday(staffId, date)) {
            log.debug("Staff {} is on holiday on {}", staffId, date);
            return new ArrayList<>();
        }

        int dayOfWeek = date.getDayOfWeek().getValue();
        WorkingHours workingHours = workingHoursRepository
                .findByStaffMemberIdAndDayOfWeek(staffId, dayOfWeek)
                .orElse(null);

        if (workingHours == null) {
            log.debug("No working hours for staff {} on day {}", staffId, dayOfWeek);
            return new ArrayList<>();
        }

        List<SlotDto> slots = generateSlots(date, workingHours, service, staff);

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Appointment> existingAppointments = appointmentRepository
                .findActiveByStaffAndDateRange(staffId, dayStart, dayEnd);

        return slots.stream()
                .peek(slot -> {
                    boolean isAvailable = existingAppointments.stream()
                            .noneMatch(appt -> isOverlapping(slot.getStartTime(), slot.getEndTime(),
                                    appt.getAppointmentDate().atTime(appt.getStartTime()),
                                    appt.getAppointmentDate().atTime(appt.getEndTime())));
                    slot.setAvailable(isAvailable);
                })
                .filter(SlotDto::getAvailable)
                .toList();
    }

    /**
     * Génère les créneaux bruts (sans tenir compte des rendez-vous existants) en respectant
     * les horaires, la pause éventuelle, la durée du service et le délai minimal.
     *
     * @param date         date ciblée
     * @param workingHours horaires du staff pour le jour
     * @param service      service à planifier
     * @param staff        membre du staff
     * @return liste de slots potentiels (possiblement encore en conflit)
     */
    private List<SlotDto> generateSlots(LocalDate date, WorkingHours workingHours,
                                        be.salon.coiffurereservation.entity.Service service, StaffMember staff) {
        List<SlotDto> slots = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minStartTime = now.plusHours(minAdvanceHours);

        LocalTime currentTime = workingHours.getStartTime();
        LocalTime endTime = workingHours.getEndTime();

        // on autorise un slot si (current + durée) == endTime (borne incluse).
        while (currentTime.plusMinutes(service.getDurationMinutes()).isBefore(endTime)
                || currentTime.plusMinutes(service.getDurationMinutes()).equals(endTime)) {

            // sauter la plage de pause
            if (workingHours.hasBreak() &&
                    !currentTime.isBefore(workingHours.getBreakStart()) &&
                    currentTime.isBefore(workingHours.getBreakEnd())) {
                currentTime = currentTime.plusMinutes(slotIntervalMinutes);
                continue;
            }

            LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(service.getDurationMinutes());

            // respect du délai minimal
            if (slotStart.isAfter(minStartTime)) {
                slots.add(SlotDto.builder()
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .staffId(staff.getId())
                        .staffName(staff.getFullName())
                        .available(true)
                        .build());
            }

            currentTime = currentTime.plusMinutes(slotIntervalMinutes);
        }

        return slots;
    }

    /**
     * Indique si deux intervalles se chevauchent strictement.
     *
     * @param start1 début 1
     * @param end1   fin 1
     * @param start2 début 2
     * @param end2   fin 2
     * @return {@code true} si chevauchement, sinon {@code false}
     */
    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                  LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
