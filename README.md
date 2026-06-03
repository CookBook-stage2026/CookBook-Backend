# CookBook — Backend

A recipe management application that lets users save and share recipes, plan weekly meal schedules, and get AI-powered suggestions for recipe enhancements and meal planning.

Built with Java 25, Spring Boot 4, PostgreSQL (production) / SQLite (development), and LangChain4j with Ollama / AWS Bedrock for AI features.

> **Frontend repository:** [https://github.com/CookBook-stage2026/CookBook-Frontend](https://github.com/CookBook-stage2026/CookBook-Frontend)  
> **Security documentation:** [documentation/security-documentation.md](documentation/security-documentation.md)  
> **Deployment documentation:** [documentation/deployment-documentation.md](documentation/deployment-documentation.md)

---

## Table of Contents

- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [OAuth2 Authentication](#oauth2-authentication)
- [AI Integration](#ai-integration)
- [Security Overview](#security-overview)
- [Deployment](#deployment)
- [CI/CD Pipeline](#cicd-pipeline)
- [Architecture Overview](#architecture-overview)
- [Code Quality](#code-quality)


---

## Project Structure

```
cookbook/
├── adapters/        
│   ├── incoming/    
│   │   └── rest/    # REST API controllers
│   └── outgoing/    
│       ├── ai/      # External AI communication
│       └── jpa/     # External database connection
├── application/     # Spring Boot configuration & security
└── core/            # Domain, services, ports & repositories
```

---

## Prerequisites

Make sure the following are installed before you begin:

| Tool                                                              | Version                   |
|-------------------------------------------------------------------|---------------------------|
| [JDK](https://adoptium.net/)                                      | 25                        |
| [Gradle](https://gradle.org/)                                     | via wrapper (`./gradlew`) |
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | any recent version        |

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/CookBook-stage2026/CookBook-Backend
cd CookBook-Backend
```

### 2. Set up `application-dev.properties`

Create `application/src/main/resources/application-dev.properties`. This file is not committed to version control. By default ollama is used for local development.
To use Bedrock fill in your region and model-id and set the profile to `dev` in [BedrockChatModelConfig](adapters/outgoing/ai/src/main/java/be/xplore/cookbook/ai/config/BedrockChatModelConfig.java)
and remove it in [OllamaChatModelConfig](adapters/outgoing/ai/src/main/java/be/xplore/cookbook/ai/config/OllamaChatModelConfig.java).
In order to use Firecrawl to import recipes, set firecrawl.enabled to true and fill in the api key in step 3.

```properties
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.url=jdbc:sqlite:myDatabase.db
spring.datasource.username=Developer
spring.datasource.password=Developer

spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
app.cors.allowed-headers=*
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.github.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.microsoft.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

# Optional — only needed if using Bedrock locally
bedrock.region=
bedrock.model-id=

# Optional — only needed if using ollama locally
ollama.model-name=finetuned_llama
ollama.base-url=http://localhost:11434/

ollama.max-response-time-in-minutes=15

firecrawl.enabled=false
```

### 3. Set up `secret.properties`

Create `application/src/main/resources/secret.properties`. This file is not committed to version control. Fill in your OAuth2 and JWT credentials at minimum:

```properties
spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=
 
spring.security.oauth2.client.registration.github.client-id=
spring.security.oauth2.client.registration.github.client-secret=
 
spring.security.oauth2.client.registration.microsoft.client-id=
spring.security.oauth2.client.registration.microsoft.client-secret=
 
app.jwt.secret=
app.cors.allowed-origins=http://localhost:4200
 
# Optional — only needed if using Bedrock locally
bedrock.aws-access-key-id=
bedrock.aws-secret-access-key=
 
# Optional — only needed if using Firecrawl
firecrawl.api-key=
```

---

### Running in development (SQLite + Ollama)

The `dev` profile uses an embedded SQLite database and routes AI calls to a local Ollama instance, so no external services are required beyond Docker.

**Step 1** — Start Ollama via Docker Compose (this builds and loads the fine-tuned model automatically):

```bash
docker compose --profile dev up
```

The `ollama` service runs a startup script that pulls the base `mistral:7b` model, applies the custom `Modelfile`, and serves it at `http://localhost:11434`.

**Step 2** — Run the Spring Boot application:

```bash
./gradlew :application:bootRun --args='--spring.profiles.active=dev'
```

The application starts on `http://localhost:8080`.

> **Note:** The first `docker compose up` will download the Mistral model (~4 GB). Subsequent starts reuse the cached model from the `ollama-data` volume.

---

## Configuration

**Required environment variables for deploy:**

| Variable                                          | Description                                     |
|---------------------------------------------------|-------------------------------------------------|
| `RDS_HOSTNAME`                                    | PostgreSQL host                                 |
| `RDS_PORT`                                        | PostgreSQL port                                 |
| `RDS_DB_NAME`                                     | Database name                                   |
| `RDS_USERNAME` / `RDS_PASSWORD`                   | Database credentials                            |
| `BEDROCK_AWS_ACCESS_KEY_ID`                       | AWS access key for Bedrock                      |
| `BEDROCK_AWS_SECRET_ACCESS_KEY`                   | AWS secret key for Bedrock                      |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`       | Google OAuth2                                   |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET`       | GitHub OAuth2                                   |
| `MICROSOFT_CLIENT_ID` / `MICROSOFT_CLIENT_SECRET` | Microsoft OAuth2                                |
| `BASE_URL`                                        | Backend base URL (used in OAuth2 redirect URIs) |
| `JWT_SECRET`                                      | Base64-encoded HMAC-SHA512 signing key          |
| `APP_CORS_ALLOWED_ORIGINS`                        | Comma-separated allowed CORS origins            |
| `FIRECRAWL_API_KEY`                               | Firecrawl API key                               |

---

## OAuth2 Authentication

The application supports login via three OAuth2 providers. Register your app with each provider and set the redirect URI to:

```
{BASE_URL}/login/oauth2/code/{provider}
```

| Provider  | Developer Console                      |
|-----------|----------------------------------------|
| Google    | https://console.cloud.google.com/      |
| GitHub    | https://github.com/settings/developers |
| Microsoft | https://portal.azure.com/              |

On a successful login the backend issues a short-lived JWT, attached as an `HttpOnly` cookie. Subsequent API requests are authorized via this cookie — no `Authorization` header is needed from the frontend.

---

## AI Integration

AI features are powered by [LangChain4j](https://github.com/langchain4j/langchain4j) and are swapped automatically by Spring profile.

| Profile  | Provider              | Model                                                    |
|----------|-----------------------|----------------------------------------------------------|
| `dev`    | Ollama (local Docker) | `finetuned_llama` — Mistral 7B with a chef system prompt |
| `deploy` | AWS Bedrock           | `eu.anthropic.claude-haiku-4-5-20251001-v1:0`            |

**Fine-tuned model details (`dev`):**

The Ollama model is built from `mistral:7b` with a custom `Modelfile` that configures a 32k context window and a system prompt instructing the model to act as a professional chef, always respond in valid JSON matching the exact schema provided in the prompt, use the web scraping tool (Firecrawl MCP) when given a URL, and restrict output to valid ingredient units and categories.

**AI services:**

- `RecipeAIService` — suggests ingredient substitutions, enhancements, and recipe variations.
- `ScheduleAIService` — generates personalised weekly meal plans based on household preferences.
  Prompts live in `adapters/outgoing/ai/src/main/resources/prompts/`. Firecrawl (a web scraping MCP server) is available as a tool for both AI services, allowing them to import recipes directly from a URL.

---

## Security Overview

Authentication is stateless — no HTTP sessions are used anywhere in the backend.

**Key components:**

- **`JwtService`** — issues JWTs on successful login. Encodes the user's UUID as the `sub` claim and includes email and display name. Signed with HMAC-SHA512 using a Base64-decoded secret.
- **`NimbusJwtDecoder`** — validates incoming bearer tokens (read from the `access_token` cookie, not the `Authorization` header) and populates the `SecurityContextHolder`.
- **`CookieAuthorizationRequestRepository`** — stores the OAuth2 state in a signed, encrypted `HttpOnly` cookie during the authorization redirect, avoiding any server-side session.
- **`OAuth2UserInfo`** — normalizes profile payloads from Google, GitHub, and Microsoft into a consistent internal format (handles quirks like GitHub's missing `email` field).
- **`OAuth2AuthenticationSuccessHandler`** — provisions or retrieves the internal user account after IdP login, issues the JWT cookie, and redirects to the frontend callback URL.
  Every OAuth2 authorization request includes `prompt=select_account`, forcing the identity provider to show the account chooser on every login.

For the full security flow see [documentation/security-documentation.md](documentation/security-documentation.md).

---

## Deployment

Production runs on **AWS Elastic Beanstalk** with a multi-container Docker environment. The deployment bundle (`deploy-files/`) contains a `docker-compose.yml` and `nginx.conf` that are zipped and uploaded to Elastic Beanstalk.

**Container layout:**

```
nginx (port 80, public)
  ├── /api/*          → backend:8080  (Spring Boot)
  ├── /oauth2/*       → backend:8080
  ├── /login/oauth2/* → backend:8080
  └── /*              → frontend:3000 (Next.js / Vite)
 ```

**AWS services used:**

| Service           | Purpose                                                  |
|-------------------|----------------------------------------------------------|
| Elastic Beanstalk | Hosts backend + frontend containers with managed scaling |
| RDS (PostgreSQL)  | Managed relational database                              |
| ECR               | Docker image registry for backend and frontend images    |
| CloudFront        | CDN for the frontend                                     |
| Bedrock           | Managed AI inference (Claude Haiku)                      |

**Environment variables** are configured in the Elastic Beanstalk console under *Configuration → Software → Environment properties*. RDS variables (`RDS_HOSTNAME`, `RDS_PORT`, `RDS_DB_NAME`, `RDS_USERNAME`, `RDS_PASSWORD`) are injected automatically when an RDS instance is associated with the environment.

For the full deployment procedure see [documentation/deployment-documentation.md](documentation/deployment-documentation.md).

---

## CI/CD Pipeline

Three GitHub Actions jobs run on push/pull request to `main` and `development`:

### Build & test

Runs on every push and pull request. Checks out the code, sets up JDK 25 (Temurin), runs Checkstyle, executes all tests with JaCoCo coverage, uploads the XML coverage report as an artifact, and sends results to SonarQube.

### Docker → GitHub Container Registry (GHCR)

Runs on push only. Builds and pushes the Docker image to GHCR for staging validation.

```
ghcr.io/cookbook-stage2026/cookbook-backend:<branch>
ghcr.io/cookbook-stage2026/cookbook-backend:latest   # main branch only
```

### Docker → AWS (Elastic Beanstalk)

Runs on push only. Builds and pushes the image to Amazon ECR, zips the `deploy-files/` directory, uploads it to S3, creates a new Elastic Beanstalk application version, and updates `CookBook-env` with the new version.

**Required GitHub secrets:**

| Secret                                        | Purpose                             |
|-----------------------------------------------|-------------------------------------|
| `SONAR_TOKEN`                                 | SonarQube analysis                  |
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` | AWS deployment                      |
| `AWS_REGION`                                  | AWS region                          |
| `AWS_ACCOUNT_ID`                              | ECR image tagging                   |
| `GITHUB_TOKEN`                                | GHCR authentication (auto-provided) |

---

## Architecture Overview

The project follows a **Hexagonal (Ports & Adapters)** architecture:

- **`core`** — Pure Java domain model and business logic. Zero framework dependencies. Defines repository and service ports (interfaces) that outer layers implement.
- **`application`** — Spring Boot wiring: security configuration (OAuth2 + JWT), global exception handling, and bean definitions.
- **`adapters/incoming/rest`** — HTTP layer: `@RestController` classes and request/response DTOs.
- **`adapters/outgoing/jpa`** — Persistence: Spring Data JPA repositories backed by PostgreSQL (deploy) or SQLite (dev).
- **`adapters/outgoing/ai`** — AI layer: LangChain4j adapters for AWS Bedrock and Ollama, selected by Spring profile.
---

## Code Quality

| Tool       | Version | Purpose                                                |
|------------|---------|--------------------------------------------------------|
| Checkstyle | 13.4.0  | Style enforcement (`config/checkstyle/checkstyle.xml`) |
| JaCoCo     | 0.8.14  | Test coverage with aggregated XML report               |
| SonarQube  | 7.2.3   | Static analysis (org: `cookbook-stage2026`)            |

Run all checks locally:

```bash
./gradlew check testCodeCoverageReport
```
