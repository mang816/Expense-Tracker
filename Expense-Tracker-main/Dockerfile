# ---- Stage 1: build the JAR ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# Windows line endings would break the gradlew script on Linux
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Tests are skipped here because they need a live database
RUN ./gradlew bootJar --no-daemon -x test

# ---- Stage 2: small runtime image ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]