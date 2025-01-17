FROM openjdk:17-jdk-slim

ARG JAR_FILE=eureka-server/build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 19000

ENTRYPOINT ["java", "-jar", "app.jar"]


