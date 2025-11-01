package be.salon.coiffurereservation;


import be.salon.coiffurereservation.config.DotenvConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Point d’entrée principal de l’application « Salon Reservation ».
 *
 * <p>Cette classe démarre le conteneur Spring Boot et initialise
 * tous les composants configurés (entités JPA, services, contrôleurs,
 * configuration de sécurité, etc.).</p>
 *
 * <p>Elle charge également le fichier <code>.env</code> au démarrage
 * grâce à {@link DotenvConfig}, afin de rendre disponibles les variables
 * d’environnement définies localement (ex: DB_HOST, SERVER_PORT, etc.).</p>
 *
 * <p>Exécution depuis un IDE ou la ligne de commande :</p>
 *
 * <pre>
 *   mvn spring-boot:run
 * </pre>
 *
 * <p>Une fois démarrée, l’API REST (et Swagger UI, si activé)
 * sera accessible sur le port défini dans <code>application.properties</code>
 * ou dans le fichier <code>.env</code>.</p>
 */
@SpringBootApplication
public class SalonReservationApplication {

    /**
     * Méthode principale qui démarre l’application Spring Boot.
     *
     * @param args arguments de ligne de commande (optionnels)
     */
    public static void main(String[] args) {
        // Utilisation du SpringApplicationBuilder pour ajouter l'initialiseur DotenvConfig
        new SpringApplicationBuilder(SalonReservationApplication.class)
                .initializers(new DotenvConfig()) // 🔥 Charge automatiquement .env
                .run(args);
    }
}
