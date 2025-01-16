FROM openjdk:17-jdk-slim

COPY ${JAR_FILE} app.jar

EXPOSE 19002

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]