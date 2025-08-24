# Use the official Maven image as the base image
ARG JAVA_VERSION=24
FROM maven:3.8.4-openjdk-${JAVA_VERSION} AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the project's POM file to the container
COPY pom.xml .

# Download the project dependencies
RUN mvn dependency:go-offline -B

# Copy the project source code to the container
COPY src ./src

# Copy the checkstyle.xml file to the container
COPY checkstyle.xml .

# Build the application
RUN mvn package -DskipTests

# Create a new image with the JAR file
FROM openjdk:${JAVA_VERSION}-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage to the container
COPY --from=builder /app/target/relia-bill-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which your Spring Boot application listens
EXPOSE 8080

# Run the Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]
