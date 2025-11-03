package be.salon.coiffurereservation.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration centrale du cache applicatif.
 *
 * <p>Utilise la bibliothèque <b>Caffeine</b> pour gérer un cache
 * en mémoire haute performance, avec une expiration automatique
 * des entrées et des statistiques activées.</p>
 *
 * <p>Les caches définis ici permettent d'améliorer les performances
 * en évitant des appels répétés à la base de données ou à des services
 * externes (par ex. récupération des employés, créneaux disponibles, etc.).</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Déclare et configure le gestionnaire de caches Caffeine.
     *
     * <ul>
     *   <li><b>maximumSize</b> : limite du nombre d’entrées en mémoire</li>
     *   <li><b>expireAfterWrite</b> : durée de vie d’une entrée après écriture</li>
     *   <li><b>recordStats</b> : active les statistiques (ex: hit ratio)</li>
     * </ul>
     *
     * @return un {@link CacheManager} configuré pour l’application.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "services",
                "staffMembers",
                "workingHours",
                "holidays",
                "availableSlots"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)                     // nombre max d’objets en cache
                .expireAfterWrite(10, TimeUnit.MINUTES) // TTL : 10 min
                .recordStats());                       // statistiques activées

        return cacheManager;
    }
}
