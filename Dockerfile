# Stage 1: Build the application
FROM amazoncorretto:21-alpine-jdk AS builder

WORKDIR /build

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Build the application
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /build/target/cineplus-0.0.1-SNAPSHOT.jar /api-v1.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/api-v1.jar"]