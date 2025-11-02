package be.salon.coiffurereservation.mapper;

import be.salon.coiffurereservation.entity.Service;
import be.salon.coiffurereservation.dto.ServiceDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    // --> DTO: calcule explicitement le prix en euros
    @Mapping(target = "priceEuros", expression = "java(service.getPriceCents() != null ? service.getPriceCents() / 100.0 : null)")
    ServiceDto toDto(Service service);

    List<ServiceDto> toDtoList(List<Service> services);

    // --> Entity: ignore les timestamps (gérés par Hibernate)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toEntity(ServiceDto dto);

    // Optionnel : si priceCents est null mais que priceEuros est fourni, on le déduit
    @AfterMapping
    default void computePriceCentsIfMissing(ServiceDto dto, @MappingTarget Service entity) {
        if (entity.getPriceCents() == null && dto.getPriceEuros() != null) {
            entity.setPriceCents((int) Math.round(dto.getPriceEuros() * 100));
        }
    }
}
