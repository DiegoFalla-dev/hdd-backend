## Multi-stage Dockerfile
# Stage 1: build the artifact using Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files and build (use -B for batch mode)
COPY . /app
RUN mvn -B -DskipTests package

# Stage 2: runtime image
FROM amazoncorretto:21-alpine-jdk

# Copy built jar from the builder stage
COPY --from=build /app/target/cineplus-0.0.1-SNAPSHOT.jar /api-v1.jar

# Recommended: run Java with sane defaults; override with env if needed
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /api-v1.jar"]
    