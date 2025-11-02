package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.SalonSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour la gestion des paramètres globaux du salon {@link SalonSetting}.
 * <p>
 * Ces paramètres peuvent inclure, par exemple :
 * <ul>
 *     <li>Les horaires d’ouverture par défaut</li>
 *     <li>Les politiques d’annulation</li>
 *     <li>Les informations de contact ou de branding</li>
 * </ul>
 * <p>
 * Chaque paramètre est identifié par une clé unique.
 */
@Repository
public interface SalonSettingRepository extends JpaRepository<SalonSetting, String> {

    /**
     * Recherche un paramètre du salon par sa clé unique.
     *
     * @param key clé du paramètre (ex : "OPENING_HOURS", "SALON_NAME", "CANCELLATION_POLICY")
     * @return paramètre correspondant, s’il existe
     */
    Optional<SalonSetting> findByKey(String key);
}
