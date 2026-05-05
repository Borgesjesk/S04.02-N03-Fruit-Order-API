# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production
FROM eclipse-temurin:21-jre-jammy
RUN groupadd -r springgroup && useradd -r -g springgroup -s /bin/false springuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown springuser:springgroup app.jar
USER springuser
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]