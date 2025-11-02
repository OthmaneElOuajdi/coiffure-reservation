package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.AppointmentStatus;
import be.salon.coiffurereservation.dto.StatsDto;
import be.salon.coiffurereservation.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service de génération de statistiques pour les rendez-vous et les revenus du salon.
 * <p>
 * Fournit des données agrégées sur une période donnée (semaine, mois, etc.)
 * destinées aux tableaux de bord ou aux rapports d’activité.
 */
@Service
@RequiredArgsConstructor
public class StatsService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Calcule les statistiques de la semaine pour les rendez-vous et le chiffre d’affaires.
     * <p>
     * Récupère :
     * <ul>
     *   <li>Le nombre total de rendez-vous (tous statuts confondus)</li>
     *   <li>Le nombre de rendez-vous confirmés</li>
     *   <li>Le nombre de rendez-vous annulés</li>
     *   <li>Le revenu total généré sur la période</li>
     *   <li>Le taux d’occupation du staff sur la période</li>
     * </ul>
     * <p>
     * Les détails par jour, par heure et les top services sont initialisés, mais non encore remplis,
     * pour extension future du reporting.
     *
     * @param startOfWeek date/heure de début de la période (inclus)
     * @param endOfWeek   date/heure de fin de la période (exclus)
     * @return objet {@link StatsDto} contenant les données agrégées
     */
    public StatsDto getWeeklyStats(LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        Long totalAppointments = appointmentRepository.countBookedBetween(startOfWeek, endOfWeek);

        Long confirmedAppointments = (long) appointmentRepository
                .findByStatusAndStartTimeBetween(AppointmentStatus.CONFIRMED,
                        startOfWeek.toLocalDate(), startOfWeek.toLocalTime(),
                        endOfWeek.toLocalDate(), endOfWeek.toLocalTime())
                .size();

        Long cancelledAppointments = (long) appointmentRepository
                .findByStatusAndStartTimeBetween(AppointmentStatus.CANCELLED,
                        startOfWeek.toLocalDate(), startOfWeek.toLocalTime(),
                        endOfWeek.toLocalDate(), endOfWeek.toLocalTime())
                .size();

        Long revenueCents = appointmentRepository.sumRevenueBookedCents(startOfWeek, endOfWeek);
        if (revenueCents == null) revenueCents = 0L;

        Map<String, Long> appointmentsByDay = new HashMap<>();
        Map<String, Long> appointmentsByHour = new HashMap<>();
        Map<String, Long> topServices = new HashMap<>();

        return StatsDto.builder()
                .totalAppointments(totalAppointments)
                .confirmedAppointments(confirmedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .occupationRate(calculateOccupationRate(startOfWeek, endOfWeek))
                .totalRevenueCents(revenueCents)
                .totalRevenueEuros(revenueCents / 100.0)
                .newClientsCount(0L)
                .appointmentsByDay(appointmentsByDay)
                .appointmentsByHour(appointmentsByHour)
                .topServices(topServices)
                .build();
    }

    /**
     * Calcule le taux d’occupation du salon (approximatif) sur une période donnée.
     * <p>
     * Le calcul par défaut repose sur un nombre fixe de créneaux disponibles (80),
     * à ajuster selon le modèle du salon (nombre de staff × créneaux/jour × jours ouvrés).
     *
     * @param start début de la période
     * @param end   fin de la période
     * @return taux d’occupation (%) entre 0 et 100
     */
    private Double calculateOccupationRate(LocalDateTime start, LocalDateTime end) {
        Long confirmedCount = appointmentRepository.countBookedBetween(start, end);
        double availableSlots = 80.0; // TODO: paramétrer dynamiquement
        return confirmedCount != null ? (confirmedCount / availableSlots) * 100 : 0.0;
    }
}
