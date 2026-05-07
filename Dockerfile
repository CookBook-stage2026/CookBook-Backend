# Stage 1: Build the application
FROM gradle:jdk25-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle :application:bootJar --no-daemon -x test

# Stage 2: Run the application
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/application/build/libs/application-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=deploy"]