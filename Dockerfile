# === BUILD STAGE ===
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first (to cache dependencies)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Now copy the actual source code
COPY src ./src

# Build the project
RUN ./mvnw clean package -DskipTests

# === RUNTIME STAGE ===
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
