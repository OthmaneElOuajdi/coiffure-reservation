package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.SalonSetting;
import be.salon.coiffurereservation.repository.SalonSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des paramètres configurables du salon.
 * <p>
 * Ce service permet de gérer les paramètres globaux du salon sous forme de paires clé/valeur,
 * permettant de modifier le comportement de l'application sans redéploiement.
 * </p>
 * <p>
 * Exemples de paramètres :
 * <ul>
 *   <li>SALON_NAME - Nom du salon</li>
 *   <li>SALON_ADDRESS - Adresse du salon</li>
 *   <li>SALON_PHONE - Téléphone du salon</li>
 *   <li>SALON_EMAIL - Email de contact</li>
 *   <li>OPENING_HOURS - Horaires d'ouverture</li>
 *   <li>CANCELLATION_POLICY - Politique d'annulation</li>
 *   <li>MIN_ADVANCE_HOURS - Délai minimal de réservation</li>
 *   <li>CANCELLATION_HOURS - Délai d'annulation</li>
 *   <li>ENABLE_PAYMENTS - Activer les paiements en ligne</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SalonSettingService {

    private final SalonSettingRepository salonSettingRepository;

    /**
     * Récupère un paramètre par sa clé.
     * Les résultats sont mis en cache pour améliorer les performances.
     *
     * @param key clé du paramètre
     * @return SalonSetting trouvé
     * @throws IllegalArgumentException si le paramètre n'existe pas
     */
    @Cacheable(value = "salonSettings", key = "#key")
    public SalonSetting getSetting(String key) {
        return salonSettingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found: " + key));
    }

    /**
     * Récupère la valeur d'un paramètre par sa clé.
     *
     * @param key clé du paramètre
     * @return valeur du paramètre
     * @throws IllegalArgumentException si le paramètre n'existe pas
     */
    public String getSettingValue(String key) {
        return salonSettingRepository.findByKey(key)
                .map(SalonSetting::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found: " + key));
    }

    /**
     * Récupère la valeur d'un paramètre avec une valeur par défaut si non trouvé.
     *
     * @param key          clé du paramètre
     * @param defaultValue valeur par défaut
     * @return valeur du paramètre ou valeur par défaut
     */
    public String getSettingValue(String key, String defaultValue) {
        return salonSettingRepository.findByKey(key)
                .map(SalonSetting::getValue)
                .orElse(defaultValue);
    }

    /**
     * Récupère un paramètre booléen.
     *
     * @param key clé du paramètre
     * @return true si la valeur est "true" (insensible à la casse)
     */
    public boolean getBooleanSetting(String key) {
        return Boolean.parseBoolean(getSettingValue(key, "false"));
    }

    /**
     * Récupère un paramètre entier.
     *
     * @param key          clé du paramètre
     * @param defaultValue valeur par défaut si non trouvé ou invalide
     * @return valeur entière du paramètre
     */
    public int getIntSetting(String key, int defaultValue) {
        try {
            return Integer.parseInt(getSettingValue(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for setting {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Récupère tous les paramètres du salon.
     *
     * @return liste de tous les paramètres
     */
    public List<SalonSetting> getAllSettings() {
        return salonSettingRepository.findAll();
    }

    /**
     * Crée ou met à jour un paramètre.
     * Le cache est invalidé après la mise à jour.
     *
     * @param key         clé du paramètre
     * @param value       valeur du paramètre
     * @param description description optionnelle
     * @return SalonSetting créé ou mis à jour
     */
    @Transactional
    @CacheEvict(value = "salonSettings", key = "#key")
    public SalonSetting setSetting(String key, String value, String description) {
        SalonSetting setting = salonSettingRepository.findByKey(key)
                .orElse(SalonSetting.builder()
                        .key(key)
                        .build());

        setting.setValue(value);
        setting.setDescription(description);

        SalonSetting saved = salonSettingRepository.save(setting);
        log.info("Setting updated with description: {} = {} ({})", key, value, description);
        return saved;
    }

    /**
     * Crée ou met à jour un paramètre sans description.
     *
     * @param key   clé du paramètre
     * @param value valeur du paramètre
     * @return SalonSetting créé ou mis à jour
     */
    @Transactional
    @CacheEvict(value = "salonSettings", key = "#key")
    public SalonSetting setSetting(String key, String value) {
        SalonSetting setting = salonSettingRepository.findByKey(key)
                .orElse(SalonSetting.builder()
                        .key(key)
                        .build());

        setting.setValue(value);
        setting.setDescription(null);

        SalonSetting saved = salonSettingRepository.save(setting);
        log.info("Setting updated without description: {} = {}", key, value);
        return saved;
    }

    /**
     * Supprime un paramètre.
     * Le cache est invalidé après la suppression.
     *
     * @param key clé du paramètre à supprimer
     */
    @Transactional
    @CacheEvict(value = "salonSettings", key = "#key")
    public void deleteSetting(String key) {
        SalonSetting setting = salonSettingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found: " + key));
        salonSettingRepository.delete(setting);
        log.info("Setting deleted: {}", key);
    }

    /**
     * Vérifie si un paramètre existe.
     *
     * @param key clé du paramètre
     * @return true si le paramètre existe
     */
    public boolean settingExists(String key) {
        return salonSettingRepository.findByKey(key).isPresent();
    }

    /**
     * Initialise les paramètres par défaut du salon s'ils n'existent pas.
     * Cette méthode peut être appelée au démarrage de l'application.
     */
    @Transactional
    public void initializeDefaultSettings() {
        createSettingIfNotExists("SALON_NAME", "Mon Salon de Coiffure",
                "Nom du salon affiché sur le site");
        createSettingIfNotExists("SALON_ADDRESS", "123 Rue Example, 75001 Paris",
                "Adresse physique du salon");
        createSettingIfNotExists("SALON_PHONE", "+33 1 23 45 67 89",
                "Numéro de téléphone du salon");
        createSettingIfNotExists("SALON_EMAIL", "contact@monsalon.fr",
                "Email de contact du salon");
        createSettingIfNotExists("MIN_ADVANCE_HOURS", "1",
                "Délai minimal en heures avant un rendez-vous");
        createSettingIfNotExists("CANCELLATION_HOURS", "24",
                "Délai d'annulation en heures avant le rendez-vous");
        createSettingIfNotExists("SLOT_INTERVAL_MINUTES", "30",
                "Intervalle entre les créneaux disponibles en minutes");
        createSettingIfNotExists("ENABLE_PAYMENTS", "true",
                "Activer les paiements en ligne");
        createSettingIfNotExists("CANCELLATION_POLICY",
                "Les annulations doivent être effectuées au moins 24h à l'avance.",
                "Politique d'annulation affichée aux clients");

        log.info("Default salon settings initialized");
    }

    /**
     * Crée un paramètre uniquement s'il n'existe pas déjà.
     *
     * @param key         clé du paramètre
     * @param value       valeur par défaut
     * @param description description du paramètre
     */
    private void createSettingIfNotExists(String key, String value, String description) {
        if (!settingExists(key)) {
            SalonSetting setting = SalonSetting.builder()
                    .key(key)
                    .value(value)
                    .description(description)
                    .build();
            salonSettingRepository.save(setting);
            log.info("Setting created: {} = {}", key, value);
        }
    }
}
