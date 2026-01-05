# 🚀 MotoRoute Quick Start Guide

## Prerequisites

Make sure you have the following installed:
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for databases)

## Quick Start (5 minutes)

### 1. Start Infrastructure Services

```bash
# Start PostgreSQL and Redis using Docker Compose
docker-compose up -d

# Wait for services to be ready (about 10 seconds)
sleep 10
```

### 2. Set Environment Variables

```bash
# Create .env file or export these variables
export OPENROUTE_API_KEY=your-api-key-here
export JWT_SECRET=$(openssl rand -base64 32)
```

**Important:** Get your free OpenRouteService API key at https://openrouteservice.org/dev/#/signup

### 3. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/motoroute-api-1.0.0-SNAPSHOT.jar
```

### 4. Verify Installation

Open your browser and go to:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

## Testing the API

### Register a New User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Save the `accessToken` from the response.

### Calculate a Route

```bash
curl -X POST http://localhost:8080/api/v1/routes/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "start": {
      "latitude": 41.0082,
      "longitude": 28.9784
    },
    "end": {
      "latitude": 39.9334,
      "longitude": 32.8597
    }
  }'
```

### Get Route History

```bash
curl -X GET "http://localhost:8080/api/v1/routes/history?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Useful Commands

### Check Application Logs

```bash
# Follow logs in real-time
docker logs -f motoroute-postgres
```

### Access PostgreSQL

```bash
docker exec -it motoroute-postgres psql -U postgres -d motoroute
```

### Access Redis CLI

```bash
docker exec -it motoroute-redis redis-cli
```

### Stop Infrastructure

```bash
docker-compose down
```

## Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Explore the API using Swagger UI at http://localhost:8080/swagger-ui.html
- Check Prometheus metrics at http://localhost:8080/actuator/prometheus
- Configure your production environment variables

## Troubleshooting

### Port Already in Use

If ports 8080, 5432, or 6379 are already in use:

```bash
# Stop docker-compose
docker-compose down

# Edit docker-compose.yml to change ports
# Edit application.yml to match new ports
```

### Database Connection Error

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check PostgreSQL logs
docker logs motoroute-postgres
```

### Redis Connection Error

```bash
# Check if Redis is running
docker ps | grep redis

# Check Redis logs
docker logs motoroute-redis
```

## Support

For issues and questions:
- Check the [README.md](README.md)
- Review application logs
- Check the Swagger documentation
