# 🏍️ MotoRoute

Motosiklet sürücüleri için en kısa rotaları gösteren navigasyon uygulaması.

## Tech Stack

- **Backend:** Java 17 + Spring Boot 3.2.1
- **Database:** PostgreSQL + PostGIS
- **Cache:** Redis
- **Auth:** JWT
- **API Documentation:** Swagger/OpenAPI
- **External Service:** OpenRouteService API

## Architecture

Proje **Hexagonal Architecture** mimarisi kullanılarak geliştirilmiştir:

```
infrastructure/rest → application/manager → domain/service → domain/repository
```

### Project Structure

```
src/main/java/com/motoroute/api/
├── MotoRouteApplication.java
├── domain/
│   ├── user/          # User domain logic
│   └── route/         # Route calculation logic
├── application/
│   ├── user/          # UserManager (DTO ↔ VO conversion)
│   └── route/         # RouteManager
├── infrastructure/
│   ├── rest/          # REST Controllers
│   ├── client/        # External API clients
│   ├── security/      # JWT security configuration
│   └── config/        # Application configuration
└── common/            # Shared DTOs, exceptions, utilities
```

## API Endpoints

### Authentication (`/api/v1/auth`)

- `POST /register` - Register new user
- `POST /login` - Login user
- `POST /refresh` - Refresh access token
- `GET /me` - Get current user (requires authentication)

### Routes (`/api/v1/routes`)

- `POST /calculate` - Calculate route between two points (requires authentication)
- `GET /history` - Get route calculation history (requires authentication)

## Setup & Run

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 13+ with PostGIS extension
- Redis 6+

### Environment Variables

Create `.env` file or set environment variables:

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=motoroute
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=motoroute-secret-key-change-in-production-must-be-at-least-256-bits-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# OpenRouteService
OPENROUTE_API_KEY=your-api-key-here
OPENROUTE_BASE_URL=https://api.openrouteservice.org

# Server
SERVER_PORT=8080
```

### Database Setup

```sql
-- Create database
CREATE DATABASE motoroute;

-- Connect to database
\c motoroute

-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
```

### Build & Run

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/motoroute-api-1.0.0-SNAPSHOT.jar

# Or use Maven
mvn spring-boot:run
```

### Docker Compose (Optional)

```bash
# Start PostgreSQL and Redis
docker-compose up -d
```

## API Documentation

After starting the application, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI specification available at:

```
http://localhost:8080/v3/api-docs
```

## Monitoring

Prometheus metrics endpoint:

```
http://localhost:8080/actuator/prometheus
```

Health check:

```
http://localhost:8080/actuator/health
```

## Key Features

- ✅ JWT-based authentication with refresh token
- ✅ Route calculation using OpenRouteService API (shortest path)
- ✅ Route history tracking
- ✅ Redis caching for route calculations
- ✅ Async route history saving
- ✅ PostgreSQL with PostGIS for location data
- ✅ HikariCP connection pooling
- ✅ Prometheus metrics
- ✅ OpenAPI documentation
- ✅ Global exception handling
- ✅ i18n support

## Development

### Code Standards

- **Entities**: Use `@Getter`, `@Setter` (no `@Data`)
- **Services**: Use `@Service`, `@Slf4j`, `@RequiredArgsConstructor`, `@Transactional`
- **Managers**: No `@Transactional`, handle DTO ↔ VO conversion
- **Controllers**: Delegate to Managers, use `@Valid` for validation

### Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn clean verify
```

## License

MIT License