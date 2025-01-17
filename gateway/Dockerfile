FROM openjdk:17-jdk-slim

ARG JAR_FILE=gateway/build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 19005

ENTRYPOINT ["java", "-jar", "app.jar"]