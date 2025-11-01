package be.salon.coiffurereservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Journalisation des actions sensibles dans le système.<br>
 * Cette entité permet de tracer :
 * <ul>
 *   <li>Quel utilisateur a réalisé une action</li>
 *   <li>À quel moment</li>
 *   <li>Sur quelle entité du domaine</li>
 *   <li>Depuis quelle adresse IP et quel device</li>
 *   <li>Avec un contexte additionnel encapsulé en JSON</li>
 * </ul>
 *
 * Exemples d'actions :
 * <pre>
 * - "APPOINTMENT_CREATED"
 * - "USER_LOGIN_FAILED"
 * - "PAYMENT_REFUNDED"
 * </pre>
 *
 * Cette table est conçue pour faciliter les audits sécurité & conformité légale.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    /** Identifiant unique du log d’audit. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Utilisateur ayant déclenché l’action (peut être null si anonyme/externe). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** Type d’action exécutée (ex: LOGIN_SUCCESS, APPOINTMENT_CANCELLED...) */
    @Column(nullable = false, length = 100)
    private String action;

    /** Type de l'entité impactée (ex: Appointment, Payment...). */
    @Column(name = "entity_type", length = 50)
    private String entityType;

    /** Identifiant de l'entité impactée. */
    @Column(name = "entity_id")
    private UUID entityId;

    /**
     * Informations contextuelles optionnelles (avant/après, causes, diffs...)
     * Stockées au format JSONB dans PostgreSQL, JSON dans H2.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details")
    private Map<String, Object> details;

    /** Adresse IP de l’utilisateur ayant généré l’événement. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** User-Agent HTTP permettant d’identifier le device & navigateur. */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /** Horodatage automatique de la création du log. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
