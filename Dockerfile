FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/todo-1.0.0.jar app.jar

# Puerto de la aplicación Spring Boot
EXPOSE 5555

ENTRYPOINT ["java", "-jar", "app.jar"]