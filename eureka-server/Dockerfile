FROM openjdk:17-jdk-slim

COPY ${JAR_FILE} app.jar

EXPOSE 19000

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]

