FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 19005

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
