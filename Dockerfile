# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Télécharger les dépendances (cache Docker)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build l'application (sans tests pour accélérer)
RUN mvn clean package -DskipTests

# Étape 2 : Image finale légère
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Installer wget pour le healthcheck
RUN apk add --no-cache wget

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/salon-reservation-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Démarrer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
