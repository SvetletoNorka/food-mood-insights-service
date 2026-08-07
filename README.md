# Food Mood Insights Service

REST microservice that generates and manages meal/mood recommendations for the **Food Mood Journal** main application.

## Description

After a user logs wellness scores for a meal in the main app, this service receives meal macros, mood, energy, and food names, then creates a personalized recommendation. Users can later apply or dismiss recommendations through the main app UI.

## Features

- Generate recommendations from meal wellness data
- List recommendations for a user (optional status filter)
- Update recommendation status (`APPLIED` or `DISMISSED`)
- Rule-based insight messages based on mood, energy, and macros
- Bean Validation on request payloads
- Centralized exception handling with JSON error responses
- Spring Cache for recommendation reads (`@Cacheable` / `@CacheEvict`)
- Scheduled jobs:
  - Cron (`0 0 3 * * *`): dismiss ACTIVE recommendations older than 30 days
  - Fixed rate (every 30 minutes): log active recommendations count

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| Web | Spring Web (REST) |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Other | Lombok, Bean Validation, Spring Cache |

## Integration with Main Application

| Detail | Value |
|--------|--------|
| Main app | `food-mood-journal-app` (port `8080`) |
| This service | port `8081` |
| Called by | Main app via OpenFeign (`InsightsClient`) |
| Config key in main app | `insights.service.url=http://localhost:8081` |

**Typical flow:**
1. Main app saves a wellness log.
2. Main app calls `POST /api/v1/users/{userId}/recommendations` with meal data.
3. This service stores and returns a recommendation.
4. Main app lists/updates recommendations via `GET` and `PUT` endpoints for the Insights page.

Both applications must be running for end-to-end Insights features.

## API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/users/{userId}/recommendations` | Generate a recommendation from meal wellness data |
| `GET` | `/api/v1/users/{userId}/recommendations?status=` | List recommendations for a user |
| `PUT` | `/api/v1/recommendations/{id}` | Update status (`APPLIED` or `DISMISSED`) |

### Recommendation statuses

| Status | Meaning |
|--------|---------|
| `ACTIVE` | Newly created, waiting for user action |
| `APPLIED` | User accepted the recommendation |
| `DISMISSED` | User dismissed the recommendation |

## Prerequisites

- **JDK 17+**
- **Maven 3.9+** (or the included `./mvnw` wrapper)
- **MySQL 8+** running on `localhost:3306`

## Getting Started

### 1. Database configuration

Edit `src/main/resources/application.properties` and set your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food_mood_insights_svc?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

The database is created automatically on first run.

### 2. Build and run

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Default port: **8081**  
Database: `food_mood_insights_svc`

### 3. Tests

```bash
.\mvnw.cmd test
```

## Project Structure

```
src/main/java/app/
├── config/              # CacheConfiguration (caching + scheduling)
├── exception/           # Domain exceptions
├── model/               # Recommendation entity and status enum
├── repository/          # Spring Data JPA
├── scheduler/           # Cron and fixed-rate jobs
├── service/             # Recommendation business logic
└── web/                 # REST controller, DTOs, mapper, exception handler
```

## License

This project is licensed under the [MIT License](LICENSE).
