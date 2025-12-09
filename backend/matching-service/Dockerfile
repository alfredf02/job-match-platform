# Use a Gradle image with JDK 17 to build the Spring Boot application
FROM gradle:8.7-jdk17-alpine AS builder

# Set the working directory inside the container for the project files
WORKDIR /home/gradle/project

# Copy Gradle wrapper and project files into the image
COPY . .

# Make sure the Gradle wrapper script is executable
RUN chmod +x gradlew

# Build the bootJar without using the Gradle daemon to keep the image small
RUN ./gradlew bootJar --no-daemon

# Use a slim JDK image for running the built application
FROM eclipse-temurin:17-jre-alpine

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built jar from the builder stage into the runtime image
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]