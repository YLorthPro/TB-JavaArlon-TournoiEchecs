# Etape 1 Build avec Maven
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -Ptest

# Etape 2 créer l'image
FROM openjdk:17
ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} tournoiEchecs.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "/tournoiEchecs.jar"]


