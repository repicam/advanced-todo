FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar api.jar

# Puerto de la aplicación Spring Boot
EXPOSE 5555

ENTRYPOINT ["java", "-jar", "api.jar"]