## scalebook-backend/Dockerfile
#
## ─────────────────────────────────────────────
## STAGE 1: Build
## Use an image that has Maven + Java 21
## ─────────────────────────────────────────────
#FROM maven:3.9.6-eclipse-temurin-21 AS builder
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy pom.xml first and download dependencies separately
## WHY: Docker caches each step. If pom.xml hasn't changed,
## Docker skips the dependency download step on re-builds.
## This makes rebuilds much faster during development.
#COPY pom.xml .
#RUN mvn dependency:go-offline -B
#
## Now copy the actual source code
#COPY src ./src
#
## Build the jar, skip tests
## (tests run in CI, not inside the Docker build itself)
#RUN mvn clean package -DskipTests
#
## ─────────────────────────────────────────────
## STAGE 2: Run
## Use a much smaller image — just Java runtime, no Maven
## ─────────────────────────────────────────────
#FROM eclipse-temurin:21-jre-jammy
#
#WORKDIR /app
#
## Copy only the compiled jar from stage 1
#COPY --from=builder /app/target/*.jar app.jar
#
## Tell Docker this container listens on port 8080
#EXPOSE 8080
#
## The command that runs when the container starts
#ENTRYPOINT ["java", "-jar", "app.jar"]
#
#
#

# ---------- BUILD ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# ---------- RUN ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
