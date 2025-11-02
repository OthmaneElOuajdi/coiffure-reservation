package be.salon.coiffurereservation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Charge automatiquement les variables d'environnement depuis le fichier .env
 * au démarrage de l'application Spring Boot.
 */
@Slf4j
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        // Chercher le fichier .env à la racine du projet
        Path envPath = Paths.get(".env");

        if (!Files.exists(envPath)) {
            log.warn("Fichier .env non trouvé. Les variables d'environnement système seront utilisées.");
            return;
        }

        try {
            Map<String, Object> envVars = loadEnvFile(envPath);
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envVars));
            log.info("✅ Fichier .env chargé avec succès ({} variables)", envVars.size());
        } catch (IOException e) {
            log.error("❌ Erreur lors du chargement du fichier .env", e);
        }
    }

    private Map<String, Object> loadEnvFile(Path envPath) throws IOException {
        Map<String, Object> envVars = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Ignorer les lignes vides et les commentaires
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parser la ligne KEY=VALUE
                int separatorIndex = line.indexOf('=');
                if (separatorIndex > 0) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();

                    // Retirer les guillemets si présents
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    envVars.put(key, value);
                }
            }
        }

        return envVars;
    }
}
