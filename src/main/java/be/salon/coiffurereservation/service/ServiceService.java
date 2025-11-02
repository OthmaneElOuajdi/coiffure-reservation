package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.dto.ServiceDto;
import be.salon.coiffurereservation.mapper.ServiceMapper;
import be.salon.coiffurereservation.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service applicatif gérant les opérations CRUD sur les prestations (services du salon).
 * <p>
 * Utilise un cache pour optimiser la récupération de la liste des services actifs.
 * Le cache est invalidé à chaque création, mise à jour ou suppression.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    /**
     * Récupère toutes les prestations actives, triées par ordre d'affichage.
     * <p>
     * Résultat mis en cache sous la clé {@code "services"} pour améliorer les performances.
     *
     * @return liste de services actifs sous forme de {@link ServiceDto}
     */
    @Cacheable("services")
    public List<ServiceDto> getAllActiveServices() {
        List<be.salon.coiffurereservation.entity.Service> services =
                serviceRepository.findByActiveTrueOrderByDisplayOrderAsc();
        return serviceMapper.toDtoList(services);
    }

    /**
     * Récupère toutes les prestations, actives ou non.
     * <p>
     * Utilisé pour les écrans d’administration.
     *
     * @return liste complète des prestations sous forme de {@link ServiceDto}
     */
    public List<ServiceDto> getAllServices() {
        List<be.salon.coiffurereservation.entity.Service> services =
                serviceRepository.findAllByOrderByDisplayOrderAsc();
        return serviceMapper.toDtoList(services);
    }

    /**
     * Récupère une prestation spécifique par son identifiant.
     *
     * @param id identifiant du service
     * @return DTO correspondant à la prestation trouvée
     * @throws IllegalArgumentException si aucun service n’existe avec cet ID
     */
    public ServiceDto getServiceById(UUID id) {
        be.salon.coiffurereservation.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        return serviceMapper.toDto(service);
    }

    /**
     * Crée une nouvelle prestation et invalide le cache des services.
     *
     * @param dto données de la prestation à créer
     * @return prestation créée sous forme de {@link ServiceDto}
     */
    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public ServiceDto createService(ServiceDto dto) {
        be.salon.coiffurereservation.entity.Service service = serviceMapper.toEntity(dto);
        service = serviceRepository.save(service);
        return serviceMapper.toDto(service);
    }

    /**
     * Met à jour une prestation existante et invalide le cache.
     *
     * @param id  identifiant de la prestation à mettre à jour
     * @param dto nouvelles données de la prestation
     * @return prestation mise à jour sous forme de {@link ServiceDto}
     * @throws IllegalArgumentException si la prestation n’existe pas
     */
    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public ServiceDto updateService(UUID id, ServiceDto dto) {
        be.salon.coiffurereservation.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setDurationMinutes(dto.getDurationMinutes());
        service.setPriceCents(dto.getPriceCents());
        service.setActive(dto.getActive());
        service.setDisplayOrder(dto.getDisplayOrder());

        service = serviceRepository.save(service);
        return serviceMapper.toDto(service);
    }

    /**
     * Supprime une prestation et vide le cache des services.
     *
     * @param id identifiant de la prestation à supprimer
     */
    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public void deleteService(UUID id) {
        serviceRepository.deleteById(id);
    }
}
