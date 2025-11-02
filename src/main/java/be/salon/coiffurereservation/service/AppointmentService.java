package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.*;
import be.salon.coiffurereservation.dto.AppointmentDto;
import be.salon.coiffurereservation.dto.CancelAppointmentRequest;
import be.salon.coiffurereservation.dto.CreateAppointmentRequest;
import be.salon.coiffurereservation.dto.UpdateAppointmentRequest;
import be.salon.coiffurereservation.mapper.AppointmentMapper;
import be.salon.coiffurereservation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service centralisant la logique métier pour la gestion des rendez-vous (appointments).
 * <p>
 * Responsabilités principales :
 * <ul>
 *   <li>Créer, lire, mettre à jour et annuler des rendez-vous</li>
 *   <li>Valider les règles métier (délai minimal de réservation, annulation, conflits de créneaux)</li>
 *   <li>Notifier via email (confirmation/annulation)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;
    private final AuditService auditService;

    /**
     * Délai minimal (en heures) à respecter entre maintenant et l'heure de début d'un rendez-vous.
     * Défaut : 1h.
     */
    @Value("${app.booking.min-advance-hours:1}")
    private Integer minAdvanceHours;

    /**
     * Délai (en heures) avant l'heure de début à partir duquel une annulation n'est plus autorisée.
     * Défaut : 24h.
     */
    @Value("${app.booking.cancellation-hours:24}")
    private Integer cancellationHours;

    /**
     * Crée un rendez-vous pour l'utilisateur identifié par son email.
     *
     * @param request   données de création (service, staff, date/heure, notes)
     * @param userEmail email de l'utilisateur demandeur
     * @return DTO du rendez-vous créé
     * @throws IllegalArgumentException si l'utilisateur, le service ou le staff n'existe pas,
     *                                  si le créneau est invalide ou en conflit
     */
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        be.salon.coiffurereservation.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        StaffMember staff = staffMemberRepository.findById(request.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        validateAppointmentTime(request.getStartTime());

        LocalDateTime endTime = request.getStartTime().plusMinutes(service.getDurationMinutes());

        checkForConflicts(staff.getId(), request.getStartTime(), endTime);

        Appointment appointment = Appointment.builder()
                .user(user)
                .staffMember(staff)
                .service(service)
                .appointmentDate(request.getStartTime().toLocalDate())
                .startTime(request.getStartTime().toLocalTime())
                .endTime(endTime.toLocalTime())
                .status(AppointmentStatus.PENDING)
                .notes(request.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("Appointment created: {} for user {}", appointment.getId(), userEmail);

        // Audit log
        auditService.logAction(userEmail, "APPOINTMENT_CREATED", "Appointment", appointment.getId(),
                java.util.Map.of(
                        "serviceId", service.getId(),
                        "staffId", staff.getId(),
                        "startTime", request.getStartTime().toString()
                ));

        emailService.sendAppointmentConfirmation(appointment);

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Retourne la liste des rendez-vous d'un utilisateur, triés par date de début décroissante.
     *
     * @param userEmail email de l'utilisateur
     * @return liste de DTOs de rendez-vous
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public List<AppointmentDto> getUserAppointments(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Appointment> appointments =
                appointmentRepository.findByUserIdOrderByAppointmentDateDescStartTimeDesc(user.getId());
        return appointmentMapper.toDtoList(appointments);
    }

    /**
     * Récupère un rendez-vous par son identifiant en contrôlant l'accès (propriétaire ou admin).
     *
     * @param id        identifiant du rendez-vous
     * @param userEmail email du demandeur (doit être propriétaire ou admin)
     * @return DTO du rendez-vous
     * @throws IllegalArgumentException si le rendez-vous n'existe pas ou accès non autorisé
     */
    public AppointmentDto getAppointmentById(UUID id, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        validateAppointmentAccess(appointment, userEmail);

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Met à jour un rendez-vous existant (propriétaire uniquement) si la fenêtre d'annulation le permet.
     * Valide le nouveau créneau et contrôle les conflits.
     *
     * @param id        identifiant du rendez-vous
     * @param request   champs modifiables (service, staff, startTime, notes)
     * @param userEmail email du propriétaire
     * @return DTO du rendez-vous mis à jour
     * @throws IllegalArgumentException si non autorisé, non modifiable, entités introuvables ou conflit de créneau
     */
    @Transactional
    public AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        if (appointment.cannotBeCancelled()) {
            throw new IllegalArgumentException("This appointment can no longer be modified");
        }

        validateAppointmentTime(request.getStartTime());

        if (request.getServiceId() != null) {
            be.salon.coiffurereservation.entity.Service service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
            appointment.setService(service);
        }

        if (request.getStaffId() != null) {
            StaffMember staff = staffMemberRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));
            appointment.setStaffMember(staff);
        }

        if (request.getStartTime() != null) {
            LocalDateTime endTime =
                    request.getStartTime().plusMinutes(appointment.getService().getDurationMinutes());
            checkForConflicts(appointment.getStaffMember().getId(), request.getStartTime(), endTime, id);
            appointment.setAppointmentDate(request.getStartTime().toLocalDate());
            appointment.setStartTime(request.getStartTime().toLocalTime());
            appointment.setEndTime(endTime.toLocalTime());
        }

        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        appointment = appointmentRepository.save(appointment);
        log.info("Appointment updated: {}", id);

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Annule un rendez-vous (propriétaire ou admin) si l'annulation respecte {@code cancellationHours}.
     *
     * @param id        identifiant du rendez-vous
     * @param request   raison d'annulation (optionnelle selon ton domaine)
     * @param userEmail email du demandeur (propriétaire ou admin)
     * @throws IllegalArgumentException si non autorisé, non annulable ou trop tard
     */
    @Transactional
    public void cancelAppointment(UUID id, CancelAppointmentRequest request, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        validateAppointmentAccess(appointment, userEmail);

        if (appointment.cannotBeCancelled()) {
            throw new IllegalArgumentException("This appointment can no longer be cancelled");
        }

        LocalDateTime minCancellationTime = LocalDateTime.now().plusHours(cancellationHours);
        LocalDateTime appointmentStartDateTime = appointment.getAppointmentDate().atTime(appointment.getStartTime());
        if (appointmentStartDateTime.isBefore(minCancellationTime)) {
            throw new IllegalArgumentException(
                    "The appointment must be cancelled at least " + cancellationHours + " hours in advance");
        }

        appointment.cancel(request.getReason());
        appointmentRepository.save(appointment);
        log.info("Appointment cancelled: {}", id);

        // Audit log
        auditService.logAction(userEmail, "APPOINTMENT_CANCELLED", "Appointment", id,
                java.util.Map.of(
                        "reason", request.getReason() != null ? request.getReason() : "No reason provided",
                        "cancelledAt", LocalDateTime.now().toString()
                ));

        emailService.sendAppointmentCancellation(appointment);
    }

    /**
     * Récupère les rendez-vous sur une plage temporelle.
     *
     * @param start borne de début (incluse)
     * @param end   borne de fin (incluse/exclue selon la requête en base)
     * @return liste de DTOs de rendez-vous
     */
    public List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findActiveByDateRange(start, end);
        return appointmentMapper.toDtoList(appointments);
    }

    /**
     * Vérifie que l'utilisateur a le droit d'accéder au rendez-vous (propriétaire ou admin).
     *
     * @param appointment le rendez-vous à vérifier
     * @param userEmail   email de l'utilisateur demandeur
     * @throws IllegalArgumentException si l'accès n'est pas autorisé
     */
    private void validateAppointmentAccess(Appointment appointment, String userEmail) {
        if (!appointment.getUser().getEmail().equals(userEmail)) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (!user.hasRole("ROLE_ADMIN")) {
                throw new IllegalArgumentException("Unauthorized access");
            }
        }
    }

    /**
     * Vérifie le respect du délai minimal {@link #minAdvanceHours} pour la réservation.
     *
     * @param startTime date/heure de début demandée
     * @throws IllegalArgumentException si le créneau est trop proche de l'instant courant
     */
    private void validateAppointmentTime(LocalDateTime startTime) {
        LocalDateTime minStartTime = LocalDateTime.now().plusHours(minAdvanceHours);
        if (startTime.isBefore(minStartTime)) {
            throw new IllegalArgumentException(
                    "The appointment must be booked at least " + minAdvanceHours + " hour(s) in advance");
        }
    }

    /**
     * Détecte l'existence de conflits pour un membre du staff sur un créneau donné.
     *
     * @param staffId   identifiant du staff
     * @param startTime début du créneau
     * @param endTime   fin du créneau
     * @throws IllegalArgumentException si au moins un conflit est détecté
     */
    private void checkForConflicts(UUID staffId, LocalDateTime startTime, LocalDateTime endTime) {
        checkForConflicts(staffId, startTime, endTime, null);
    }

    /**
     * Variante avec exclusion d'un rendez-vous (utile lors d'une mise à jour).
     *
     * @param staffId   identifiant du staff
     * @param startTime début du créneau
     * @param endTime   fin du créneau
     * @param excludeId identifiant du rendez-vous à ignorer (peut être {@code null})
     * @throws IllegalArgumentException si au moins un conflit est détecté
     */
    private void checkForConflicts(UUID staffId, LocalDateTime startTime, LocalDateTime endTime, UUID excludeId) {
        List<Appointment> conflicts =
                appointmentRepository.findActiveConflicts(staffId, startTime, endTime);

        if (excludeId != null) {
            conflicts = conflicts.stream()
                    .filter(a -> !a.getId().equals(excludeId))
                    .toList();
        }

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("This time slot is no longer available");
        }
    }
}
