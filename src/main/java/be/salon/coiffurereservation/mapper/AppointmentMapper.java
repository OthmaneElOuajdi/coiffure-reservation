package be.salon.coiffurereservation.mapper;

import be.salon.coiffurereservation.entity.Appointment;
import be.salon.coiffurereservation.dto.AppointmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Mapper MapStruct permettant de convertir les entités {@link Appointment}
 * en objets de transfert {@link AppointmentDto}.
 *
 * <p>Ce mapper est utilisé pour exposer les données de rendez-vous
 * au format adapté à l’API (DTO), tout en masquant les relations
 * et entités internes du domaine.</p>
 *
 * <p>Il est automatiquement géré par Spring grâce au paramètre
 * <code>componentModel = "spring"</code>.</p>
 *
 * <h3>Principaux mappings :</h3>
 * <ul>
 *   <li>{@code user.id → userId}</li>
 *   <li>{@code user.email → userEmail}</li>
 *   <li>{@code user.firstName → userName}</li>
 *   <li>{@code staffMember.fullName → staffName}</li>
 *   <li>{@code service.id → serviceId}</li>
 *   <li>{@code service.name → serviceName}</li>
 *   <li>{@code service.durationMinutes → serviceDuration}</li>
 *   <li>{@code service.priceCents → servicePriceCents}</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    /**
     * Convertit une entité {@link Appointment} en DTO {@link AppointmentDto}.
     *
     * @param appointment l’entité de rendez-vous à convertir
     * @return le DTO correspondant
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.firstName", target = "userName")
    @Mapping(source = "staffMember.id", target = "staffId")
    @Mapping(source = "staffMember.fullName", target = "staffName")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "service.durationMinutes", target = "serviceDuration")
    @Mapping(source = "service.priceCents", target = "servicePriceCents")
    @Mapping(target = "startTime", expression = "java(toLocalDateTime(appointment.getAppointmentDate(), appointment.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(toLocalDateTime(appointment.getAppointmentDate(), appointment.getEndTime()))")
    AppointmentDto toDto(Appointment appointment);

    /**
     * Convertit une liste d’entités {@link Appointment} en liste de {@link AppointmentDto}.
     *
     * @param appointments la liste d’entités à convertir
     * @return la liste des DTO correspondants
     */
    List<AppointmentDto> toDtoList(List<Appointment> appointments);

    default LocalDateTime toLocalDateTime(LocalDate date, LocalTime time) {
        return date.atTime(time);
    }
}
