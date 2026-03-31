# ==========================================
# Build Stage
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the pom.xml first to download dependencies
# This leverages Docker cache to avoid re-downloading dependencies repeatedly
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the src directory and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Package Stage
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=build /app/target/*.jar app.jar

# Render injects the PORT environment variable.
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
