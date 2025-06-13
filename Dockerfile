FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8082  # optional but useful for documentation

ENTRYPOINT ["java", "-jar", "app.jar"]