package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <p>Entité représentant une demande de remboursement effectuée par un utilisateur
 * pour un rendez-vous donné. Cette entité conserve la raison du remboursement,
 * les fichiers justificatifs, le statut de la demande ainsi que les métadonnées
 * de traitement par un administrateur.</p>
 *
 * <p>Chaque {@code RefundRequest} est liée à un {@link Appointment} et à un {@link User}.
 * Elle peut être traitée par un administrateur qui ajoute un commentaire et met à jour
 * le statut et la date de traitement.</p>
 *
 * @author
 * @version 1.0
 * @since 2025-11
 */
@Entity
@Table(name = "refund_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {

    /**
     * Identifiant unique de la demande de remboursement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Rendez-vous concerné par la demande de remboursement.
     * <p>Relation ManyToOne car plusieurs demandes peuvent être liées à un même rendez-vous.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /**
     * Utilisateur ayant soumis la demande de remboursement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Raison justifiant la demande de remboursement.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * Chemin du fichier justificatif associé à la demande (si applicable).
     */
    @Column(name = "justification_file_path")
    private String justificationFilePath;

    /**
     * Nom du fichier justificatif (tel qu’il a été uploadé par l’utilisateur).
     */
    @Column(name = "justification_file_name")
    private String justificationFileName;

    /**
     * Statut actuel de la demande de remboursement.
     * <p>Valeur par défaut : {@link RefundStatus#PENDING}</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RefundStatus status = RefundStatus.PENDING;

    /**
     * Commentaire ajouté par un administrateur lors du traitement de la demande.
     */
    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    /**
     * Administrateur ayant traité la demande de remboursement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    /**
     * Date et heure du traitement de la demande par un administrateur.
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Date de création de la demande de remboursement.
     * <p>Définie automatiquement lors de la création de l’entité.</p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière mise à jour de la demande.
     * <p>Définie automatiquement à chaque modification de l’entité.</p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Initialise les champs {@code createdAt} et {@code updatedAt}
     * avant la persistance initiale dans la base de données.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Met à jour le champ {@code updatedAt} avant toute mise à jour
     * dans la base de données.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Énumération représentant les différents statuts possibles
     * d'une demande de remboursement.
     */
    public enum RefundStatus {
        /** En attente de traitement. */
        PENDING,
        /** Demande approuvée par un administrateur. */
        APPROVED,
        /** Demande rejetée. */
        REJECTED,
        /** Remboursement effectué avec succès. */
        REFUNDED
    }
}
