# Stage 1: Build
FROM gradle:jdk25-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle :application:bootJar --no-daemon -x test

# Stage 2: Run
FROM eclipse-temurin:25-jre-alpine

RUN apk add --no-cache nodejs npm \
    && npm install -g firecrawl-mcp \
    && npm cache clean --force

WORKDIR /app
COPY --from=builder /app/application/build/libs/application-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=deploy"]