FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/esg-dashboard-1.0.0.jar app.jar

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]