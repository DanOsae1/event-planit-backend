# Use Maven to build the application
FROM maven:3.8.6-amazoncorretto-17 AS builder

WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml ./
COPY src ./src

# Package the application without running tests
RUN mvn clean package

# Use Amazon Corretto as the base image for the runtime
FROM amazoncorretto:17.0.0-alpine3.14

# Add necessary packages
RUN apk update && apk add bash curl

WORKDIR /app

# Copy the packaged JAR file from the builder stage
COPY --from=builder /app/target/*.jar ./eventplanit.jar

# Expose ports for the application and debugging
EXPOSE 8080
EXPOSE 9999

# Set the command to run the application with the Spring profile set to 'docker'
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9999", "-Dspring.profiles.active=docker", "-jar", "eventplanit.jar"]
