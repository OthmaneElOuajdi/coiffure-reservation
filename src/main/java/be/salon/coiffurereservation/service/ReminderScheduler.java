package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.Appointment;
import be.salon.coiffurereservation.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tâche planifiée envoyant les emails de rappel pour les rendez-vous du lendemain.
 * <p>
 * Parcourt chaque jour les rendez-vous confirmés de J+1 et déclenche l'envoi d'un
 * email via {@link EmailService#sendAppointmentReminder(Appointment)}.
 * <br>
 * ⚠️ Nécessite {@code @EnableScheduling} dans une classe de configuration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    /**
     * Tâche quotidienne (par défaut à 10:00) qui envoie les rappels pour les rendez-vous du lendemain.
     * <p>
     * Cron par défaut : {@code 0 0 10 * * *} (tous les jours à 10:00).
     * Fenêtre traitée : [demain 00:00, après-demain 00:00).
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendDailyReminders() {
        log.info("Starting daily reminder job");

        LocalDateTime tomorrow = LocalDateTime.now()
                .plusDays(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfTomorrow = tomorrow.plusDays(1);

        List<Appointment> appointments = appointmentRepository.findConfirmedBetween(
                tomorrow.toLocalDate(), tomorrow.toLocalTime(),
                endOfTomorrow.toLocalDate(), endOfTomorrow.toLocalTime());

        log.info("Found {} appointments for tomorrow", appointments.size());

        for (Appointment appointment : appointments) {
            try {
                emailService.sendAppointmentReminder(appointment);
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment {}", appointment.getId(), e);
            }
        }

        log.info("Daily reminder job completed");
    }
}
