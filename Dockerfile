# Utiliser une image Java 21 (vérifie ta version Java)
FROM eclipse-temurin:21-jdk-alpine

# Répertoire de travail
WORKDIR /app

# Copier les fichiers Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline

# Copier le code source
COPY src ./src

# Build l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8080

# Lancer l'application
CMD ["java", "-jar", "target/gestionecole-0.0.1-SNAPSHOT.jar"]