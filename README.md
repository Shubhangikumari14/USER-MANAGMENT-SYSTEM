# Enterprise User Management System

A production-ready REST API for user management built with **Java 21**, **Spring Boot 3**, **Spring Security**, **JWT**, **MySQL**, and **JPA/Hibernate**.

## Features

- User registration and login
- JWT token authentication (stateless)
- Role-based access control (`ADMIN`, `USER`)
- User CRUD APIs (admin-protected)
- BCrypt password encryption
- Global exception handling
- Jakarta Bean Validation
- Structured logging (SLF4J)
- Swagger / OpenAPI documentation
- Docker and Docker Compose support

## Tech Stack

| Technology        | Version |
|-------------------|---------|
| Java              | 21      |
| Spring Boot       | 3.3.5   |
| Spring Security   | 6.x     |
| MySQL             | 8.0     |
| JWT (jjwt)        | 0.12.6  |
| SpringDoc OpenAPI | 2.6.0   |
| Maven             | 3.9+    |

## Project Structure

```
src/main/java/com/enterprise/usermanagement/
├── UserManagementApplication.java
├── config/          # OpenAPI, data seeding
├── controller/      # REST endpoints
├── dto/             # Request/response models
├── entity/          # JPA entities
├── exception/       # Custom exceptions & handler
├── repository/      # Spring Data JPA
├── security/        # JWT filter, Security config
└── service/         # Business logic
```

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8.0 (or use Docker Compose)

## Quick Start (Local)

### 1. Configure MySQL

Update `src/main/resources/application.properties` if needed:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Create database (optional)

Hibernate `ddl-auto=update` creates tables automatically. You can also run:

```bash
mysql -u root -p < schema.sql
```

### 3. Build and run

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

The API starts at **http://localhost:8080**.

### 4. Default admin account

On first startup, a default admin is created:

| Field    | Value                 |
|----------|-----------------------|
| Username | `admin`               |
| Password | `Admin@123`           |
| Email    | `admin@enterprise.com`|
| Role     | `ADMIN`               |

## Docker

```bash
docker-compose up --build
```

- API: http://localhost:8080
- MySQL: localhost:3306

## API Endpoints

| Method | Endpoint              | Access        | Description              |
|--------|-----------------------|---------------|--------------------------|
| POST   | `/api/auth/register`  | Public        | Register new user        |
| POST   | `/api/auth/login`     | Public        | Login, returns JWT       |
| GET    | `/api/users/me`       | Authenticated | Current user profile     |
| GET    | `/api/users`          | ADMIN         | List all users           |
| GET    | `/api/users/{id}`     | ADMIN         | Get user by ID           |
| POST   | `/api/users`          | ADMIN         | Create user              |
| PUT    | `/api/users/{id}`     | ADMIN         | Update user              |
| DELETE | `/api/users/{id}`     | ADMIN         | Delete user              |

### Authentication

Include the JWT in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

### Example: Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

### Example: Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "Password@123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

## Swagger UI

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

Use the **Authorize** button and enter: `Bearer <token>`.

## Postman

Import `postman/User-Management-System.postman_collection.json`.

1. Run **Login** (saves token automatically).
2. Use other requests (token is applied via collection variable).

## Password Policy

Passwords must:

- Be 8–100 characters
- Include uppercase, lowercase, digit, and special character (`@$!%*?&`)

## Configuration

| Property               | Description              | Default        |
|------------------------|--------------------------|----------------|
| `server.port`          | HTTP port                | `8080`         |
| `app.jwt.secret`       | Base64-encoded HMAC key  | (see props)    |
| `app.jwt.expiration-ms`| Token TTL (ms)           | `86400000` (24h)|

## License

Apache 2.0
