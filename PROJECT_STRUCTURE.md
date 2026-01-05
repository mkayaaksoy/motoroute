# 📋 MotoRoute Project Structure

This document provides a detailed overview of the project structure and key components.

## Directory Structure

```
motoroute/
├── src/
│   └── main/
│       ├── java/com/motoroute/api/
│       │   ├── MotoRouteApplication.java              # Main Spring Boot application
│       │   │
│       │   ├── common/                                # Shared components
│       │   │   ├── dto/
│       │   │   │   ├── request/                       # Request DTOs
│       │   │   │   │   ├── RegisterRequest.java
│       │   │   │   │   ├── LoginRequest.java
│       │   │   │   │   ├── RefreshTokenRequest.java
│       │   │   │   │   └── CalculateRouteRequest.java
│       │   │   │   └── response/                      # Response DTOs
│       │   │   │       ├── AuthResponse.java
│       │   │   │       ├── UserResponse.java
│       │   │   │       └── RouteResponse.java
│       │   │   ├── entity/
│       │   │   │   └── AbstractIdStatusEntity.java    # Base entity class
│       │   │   └── exception/
│       │   │       ├── MotoRouteApiBusinessException.java
│       │   │       └── GlobalExceptionHandler.java
│       │   │
│       │   ├── domain/                                # Domain Layer (Business Logic)
│       │   │   ├── user/
│       │   │   │   ├── entity/
│       │   │   │   │   └── User.java                  # User entity
│       │   │   │   ├── repository/
│       │   │   │   │   └── UserRepository.java        # User data access
│       │   │   │   ├── service/
│       │   │   │   │   └── UserService.java           # User business logic
│       │   │   │   └── vo/                            # Value Objects
│       │   │   │       ├── UserVO.java
│       │   │   │       └── CreateUserVO.java
│       │   │   └── route/
│       │   │       ├── entity/
│       │   │       │   └── RouteHistory.java          # Route history entity
│       │   │       ├── repository/
│       │   │       │   └── RouteHistoryRepository.java
│       │   │       ├── service/
│       │   │       │   └── RouteService.java          # Route calculation logic
│       │   │       └── vo/
│       │   │           ├── RouteVO.java
│       │   │           └── CalculateRouteVO.java
│       │   │
│       │   ├── application/                           # Application Layer (Orchestration)
│       │   │   ├── user/
│       │   │   │   └── UserManager.java               # DTO ↔ VO conversion, no business logic
│       │   │   └── route/
│       │   │       └── RouteManager.java
│       │   │
│       │   └── infrastructure/                        # Infrastructure Layer
│       │       ├── rest/                              # REST API Controllers
│       │       │   ├── user/
│       │       │   │   └── RestUserController.java    # Auth endpoints
│       │       │   └── route/
│       │       │       └── RestRouteController.java   # Route endpoints
│       │       ├── client/
│       │       │   └── openroute/
│       │       │       ├── OpenRouteClient.java       # OpenRouteService API client
│       │       │       └── OpenRouteResponse.java
│       │       ├── security/
│       │       │   ├── JwtTokenProvider.java          # JWT token generation/validation
│       │       │   ├── JwtAuthenticationFilter.java   # JWT filter
│       │       │   └── SecurityConfig.java            # Spring Security configuration
│       │       └── config/
│       │           ├── JwtProperties.java             # JWT configuration properties
│       │           ├── OpenRouteProperties.java       # OpenRoute API properties
│       │           ├── RedisConfig.java               # Redis cache configuration
│       │           ├── OpenApiConfig.java             # Swagger/OpenAPI configuration
│       │           └── MessageConfig.java             # i18n message source
│       │
│       └── resources/
│           ├── application.yml                        # Application configuration
│           └── messages.properties                    # i18n messages
│
├── pom.xml                                           # Maven dependencies
├── docker-compose.yml                                # PostgreSQL + Redis setup
├── README.md                                         # Full documentation
├── QUICKSTART.md                                     # Quick start guide
└── .gitignore

```

## Layer Responsibilities

### 1. Domain Layer (`domain/`)
- **Purpose**: Contains core business logic and entities
- **Key Principles**:
  - No dependencies on other layers
  - Pure business logic
  - Works with Value Objects (VOs)
  - Uses `@Transactional` for database operations

**Components**:
- **Entities**: JPA entities with proper annotations
- **Repositories**: Data access interfaces
- **Services**: Business logic implementation
- **Value Objects**: Immutable data transfer within domain

