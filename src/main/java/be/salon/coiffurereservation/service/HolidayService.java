package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.Holiday;
import be.salon.coiffurereservation.entity.StaffMember;
import be.salon.coiffurereservation.repository.HolidayRepository;
import be.salon.coiffurereservation.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des jours fériés et congés.
 * <p>
 * Ce service permet de gérer les périodes d'indisponibilité :
 * <ul>
 *   <li>Fermetures globales du salon (jours fériés, vacances)</li>
 *   <li>Congés individuels des membres du staff</li>
 *   <li>Vérification de disponibilité pour la prise de rendez-vous</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final StaffMemberRepository staffMemberRepository;

    /**
     * Crée un jour férié ou une période de congé.
     *
     * @param holiday entité Holiday à créer
     * @return Holiday créé
     */
    @Transactional
    public Holiday createHoliday(Holiday holiday) {
        Holiday saved = holidayRepository.save(holiday);
        log.info("Holiday created: {} from {} to {}",
                saved.getName(), saved.getStartDate(), saved.getEndDate());
        return saved;
    }

    /**
     * Crée un congé pour un membre du staff spécifique.
     *
     * @param staffId   identifiant du membre du staff
     * @param name      nom/description du congé
     * @param startDate date de début
     * @param endDate   date de fin
     * @return Holiday créé
     */
    @Transactional
    public Holiday createStaffHoliday(UUID staffId, String name, LocalDate startDate, LocalDate endDate) {
        StaffMember staff = staffMemberRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        Holiday holiday = Holiday.builder()
                .staffMember(staff)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .isRecurring(false)
                .build();

        return createHoliday(holiday);
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
    @Transactional
    public Holiday createSalonHoliday(String name, LocalDate startDate, LocalDate endDate, boolean isRecurring) {
        Holiday holiday = Holiday.builder()
                .staffMember(null) // Fermeture globale
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .isRecurring(isRecurring)
                .build();

        return createHoliday(holiday);
    }

    /**
     * Vérifie si un membre du staff est en congé à une date donnée.
     *
     * @param staffId identifiant du membre du staff
     * @param date    date à vérifier
     * @return true si le staff est en congé (ou si le salon est fermé)
     */
    public boolean isStaffOnHoliday(UUID staffId, LocalDate date) {
        List<Holiday> holidays = holidayRepository.findHolidaysForStaffOnDate(staffId, date);
        return !holidays.isEmpty();
    }

    /**
     * Vérifie si le salon est fermé à une date donnée.
     *
     * @param date date à vérifier
     * @return true si le salon est fermé
     */
    public boolean isSalonClosed(LocalDate date) {
        List<Holiday> salonHolidays = holidayRepository.findSalonHolidaysOnDate(date);
        return !salonHolidays.isEmpty();
    }

    /**
     * Récupère tous les congés d'un membre du staff.
     *
     * @param staffId identifiant du membre du staff
     * @return liste des congés
     */
    public List<Holiday> getStaffHolidays(UUID staffId) {
        return holidayRepository.findByStaffMemberId(staffId);
    }

    /**
     * Récupère toutes les fermetures globales du salon.
     *
     * @return liste des jours fériés du salon
     */
    public List<Holiday> getSalonHolidays() {
        return holidayRepository.findByStaffMemberIsNull();
    }

    /**
     * Récupère tous les jours fériés à venir à partir d'aujourd'hui.
     *
     * @return liste des congés futurs
     */
    public List<Holiday> getUpcomingHolidays() {
        return holidayRepository.findUpcomingHolidays(LocalDate.now());
    }

    /**
     * Récupère tous les jours fériés à venir à partir d'une date donnée.
     *
     * @param fromDate date de départ
     * @return liste des congés futurs
     */
    public List<Holiday> getUpcomingHolidaysFrom(LocalDate fromDate) {
        return holidayRepository.findUpcomingHolidays(fromDate);
    }

    /**
     * Récupère les congés applicables à un staff pour une date donnée.
     *
     * @param staffId identifiant du membre du staff
     * @param date    date à vérifier
     * @return liste des congés (globaux + personnels)
     */
    public List<Holiday> getHolidaysForStaffOnDate(UUID staffId, LocalDate date) {
        return holidayRepository.findHolidaysForStaffOnDate(staffId, date);
    }

    /**
     * Récupère tous les jours fériés.
     *
     * @return liste de tous les congés
     */
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    /**
     * Récupère un jour férié par son identifiant.
     *
     * @param id identifiant du congé
     * @return Holiday trouvé
     */
    public Holiday getHolidayById(UUID id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found"));
    }

    /**
     * Met à jour un jour férié existant.
     *
     * @param id      identifiant du congé
     * @param holiday nouvelles données
     * @return Holiday mis à jour
     */
    @Transactional
    public Holiday updateHoliday(UUID id, Holiday holiday) {
        Holiday existing = getHolidayById(id);

        existing.setName(holiday.getName());
        existing.setStartDate(holiday.getStartDate());
        existing.setEndDate(holiday.getEndDate());
        existing.setIsRecurring(holiday.getIsRecurring());

        if (holiday.getStaffMember() != null) {
            existing.setStaffMember(holiday.getStaffMember());
        }

        Holiday updated = holidayRepository.save(existing);
        log.info("Holiday updated: {}", id);
        return updated;
    }

    /**
     * Supprime un jour férié.
     *
     * @param id identifiant du congé à supprimer
     */
    @Transactional
    public void deleteHoliday(UUID id) {
        Holiday holiday = getHolidayById(id);
        holidayRepository.delete(holiday);
        log.info("Holiday deleted: {}", id);
    }
}
