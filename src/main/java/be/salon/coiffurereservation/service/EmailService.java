package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Service d'envoi d'emails liés aux rendez-vous.
 * <p>
 * Gère l'envoi asynchrone des emails de confirmation, d'annulation et de rappel.
 * Utilise {@link JavaMailSender} avec des messages texte simples. Les URLs front sont
 * construites à partir de {@code app.frontend.url}.
 * <br>
 * Nécessite l'activation d'async avec {@code @EnableAsync} sur une classe de configuration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Adresse expéditrice utilisée pour tous les emails.
     * Valeur par défaut: {@code noreply@salon.be}.
     */
    @Value("${spring.mail.username:noreply@salon.be}")
    private String fromEmail;

    /**
     * URL de base du frontend (ex: {@code https://app.salon.be}).
     * Utilisée pour construire les liens dans les emails.
     */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /** Format d'affichage des dates-heures dans les emails (ex: 31/12/2025 at 14:30). */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm");

    /**
     * Envoie un email de confirmation pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Appointment Confirmation}</li>
     *   <li>Contenu: service, date/heure, durée, staff, prix, lien de gestion</li>
     * </ul>
     *
     * @param appointment rendez-vous confirmé
     */
    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Appointment Confirmation");

            String body = String.format("""
                    Hello %s,

                    Your appointment has been successfully confirmed.

                    Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s
                    - Price: %.2f €

                    To modify or cancel your appointment, please log in to your account:
                    %s

                    See you soon!

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    appointment.getStartTime().format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName(),
                    appointment.getService().getPriceEuros(),
                    frontendUrl + "/appointments");

            message.setText(body);
            mailSender.send(message);
            log.info("Confirmation email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email", e);
        }
    }

    /**
     * Envoie un email d'annulation pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Appointment Cancellation}</li>
     *   <li>Contenu: service, date/heure, staff, raison d'annulation, lien de nouvelle réservation</li>
     * </ul>
     *
     * @param appointment rendez-vous annulé
     */
    @Async
    public void sendAppointmentCancellation(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Appointment Cancellation");

            String body = String.format("""
                    Hello %s,

                    Your appointment has been cancelled.

                    Cancelled appointment details:
                    - Service: %s
                    - Date & Time: %s
                    - Staff member: %s

                    Reason: %s

                    You can book a new appointment anytime:
                    %s

                    Best regards,
                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    appointment.getStartTime().format(DATE_FORMATTER),
                    appointment.getStaffMember().getFullName(),
                    appointment.getCancellationReason() != null ? appointment.getCancellationReason() : "Not specified",
                    frontendUrl + "/booking");

            message.setText(body);
            mailSender.send(message);
            log.info("Cancellation email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send cancellation email", e);
        }
    }

    /**
     * Envoie un email de rappel (J-1) pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Reminder: Appointment Tomorrow}</li>
     *   <li>Contenu: service, date/heure, durée, staff</li>
     * </ul>
     *
     * @param appointment rendez-vous à rappeler
     */
    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Reminder: Appointment Tomorrow");

            String body = String.format("""
                    Hello %s,

                    This is a reminder that you have an appointment tomorrow.

                    Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s

                    We are looking forward to seeing you!

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    appointment.getStartTime().format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName());

            message.setText(body);
            mailSender.send(message);
            log.info("Reminder email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send reminder email", e);
        }
    }
}
