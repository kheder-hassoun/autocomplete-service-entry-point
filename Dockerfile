FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]

