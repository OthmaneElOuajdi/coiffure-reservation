package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des horaires de travail {@link WorkingHours}.
 * <p>
 * Permet de récupérer les heures d’ouverture et disponibilités du personnel,
 * jour par jour ou par employé. Ces données sont utilisées pour :
 * <ul>
 *     <li>La planification des rendez-vous,</li>
 *     <li>Le calcul des créneaux disponibles,</li>
 *     <li>Et la gestion des horaires côté administration.</li>
 * </ul>
 */
@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {

    /**
     * Récupère les horaires de travail d’un membre du personnel spécifique.
     *
     * @param staffId identifiant du membre du personnel
     * @return liste des horaires associés à cet employé
     */
    List<WorkingHours> findByStaffMemberId(UUID staffId);

    /**
     * Récupère les horaires d’un membre du personnel pour un jour spécifique.
     *
     * @param staffId identifiant du membre du personnel
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return horaire correspondant, s’il existe
     */
    Optional<WorkingHours> findByStaffMemberIdAndDayOfWeek(UUID staffId, Integer dayOfWeek);

    /**
     * Récupère les horaires de travail pour un jour donné pour tout le personnel.
     *
     * @param dayOfWeek jour de la semaine (1 = lundi, 7 = dimanche)
     * @return liste des horaires de tous les employés ce jour-là
     */
    List<WorkingHours> findByDayOfWeek(Integer dayOfWeek);
}
