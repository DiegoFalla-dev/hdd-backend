## Multi-stage Dockerfile: build the JAR with Maven, then copy into a lightweight runtime image
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy all sources and build the application (skip tests to speed up CI build)
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
COPY src src
RUN mvn -B -DskipTests package

FROM amazoncorretto:21-alpine-jdk
COPY --from=builder /workspace/target/cineplus-0.0.1-SNAPSHOT.jar /api-v1.jar
ENTRYPOINT ["java","-jar","/api-v1.jar"]