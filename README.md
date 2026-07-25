# Food Mood Insights Service

REST microservice that generates and manages meal/mood recommendations for the Food Mood Journal main application.

## API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/users/{userId}/recommendations` | Generate a recommendation from meal wellness data |
| `GET` | `/api/v1/users/{userId}/recommendations?status=` | List recommendations for a user |
| `PUT` | `/api/v1/recommendations/{id}` | Update status (`APPLIED` or `DISMISSED`) |

## Run

```bash
./mvnw spring-boot:run
```

Default port: **8081**  
Database: `food_mood_insights_svc` (created automatically)

Update MySQL credentials in `src/main/resources/application.properties` if needed.
