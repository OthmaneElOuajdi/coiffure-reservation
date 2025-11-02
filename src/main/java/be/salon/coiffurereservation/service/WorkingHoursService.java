package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.StaffMember;
import be.salon.coiffurereservation.entity.WorkingHours;
import be.salon.coiffurereservation.repository.StaffMemberRepository;
import be.salon.coiffurereservation.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des horaires de travail du personnel.
 * <p>
 * Ce service permet de gérer les horaires d'ouverture et de disponibilité
 * des membres du staff, jour par jour.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final StaffMemberRepository staffMemberRepository;

    /**
     * Récupère tous les horaires de travail d'un membre du staff.
     * Utilise la méthode findByStaffMemberId() du repository.
     *
     * @param staffId identifiant du membre du staff
     * @return liste des horaires de la semaine
     */
    public List<WorkingHours> getStaffWorkingHours(UUID staffId) {
        return workingHoursRepository.findByStaffMemberId(staffId);
    }

    /**
     * Récupère les horaires de travail d'un membre du staff pour un jour spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return WorkingHours trouvé ou null si non défini
     */
    public WorkingHours getStaffWorkingHoursForDay(UUID staffId, Integer dayOfWeek) {
        return workingHoursRepository.findByStaffMemberIdAndDayOfWeek(staffId, dayOfWeek)
                .orElse(null);
    }

    /**
     * Récupère les horaires de travail d'un membre du staff pour un jour spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine (enum Java)
     * @return WorkingHours trouvé ou null si non défini
     */
    public WorkingHours getStaffWorkingHoursForDay(UUID staffId, DayOfWeek dayOfWeek) {
        return getStaffWorkingHoursForDay(staffId, dayOfWeek.getValue());
    }

    /**
     * Récupère tous les horaires de travail pour un jour donné (tous les membres du staff).
     * Utilise la méthode findByDayOfWeek() du repository.
     *
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return liste des horaires de tous les employés ce jour-là
     */
    public List<WorkingHours> getWorkingHoursForDay(Integer dayOfWeek) {
        return workingHoursRepository.findByDayOfWeek(dayOfWeek);
    }

    /**
     * Récupère tous les horaires de travail pour un jour donné (tous les membres du staff).
     *
     * @param dayOfWeek jour de la semaine (enum Java)
     * @return liste des horaires de tous les employés ce jour-là
     */
    public List<WorkingHours> getWorkingHoursForDay(DayOfWeek dayOfWeek) {
        return getWorkingHoursForDay(dayOfWeek.getValue());
    }

    /**
     * Crée ou met à jour les horaires de travail d'un membre du staff pour un jour.
     *
     * @param staffId    identifiant du membre du staff
     * @param dayOfWeek  jour de la semaine (1 = lundi, 7 = dimanche)
     * @param startTime  heure de début
     * @param endTime    heure de fin
     * @param breakStart heure de début de pause (optionnel)
     * @param breakEnd   heure de fin de pause (optionnel)
     * @return WorkingHours créé ou mis à jour
     */
    @Transactional
    public WorkingHours setWorkingHours(UUID staffId, Integer dayOfWeek,
                                        LocalTime startTime, LocalTime endTime,
                                        LocalTime breakStart, LocalTime breakEnd) {
        StaffMember staff = staffMemberRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        WorkingHours workingHours = workingHoursRepository
                .findByStaffMemberIdAndDayOfWeek(staffId, dayOfWeek)
                .orElse(WorkingHours.builder()
                        .staffMember(staff)
                        .dayOfWeek(dayOfWeek)
                        .build());

        workingHours.setStartTime(startTime);
        workingHours.setEndTime(endTime);
        workingHours.setBreakStart(breakStart);
        workingHours.setBreakEnd(breakEnd);

        WorkingHours saved = workingHoursRepository.save(workingHours);
        log.info("Working hours set for staff {} on day {}", staffId, dayOfWeek);
        return saved;
    }

    /**
     * Crée ou met à jour les horaires de travail sans pause.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine
     * @param startTime heure de début
     * @param endTime   heure de fin
     * @return WorkingHours créé ou mis à jour
     */
    @Transactional
    public WorkingHours setWorkingHours(UUID staffId, Integer dayOfWeek,
                                        LocalTime startTime, LocalTime endTime) {
        return setWorkingHours(staffId, dayOfWeek, startTime, endTime, null, null);
    }

    /**
     * Définit les mêmes horaires pour tous les jours de la semaine.
     *
     * @param staffId   identifiant du membre du staff
     * @param startTime heure de début
     * @param endTime   heure de fin
     */
    @Transactional
    public void setWeeklyWorkingHours(UUID staffId, LocalTime startTime, LocalTime endTime) {
        for (int day = 1; day <= 7; day++) {
            setWorkingHours(staffId, day, startTime, endTime);
        }
        log.info("Weekly working hours set for staff {}", staffId);
    }

    /**
     * Définit les horaires pour les jours ouvrables (lundi à vendredi).
     *
     * @param staffId   identifiant du membre du staff
     * @param startTime heure de début
     * @param endTime   heure de fin
     */
    @Transactional
    public void setWeekdayWorkingHours(UUID staffId, LocalTime startTime, LocalTime endTime) {
        for (int day = 1; day <= 5; day++) { // Lundi à vendredi
            setWorkingHours(staffId, day, startTime, endTime);
        }
        log.info("Weekday working hours set for staff {}", staffId);
    }

    /**
     * Supprime les horaires de travail d'un membre du staff pour un jour spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine
     */
    @Transactional
    public void deleteWorkingHours(UUID staffId, Integer dayOfWeek) {
        workingHoursRepository.findByStaffMemberIdAndDayOfWeek(staffId, dayOfWeek)
                .ifPresent(workingHours -> {
                    workingHoursRepository.delete(workingHours);
                    log.info("Working hours deleted for staff {} on day {}", staffId, dayOfWeek);
                });
    }

    /**
     * Supprime tous les horaires de travail d'un membre du staff.
     *
     * @param staffId identifiant du membre du staff
     */
    @Transactional
    public void deleteAllStaffWorkingHours(UUID staffId) {
        List<WorkingHours> workingHours = getStaffWorkingHours(staffId);
        workingHoursRepository.deleteAll(workingHours);
        log.info("All working hours deleted for staff {}", staffId);
    }

    /**
     * Vérifie si un membre du staff travaille un jour donné.
     *
     * @param staffId   identifiant du membre du staff
     * @param dayOfWeek jour de la semaine
     * @return true si le staff travaille ce jour
     */
    public boolean isStaffWorkingOnDay(UUID staffId, Integer dayOfWeek) {
        return workingHoursRepository.findByStaffMemberIdAndDayOfWeek(staffId, dayOfWeek)
                .isPresent();
    }

    /**
     * Récupère tous les horaires de travail.
     *
     * @return liste de tous les horaires
     */
    public List<WorkingHours> getAllWorkingHours() {
        return workingHoursRepository.findAll();
    }

    /**
     * Récupère un horaire de travail par son identifiant.
     *
     * @param id identifiant de l'horaire
     * @return WorkingHours trouvé
     * @throws IllegalArgumentException si l'horaire n'existe pas
     */
    public WorkingHours getWorkingHoursById(UUID id) {
        return workingHoursRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Working hours not found"));
    }

    /**
     * Copie les horaires d'un membre du staff vers un autre.
     *
     * @param sourceStaffId identifiant du staff source
     * @param targetStaffId identifiant du staff cible
     */
    @Transactional
    public void copyWorkingHours(UUID sourceStaffId, UUID targetStaffId) {
        // Vérifier que le staff cible existe
        staffMemberRepository.findById(targetStaffId)
                .orElseThrow(() -> new IllegalArgumentException("Target staff member not found"));

        List<WorkingHours> sourceHours = getStaffWorkingHours(sourceStaffId);

        for (WorkingHours source : sourceHours) {
            setWorkingHours(targetStaffId, source.getDayOfWeek(),
                    source.getStartTime(), source.getEndTime(),
                    source.getBreakStart(), source.getBreakEnd());
        }

        log.info("Working hours copied from staff {} to staff {}", sourceStaffId, targetStaffId);
    }
}
