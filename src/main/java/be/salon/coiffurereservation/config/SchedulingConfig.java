package be.salon.coiffurereservation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration globale pour l’activation du scheduling (tâches planifiées).
 *
 * <p>Permet à l’application d’exécuter automatiquement des méthodes
 * annotées avec {@link org.springframework.scheduling.annotation.Scheduled},
 * par exemple pour des nettoyages périodiques, des notifications,
 * ou des synchronisations automatiques.</p>
 *
 * <p>Exemple d’utilisation :</p>
 * <pre>
 *   @Scheduled(fixedRate = 60000)
 *   public void nettoyageAutomatique() {
 *   }
 * </pre>
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

    public SchedulingConfig() {
        log.info("✅ Scheduling activé : les tâches planifiées seront exécutées automatiquement.");
    }
}
