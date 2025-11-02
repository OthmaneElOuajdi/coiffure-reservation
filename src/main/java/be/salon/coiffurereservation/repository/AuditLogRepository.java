package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des journaux d’audit {@link AuditLog}.
 * <p>
 * Cette interface permet de consulter les événements liés aux actions des utilisateurs,
 * comme les connexions, paiements, annulations ou modifications d’un rendez-vous.
 * <br>
 * Les méthodes intégrées facilitent la recherche par utilisateur, par type d’action
 * ou sur une période temporelle donnée.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Retourne les événements d’audit d’un utilisateur spécifique,
     * classés du plus récent au plus ancien.
     *
     * @param userId identifiant de l’utilisateur
     * @return liste des journaux correspondants
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Retourne les événements correspondant à une action donnée
     * (ex : "LOGIN", "BOOK_APPOINTMENT", "CANCEL_APPOINTMENT"),
     * triés du plus récent au plus ancien.
     *
     * @param action nom de l’action enregistrée
     * @return liste des journaux correspondants
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Retourne les événements enregistrés dans une période temporelle donnée.
     *
     * @param start date/heure de début
     * @param end   date/heure de fin
     * @return liste des journaux correspondants, triés par date de création décroissante
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    /**
     * Retourne les 100 derniers événements d’audit (les plus récents).
     *
     * @return liste des 100 derniers journaux d’audit
     */
    List<AuditLog> findTop100ByOrderByCreatedAtDesc();
}
