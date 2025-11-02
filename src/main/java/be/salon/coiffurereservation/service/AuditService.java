package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.AuditLog;
import be.salon.coiffurereservation.entity.User;
import be.salon.coiffurereservation.repository.AuditLogRepository;
import be.salon.coiffurereservation.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service de gestion des journaux d'audit.
 * <p>
 * Ce service permet d'enregistrer et de consulter les événements importants
 * du système (connexions, créations/modifications/suppressions de rendez-vous,
 * paiements, etc.).
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Enregistre une action d'audit avec les informations de l'utilisateur connecté.
     *
     * @param userEmail email de l'utilisateur
     * @param action    type d'action (ex: "APPOINTMENT_CREATED")
     * @param entityType type d'entité concernée (ex: "Appointment")
     * @param entityId  identifiant de l'entité
     * @param details   détails additionnels (optionnel)
     */
    @Transactional
    public void logAction(String userEmail, String action, String entityType, UUID entityId, Map<String, Object> details) {
        User user = userRepository.findByEmail(userEmail).orElse(null);

        HttpServletRequest request = getCurrentRequest();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log created: action={}, user={}, entityType={}, entityId={}",
                action, userEmail, entityType, entityId);
    }

    /**
     * Enregistre une action d'audit sans utilisateur (action système ou anonyme).
     *
     * @param action    type d'action
     * @param entityType type d'entité concernée
     * @param entityId  identifiant de l'entité
     * @param details   détails additionnels (optionnel)
     */
    @Transactional
    public void logSystemAction(String action, String entityType, UUID entityId, Map<String, Object> details) {
        HttpServletRequest request = getCurrentRequest();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.info("System audit log created: action={}, entityType={}, entityId={}",
                action, entityType, entityId);
    }

    /**
     * Récupère les logs d'audit d'un utilisateur spécifique.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des logs d'audit
     */
    public List<AuditLog> getUserAuditLogs(UUID userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Récupère les logs d'audit par type d'action.
     *
     * @param action type d'action
     * @return liste des logs d'audit
     */
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * Récupère les logs d'audit dans une période donnée.
     *
     * @param start date/heure de début
     * @param end   date/heure de fin
     * @return liste des logs d'audit
     */
    public List<AuditLog> getAuditLogsBetween(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    /**
     * Récupère les 100 derniers logs d'audit.
     *
     * @return liste des 100 derniers logs
     */
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    /**
     * Récupère la requête HTTP courante.
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Extrait l'adresse IP du client depuis la requête HTTP.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Si plusieurs IPs (proxy chain), prendre la première
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
