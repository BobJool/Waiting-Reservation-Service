FROM openjdk:17-jdk-slim

# Build arguments for hostname and port
ARG EUREKA_HOSTNAME
ARG EUREKA_PORT

# Set environment variables for hostname and port
ENV EUREKA_HOSTNAME=${EUREKA_HOSTNAME}
ENV EUREKA_PORT=${EUREKA_PORT}

# Set Spring profile to prod
ENV SPRING_PROFILES_ACTIVE=prod

# Copy JAR file into the container
ARG JAR_FILE=eureka-server/build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 19000

# Entry point for the application
ENTRYPOINT ["java", "-jar", "app.jar"]


