FROM openjdk:17-jdk-slim

COPY ${JAR_FILE} app.jar

EXPOSE 19005

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]