package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.RefundRequest;
import be.salon.coiffurereservation.entity.RefundRequest.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, String> {

    /**
     * Trouve toutes les demandes de remboursement par statut.
     */
    List<RefundRequest> findByStatus(RefundStatus status);

    /**
     * Trouve toutes les demandes de remboursement d'un utilisateur.
     */
    @Query("SELECT r FROM RefundRequest r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<RefundRequest> findByUserId(@Param("userId") UUID userId);

    /**
     * Trouve toutes les demandes de remboursement pour un rendez-vous.
     */
    @Query("SELECT r FROM RefundRequest r WHERE r.appointment.id = :appointmentId ORDER BY r.createdAt DESC")
    List<RefundRequest> findByAppointmentId(@Param("appointmentId") UUID appointmentId);

    /**
     * Trouve une demande de remboursement en attente pour un rendez-vous.
     */
    @Query("SELECT r FROM RefundRequest r WHERE r.appointment.id = :appointmentId AND r.status = 'PENDING'")
    Optional<RefundRequest> findPendingByAppointmentId(@Param("appointmentId") UUID appointmentId);

    /**
     * Trouve toutes les demandes de remboursement en attente.
     */
    @Query("SELECT r FROM RefundRequest r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<RefundRequest> findAllPending();

    /**
     * Compte les demandes de remboursement en attente.
     */
    @Query("SELECT COUNT(r) FROM RefundRequest r WHERE r.status = 'PENDING'")
    long countPending();

    /**
     * Calcule le montant total remboursé (approuvé ou remboursé) sur une période donnée.
     */
    @Query("SELECT COALESCE(SUM(a.service.priceCents), 0) FROM RefundRequest r " +
           "JOIN r.appointment a " +
           "WHERE (r.status = 'APPROVED' OR r.status = 'REFUNDED') " +
           "AND r.processedAt >= :start AND r.processedAt < :end")
    Long sumRefundedAmountCents(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
