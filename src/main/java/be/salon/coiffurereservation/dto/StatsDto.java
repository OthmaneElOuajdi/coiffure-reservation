package be.salon.coiffurereservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Représente un ensemble de statistiques agrégées sur les rendez-vous, les clients
 * et les performances globales du salon.
 *
 * <p>Ce DTO est principalement utilisé dans le tableau de bord d’administration
 * pour afficher les indicateurs clés sur une période donnée (semaine, mois, etc.).</p>
 *
 * <h3>Exemple JSON :</h3>
 * <pre>
 * {
 *   "totalAppointments": 120,
 *   "confirmedAppointments": 95,
 *   "cancelledAppointments": 10,
 *   "occupationRate": 82.5,
 *   "totalRevenueCents": 245000,
 *   "totalRevenueEuros": 2450.00,
 *   "newClientsCount": 15,
 *   "appointmentsByDay": {
 *     "Monday": 20,
 *     "Tuesday": 25,
 *     "Wednesday": 18
 *   },
 *   "appointmentsByHour": {
 *     "09:00": 8,
 *     "10:00": 10,
 *     "11:00": 12
 *   },
 *   "topServices": {
 *     "Coupe femme": 35,
 *     "Coloration": 28,
 *     "Brushing": 22
 *   }
 * }
 * </pre>
 *
 * <p>Ce DTO est généré et retourné par le service {@link be.salon.coiffurereservation.service.StatsService}
 * à partir des données de la base (via {@link be.salon.coiffurereservation.repository.AppointmentRepository}).</p>
 *
 * @see be.salon.coiffurereservation.service.StatsService
 * @see be.salon.coiffurereservation.repository.AppointmentRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {

    /** Nombre total de rendez-vous sur la période. */
    private Long totalAppointments;

    /** Nombre de rendez-vous confirmés. */
    private Long confirmedAppointments;

    /** Nombre de rendez-vous annulés. */
    private Long cancelledAppointments;

    /** Taux d’occupation global du salon sur la période (en pourcentage). */
    private Double occupationRate;

    /** Chiffre d’affaires total (en centimes d’euro). */
    private Long totalRevenueCents;

    /** Chiffre d’affaires total converti en euros. */
    private Double totalRevenueEuros;

    /** Nombre de nouveaux clients sur la période. */
    private Long newClientsCount;

    /** Répartition des rendez-vous par jour (ex. : {"Lundi": 15, "Mardi": 22}). */
    private Map<String, Long> appointmentsByDay;

    /** Répartition des rendez-vous par heure de la journée (ex. : {"09:00": 5, "10:00": 8}). */
    private Map<String, Long> appointmentsByHour;

    /** Classement des services les plus réservés (ex. : {"Coupe femme": 30, "Brushing": 20}). */
    private Map<String, Long> topServices;
}