### 2. Application Layer (`application/`)
- **Purpose**: Orchestrates domain services and handles DTO ↔ VO conversion
- **Key Principles**:
  - No business logic
  - No `@Transactional` annotations
  - Delegates to domain services
  - Converts between DTOs (external) and VOs (internal)

**Components**:
- **Managers**: Coordinate domain services and perform conversions

### 3. Infrastructure Layer (`infrastructure/`)
- **Purpose**: Implements technical concerns and external integrations
- **Key Principles**:
  - Depends on domain and application layers
  - Handles HTTP, security, external APIs
  - Contains all framework-specific code

**Components**:
- **REST Controllers**: Handle HTTP requests/responses
- **Clients**: External API integrations
- **Security**: Authentication and authorization
- **Config**: Application configuration

### 4. Common Layer (`common/`)
- **Purpose**: Shared components used across all layers
- **Components**:
  - DTOs for API contracts
  - Base entity classes
  - Exception handling
  - Utilities

## Key Design Patterns

### 1. Hexagonal Architecture
```
External World (HTTP/DB) → Infrastructure → Application → Domain
```

### 2. Repository Pattern
- Abstracts data access
- Defined in domain layer
- Implemented by Spring Data JPA

### 3. DTO/VO Pattern
- **DTOs**: Used for external communication (API requests/responses)
- **VOs**: Used for internal domain communication
- **Managers**: Convert between DTOs and VOs

### 4. Service Pattern
- **Services**: Contain business logic
- **Managers**: Orchestrate services without business logic

## Data Flow

### Incoming Request Flow
```
1. HTTP Request → REST Controller
2. Controller validates DTO with @Valid
3. Controller calls Manager
4. Manager converts DTO → VO
5. Manager calls Domain Service
6. Service performs business logic
7. Service returns VO
8. Manager converts VO → DTO
9. Controller returns HTTP Response
```

### Example: User Registration
```
POST /api/v1/auth/register (RegisterRequest DTO)
    ↓
RestUserController.register()
    ↓
UserManager.register(RegisterRequest)
    ↓
CreateUserVO created from RegisterRequest
    ↓
UserService.createUser(CreateUserVO)
    ↓
User entity created and saved
    ↓
UserVO returned
    ↓
AuthResponse DTO created
    ↓
HTTP 200 with AuthResponse
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(500),
    status BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Route History Table
```sql
CREATE TABLE route_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    start_lat NUMERIC(10,8) NOT NULL,
    start_lon NUMERIC(11,8) NOT NULL,
    end_lat NUMERIC(10,8) NOT NULL,
    end_lon NUMERIC(11,8) NOT NULL,
    distance NUMERIC(10,2) NOT NULL,
    duration NUMERIC(10,2) NOT NULL,
    route_geometry TEXT,
    status BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Configuration Files

### application.yml
- Database configuration (PostgreSQL)
- Redis configuration
- JWT settings
- OpenRouteService API settings
- Server configuration
- Actuator endpoints
- Logging levels

### pom.xml
Key dependencies:
- Spring Boot Web, Data JPA, Security
- PostgreSQL + PostGIS
- Redis + Cache
- JWT (jjwt)
- WebFlux (for OpenRouteService client)
- Swagger/OpenAPI
- Prometheus metrics
- Lombok

## API Endpoints Summary

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `GET /api/v1/auth/me` - Get current user (authenticated)

### Routes
- `POST /api/v1/routes/calculate` - Calculate route (authenticated)
- `GET /api/v1/routes/history` - Get history (authenticated)

### Monitoring
- `GET /actuator/health` - Health check
- `GET /actuator/prometheus` - Prometheus metrics
- `GET /swagger-ui.html` - API documentation

## Security Implementation

### JWT Authentication
1. User logs in with credentials
2. Server validates and generates JWT access token + refresh token
3. Client stores tokens
4. Client sends access token in Authorization header
5. JwtAuthenticationFilter validates token on each request
6. If token expires, client uses refresh token to get new access token

### Password Security
- BCrypt encryption with salt
- Never store plain text passwords
- Password validation on registration

## Caching Strategy

### Redis Cache
- Route calculations cached by coordinates
- Cache key: `startLat-startLon-endLat-endLon`
- TTL: 1 hour
- Reduces API calls to OpenRouteService

## Async Processing

### Route History Saving
- Uses `@Async` annotation
- Saves route history asynchronously
- Doesn't block route calculation response
- Improves API response time

## Error Handling

### Global Exception Handler
- Catches all exceptions
- Returns consistent error responses
- Supports i18n messages
- Logs errors appropriately

### Business Exceptions
- `MotoRouteApiBusinessException`
- Uses message keys for i18n
- Converted to HTTP error responses by GlobalExceptionHandler
