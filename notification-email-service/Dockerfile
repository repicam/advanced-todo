FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar notification.jar

# Puerto de la aplicación Spring Boot
EXPOSE 6666

ENTRYPOINT ["java", "-jar", "notification.jar"]