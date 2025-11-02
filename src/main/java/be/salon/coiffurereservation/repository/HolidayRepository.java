package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des jours fériés et congés {@link Holiday}.
 * <p>
 * Permet de gérer :
 * <ul>
 *     <li>Les congés spécifiques à un membre du staff</li>
 *     <li>Les jours de fermeture du salon (globaux)</li>
 *     <li>La recherche de jours fériés dans une période donnée</li>
 * </ul>
 * <p>
 * Ces données sont utilisées notamment pour empêcher les prises de rendez-vous
 * pendant les congés du personnel ou les fermetures du salon.
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {

    /**
     * Recherche tous les jours fériés ou congés applicables à un membre du staff
     * pour une date donnée.
     * <p>
     * Inclut à la fois :
     * <ul>
     *     <li>Les congés globaux du salon (staffMember = NULL)</li>
     *     <li>Et ceux du membre du staff concerné</li>
     * </ul>
     *
     * @param staffId identifiant du membre du staff
     * @param date    date à vérifier
     * @return liste des jours fériés correspondant
     */
    @Query("""
           SELECT h FROM Holiday h
           WHERE (h.staffMember IS NULL OR h.staffMember.id = :staffId)
             AND h.startDate <= :date
             AND h.endDate >= :date
           """)
    List<Holiday> findHolidaysForStaffOnDate(@Param("staffId") UUID staffId,
                                             @Param("date") LocalDate date);

    /**
     * Recherche les jours de fermeture globaux du salon pour une date donnée.
     * <p>
     * Ces jours ne concernent pas un membre du staff en particulier.
     *
     * @param date date à vérifier
     * @return liste des jours fériés globaux du salon
     */
    @Query("""
           SELECT h FROM Holiday h
           WHERE h.staffMember IS NULL
             AND h.startDate <= :date
             AND h.endDate >= :date
           """)
    List<Holiday> findSalonHolidaysOnDate(@Param("date") LocalDate date);

    /**
     * Retourne la liste des congés pour un membre du staff spécifique.
     *
     * @param staffId identifiant du membre du staff
     * @return liste des congés du staff
     */
    List<Holiday> findByStaffMemberId(UUID staffId);

    /**
     * Retourne la liste des jours fériés ou fermetures globales du salon.
     *
     * @return liste des congés globaux (staffMember = NULL)
     */
    List<Holiday> findByStaffMemberIsNull();

    /**
     * Recherche tous les jours fériés (globaux ou personnels)
     * à venir à partir d'une date donnée.
     *
     * @param fromDate date de départ de la recherche
     * @return liste des congés futurs, triés par date de début croissante
     */
    @Query("""
           SELECT h FROM Holiday h
           WHERE h.endDate >= :fromDate
           ORDER BY h.startDate
           """)
    List<Holiday> findUpcomingHolidays(@Param("fromDate") LocalDate fromDate);
}
