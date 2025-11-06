package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.Appointment;
import be.salon.coiffurereservation.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des {@link Appointment}.
 * <p>
 * Cette interface fournit un accès typé aux rendez-vous enregistrés en base,
 * ainsi que plusieurs requêtes personnalisées pour filtrer selon :
 * <ul>
 *     <li>Le client (user)</li>
 *     <li>Le membre du staff (coiffeur)</li>
 *     <li>La période temporelle</li>
 *     <li>Le statut du rendez-vous</li>
 * </ul>
 * <p>
 * Les méthodes par défaut simplifient les appels les plus courants,
 * notamment la recherche des rendez-vous "actifs" (PENDING ou CONFIRMED).
 *
 * @author …
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Retourne les rendez-vous d'un utilisateur ordonnés par date de début décroissante.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des rendez-vous triés par date de début
     */
    List<Appointment> findByUserIdOrderByAppointmentDateDescStartTimeDesc(UUID userId);

    /**
     * Recherche les rendez-vous d’un membre du staff dans une période donnée,
     * filtrés par un ensemble de statuts.
     *
     * @param staffId   identifiant du membre du staff
     * @param startDate date de début
     * @param startTime heure de début
     * @param endDate   date de fin
     * @param endTime   heure de fin
     * @param statuses  statuts à inclure
     * @return liste ordonnée des rendez-vous
     */
    @Query("""
           SELECT a FROM Appointment a
           WHERE a.staffMember.id = :staffId
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.endTime <= :endTime)
             )
             AND a.status IN :statuses
           ORDER BY a.appointmentDate, a.startTime
           """)
    List<Appointment> findByStaffAndDateRange(
            @Param("staffId") UUID staffId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    /**
     * Recherche les rendez-vous sur une période donnée,
     * indépendamment du staff.
     *
     * @param startDate date de début
     * @param startTime heure de début
     * @param endDate   date de fin
     * @param endTime   heure de fin
     * @param statuses  statuts à inclure
     * @return liste ordonnée des rendez-vous
     */
    @Query("""
           SELECT a FROM Appointment a
           WHERE (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.endTime <= :endTime)
             )
             AND a.status IN :statuses
           ORDER BY a.appointmentDate, a.startTime
           """)
    List<Appointment> findByDateRange(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    /**
     * Détecte les rendez-vous en conflit (chevauchement horaire)
     * pour un membre du staff donné.
     *
     * @param staffId   identifiant du membre du staff
     * @param startTime début du nouveau créneau
     * @param endTime   fin du nouveau créneau
     * @param statuses  statuts considérés comme actifs
     * @return liste des rendez-vous en conflit
     */
    @Query("""
           SELECT a FROM Appointment a
           WHERE a.staffMember.id = :staffId
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime < :endTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.endTime > :startTime)
             )
             AND a.status IN :statuses
           """)
    List<Appointment> findConflictingAppointments(
            @Param("staffId") UUID staffId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    /**
     * Recherche les rendez-vous d’un certain statut sur une période donnée.
     *
     * @param status statut ciblé
     * @param start  date/heure de début
     * @param end    date/heure de fin
     * @return liste des rendez-vous correspondants
     */
    @Query("""
           SELECT a FROM Appointment a
           WHERE a.status = :status
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.startTime < :endTime)
             )
           """)
    List<Appointment> findByStatusAndStartTimeBetween(
            @Param("status") AppointmentStatus status,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime
    );

    /**
     * Compte les rendez-vous correspondant à une liste de statuts
     * sur une période donnée.
     *
     * @param statuses statuts à inclure
     * @param start    date/heure de début
     * @param end      date/heure de fin
     * @return nombre de rendez-vous correspondants
     */
    @Query("""
           SELECT COUNT(a) FROM Appointment a
           WHERE a.status IN :statuses
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.startTime < :endTime)
             )
           """)
    Long countWithStatusesBetween(
            @Param("statuses") Collection<AppointmentStatus> statuses,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime
    );

    /**
     * Calcule le chiffre d’affaires (en centimes) généré par les rendez-vous
     * d’un certain ensemble de statuts sur une période donnée.
     *
     * @param statuses statuts à inclure (ex : CONFIRMED, COMPLETED)
     * @param start    date/heure de début
     * @param end      date/heure de fin
     * @return total en centimes (0 si aucun résultat)
     */
    @Query("""
           SELECT COALESCE(SUM(a.service.priceCents), 0) FROM Appointment a
           WHERE a.status IN :statuses
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.startTime < :endTime)
             )
           """)
    Long sumRevenueCentsBetween(
            @Param("statuses") Collection<AppointmentStatus> statuses,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime
    );

    /**
     * Retourne tous les rendez-vous confirmés dans une période donnée.
     *
     * @param start date/heure de début
     * @param end   date/heure de fin
     * @return liste des rendez-vous confirmés
     */
    @Query("""
           SELECT a FROM Appointment a
           WHERE a.status = be.salon.coiffurereservation.entity.AppointmentStatus.CONFIRMED
             AND (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.startTime < :endTime)
             )
           """)
    List<Appointment> findConfirmedBetween(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime
    );

    // ==========================================================
    // Méthodes par défaut pratiques pour les cas d’usage fréquents
    // ==========================================================

    /**
     * Retourne les rendez-vous actifs (PENDING ou CONFIRMED)
     * d’un membre du staff dans une plage temporelle donnée.
     */
    default List<Appointment> findActiveByStaffAndDateRange(UUID staffId, LocalDateTime start, LocalDateTime end) {
        return findByStaffAndDateRange(
                staffId, start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
        );
    }

    /**
     * Retourne les rendez-vous actifs (PENDING ou CONFIRMED)
     * sur une plage temporelle donnée avec toutes les relations chargées.
     * Utilisé pour l'admin dashboard pour éviter les problèmes de lazy loading.
     */
    @Query("""
           SELECT DISTINCT a FROM Appointment a
           LEFT JOIN FETCH a.user
           LEFT JOIN FETCH a.staffMember
           LEFT JOIN FETCH a.service
           WHERE (
               a.appointmentDate > :startDate OR
               (a.appointmentDate = :startDate AND a.startTime >= :startTime)
             )
             AND (
               a.appointmentDate < :endDate OR
               (a.appointmentDate = :endDate AND a.endTime <= :endTime)
             )
             AND a.status IN :statuses
           ORDER BY a.appointmentDate, a.startTime
           """)
    List<Appointment> findByDateRangeWithRelations(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    /**
     * Retourne les rendez-vous actifs (PENDING ou CONFIRMED)
     * sur une plage temporelle donnée.
     */
    default List<Appointment> findActiveByDateRange(LocalDateTime start, LocalDateTime end) {
        return findByDateRangeWithRelations(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
        );
    }

    /**
     * Vérifie s’il existe des conflits (chevauchements) pour un membre du staff
     * sur un créneau donné avec des rendez-vous actifs.
     */
    default List<Appointment> findActiveConflicts(UUID staffId, LocalDateTime start, LocalDateTime end) {
        return findConflictingAppointments(
                staffId, start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
        );
    }

    /**
     * Compte le nombre de rendez-vous confirmés ou complétés sur une période donnée.
     */
    default Long countBookedBetween(LocalDateTime start, LocalDateTime end) {
        return countWithStatusesBetween(
                List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED),
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime()
        );
    }

    /**
     * Calcule le total du chiffre d’affaires (en centimes)
     * pour les rendez-vous confirmés ou complétés sur une période donnée.
     */
    default Long sumRevenueBookedCents(LocalDateTime start, LocalDateTime end) {
        return sumRevenueCentsBetween(
                List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED),
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime()
        );
    }

    /**
     * Trouve tous les rendez-vous avec un statut donné créés avant une date donnée.
     */
    List<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus status, LocalDateTime createdAt);
}
