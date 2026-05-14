# 🍊 S04.02 - Fruit Order API (Level 3: MongoDB & Docker Compose)

A production-ready REST API built with Spring Boot for managing fruit orders with embedded subdocuments. This is the third level of a three-level exercise, upgrading from H2 (Level 1) and MySQL (Level 2) to MongoDB for document-based persistence. Each order contains a client name, delivery date, and a list of fruit items stored as embedded subdocuments — no joins, no foreign keys, just clean document storage.

## 📦 What This Project Does

This is a Fruit Order Management API where clients can place orders specifying which fruits they need and when they want them delivered. The API supports full CRUD operations on orders, with each order containing embedded `OrderItem` subdocuments — a natural fit for MongoDB's document model.

The API enforces strict input validation (delivery date must be in the future, all fields required), returns proper HTTP status codes for every scenario, uses DTOs to decouple the API contract from the database schema, and handles all errors through a centralized `GlobalExceptionHandler`. The application runs in a multi-container Docker Compose environment with MongoDB 7.0 and uses environment-based configuration for all credentials.

## 🧠 What I Learned Building This

Building on Level 1's foundation (DTOs, validation, Docker) and Level 2's relational concepts (JPA, MySQL, Docker Compose), this level introduced document-based persistence and new architectural patterns. These are the key concepts I applied:

- **MongoDB Document Model with Embedded Subdocuments:** Instead of relational tables with foreign keys, each `Order` document contains its `OrderItem` list directly — no joins needed. Spring Data MongoDB maps this naturally using `@Document` on the parent and plain POJOs for subdocuments.

- **Docker Compose Multi-Container Setup with MongoDB Authentication:** The application and MongoDB run in separate containers on the same Docker network. The `mongo-init.js` script creates an application-specific user with `readWrite` permissions on the `fruit_order_db` database, and the app authenticates using `?authSource=fruit_order_db` in the connection URI — matching where the user was created.

- **Environment-Based Credential Management:** All credentials live in a `.env` file excluded from version control via `.gitignore`. The `docker-compose.yml` references `${SPRING_DATA_MONGODB_URI}` so no passwords appear in tracked files. A `.env.example` with placeholder values shows the required structure.

- **API Versioning with `/api/v1/` Prefix:** All endpoints use versioned paths (`/api/v1/orders`) to support future API evolution without breaking existing clients — a production best practice.

- **SLF4J Structured Logging:** Every state-changing operation (create, update, delete) is logged with contextual information (client name, order ID), providing an audit trail useful for debugging and monitoring.

- **@WebMvcTest Controller Tests with Mockito:** 15 tests covering all CRUD endpoints — happy paths and unhappy paths (validation failures, not-found scenarios). Tests use `@WebMvcTest` to load only the web layer with mocked service dependencies, running in milliseconds without any database.

- **Date Validation with `@Future`:** The `deliveryDate` field uses `@NotNull` + `@Future` to ensure orders can only be placed for future dates — the API rejects today's date or any past date with a 400 response.

## 🛠 Technologies

- Java 21 (Temurin LTS)
- Spring Boot 3.5.0
- Spring Data MongoDB
- MongoDB 7.0 (Docker)
- Maven (wrapper included — no local Maven installation needed)
- Bean Validation (Hibernate Validator)
- Lombok
- JUnit 5 + MockMvc + Mockito
- Docker (multi-stage production build)
- SLF4J Logging
- IntelliJ IDEA

## 📁 Project Structure

The project follows a layered MVC architecture with clear separation of concerns:

```
src/main/java/cat/itacademy/s04/t02/n03/fruit_order_api/

├── controller/
│   └── OrderController.java          # Handles HTTP requests, returns ResponseEntity
│                                      # CRUD endpoints: POST, GET, PUT, DELETE /orders
├── dto/
│   ├── OrderRequestDto.java           # Input: clientName + deliveryDate + items with validation
│   ├── OrderResponseDto.java          # Output: id + clientName + deliveryDate + items
│   └── OrderItemDto.java              # Shared: fruitName + quantityInKilos
│
├── exception/
│   ├── OrderNotFoundException.java    # Custom RuntimeException for missing orders
│   └── GlobalExceptionHandler.java    # @RestControllerAdvice — formats all errors
│
├── mapper/
│   └── OrderMapper.java               # Entity ↔ DTO conversion utilities
│                                      # Static methods with null-safety
├── model/
│   ├── Order.java                     # @Document — MongoDB document with embedded items
│   └── OrderItem.java                 # Embedded subdocument: fruitName + quantityInKilos
│
├── repository/
│   └── OrderRepository.java           # Interface extending MongoRepository<Order, String>
│
├── service/
│   ├── OrderService.java              # Interface defining CRUD operations
│   └── OrderServiceImpl.java          # Implementation with DTO mapping and exception handling
│
└── FruitOrderApiApplication.java      # Spring Boot entry point
```

```
src/test/java/cat/itacademy/s04/t02/n03/fruit_order_api/

├── controller/
│   └── OrderControllerTest.java       # 15 @WebMvcTest cases covering all endpoints
└── FruitOrderApiApplicationTests.java # Context load test
```

## 🔌 API Endpoints

**Order Management**

| Method | Endpoint | Description | Success | Error |
|--------|----------|-------------|---------|-------|
| POST | /api/v1/orders | Create a new order | 201 Created | 400 Bad Request |
| GET | /api/v1/orders | Retrieve all orders | 200 OK | — |
| GET | /api/v1/orders/{id} | Retrieve an order by ID | 200 OK | 404 Not Found |
| PUT | /api/v1/orders/{id} | Update an existing order | 200 OK | 404 / 400 |
| DELETE | /api/v1/orders/{id} | Remove an order | 204 No Content | 404 Not Found |

## Error Response Format

All errors return a consistent JSON structure:

**404 Not Found:**
```json
{
  "timestamp": "2026-05-14T13:00:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Order with id abc123 not found"
}
```

**400 Validation Error:**
```json
{
  "timestamp": "2026-05-14T13:00:00.000",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "clientName": "Client name cannot be empty",
    "deliveryDate": "Delivery date is required"
  }
}
```

## ⚙ How to Run

### Option A: Docker Compose (Recommended)

1. Copy the environment template and fill in your values:
```bash
cp .env.example .env
```

2. Start the application:
```bash
docker-compose up --build
```

This starts both MongoDB and the Spring Boot application. The API will be available at `http://localhost:9000`.

To stop:
```bash
docker-compose down
```

### Run Tests (no Docker needed)

```bash
./mvnw test
```

Tests use `@WebMvcTest` with mocked services — no database required.

## 📘 API Usage Examples

**Create an order:**
```bash
curl -s -X POST http://localhost:9000/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Jess",
    "deliveryDate": "2026-05-20",
    "items": [
      {"fruitName": "Mango", "quantityInKilos": 5},
      {"fruitName": "Banana", "quantityInKilos": 3}
    ]
  }'
```

**Response (201 Created):**
```json
{
  "id": "6a05b704294ad7a2f61d62a0",
  "clientName": "Jess",
  "deliveryDate": "2026-05-20",
  "items": [
    {"fruitName": "Mango", "quantityInKilos": 5},
    {"fruitName": "Banana", "quantityInKilos": 3}
  ]
}
```

**Get all orders:**
```bash
curl http://localhost:9000/api/v1/orders
```

**Get order by ID:**
```bash
curl http://localhost:9000/api/v1/orders/6a05b704294ad7a2f61d62a0
```

**Update an order:**
```bash
curl -s -X PUT http://localhost:9000/api/v1/orders/6a05b704294ad7a2f61d62a0 \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Jess",
    "deliveryDate": "2026-05-25",
    "items": [
      {"fruitName": "Grape", "quantityInKilos": 10}
    ]
  }'
```

**Delete an order:**
```bash
curl -s -X DELETE http://localhost:9000/api/v1/orders/6a05b704294ad7a2f61d62a0
```
Response: 204 No Content

**Try invalid input:**
```bash
curl -s -X POST http://localhost:9000/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"clientName": "", "items": []}'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-05-14T13:00:00.000",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "clientName": "Client name cannot be empty",
    "deliveryDate": "Delivery date is required",
    "items": "The order must contain at least one item"
  }
}
```

## 🧪 Test Coverage

| Metric | Count |
|--------|-------|
| Total Tests | 15 (0 failures) |
| Controller Tests (@WebMvcTest) | 15 |

**Tests by Endpoint:**

✅ **POST /api/v1/orders — 6 tests:**
Create order with valid data returns 201, blank clientName returns 400, null deliveryDate returns 400, past deliveryDate returns 400, empty items list returns 400, null quantity returns 400

✅ **GET /api/v1/orders — 2 tests:**
Get all orders returns 200 with list, returns 200 with empty list when no orders

✅ **GET /api/v1/orders/{id} — 2 tests:**
Get by valid ID returns 200, non-existing ID returns 404

✅ **PUT /api/v1/orders/{id} — 3 tests:**
Update with valid data returns 200, non-existing ID returns 404, invalid data returns 400

✅ **DELETE /api/v1/orders/{id} — 2 tests:**
Delete existing order returns 204, non-existing ID returns 404

## 🏗 Architecture & Design Decisions

- **DTO over Entity exposure:** The client never interacts with JPA entities. `OrderRequestDto` controls what data enters the system (`clientName`, `deliveryDate`, `items`) — the database stores `_id` and `_class`. `OrderResponseDto` controls what data leaves. This prevents mass assignment attacks and decouples the API contract from the database schema.

- **MongoDB embedded subdocuments:** `OrderItem` is stored inside the `Order` document, not in a separate collection. This matches the access pattern — you always read/write items together with their order. No joins, no N+1 queries.

- **`@Future` for delivery date validation:** The spec requires delivery dates to be at least tomorrow. `@Future` rejects today and all past dates at the validation layer, before hitting the service.

- **Static mapper with private constructor:** `OrderMapper` is a stateless utility class. The private constructor with `@NoArgsConstructor(access = AccessLevel.PRIVATE)` prevents instantiation. Static methods keep it simple — no Spring dependency injection needed for pure data conversion.

- **Non-root Docker container:** The production image runs as `springuser`, not root. This follows the Principle of Least Privilege — a core cybersecurity concept that limits the blast radius if the application is compromised.

- **MongoDB healthcheck with `start_period`:** Docker Compose includes a `start_period: 30s` to account for MongoDB's initialization cycle (which involves two startups — one without auth for init scripts, one with auth for production). Without this, the app container starts before MongoDB is ready.

- **Credential management with `.env` + `.gitignore`:** All secrets live in `.env` (excluded from Git). `docker-compose.yml` uses `${VARIABLE}` references. The `.env.example` with placeholders shows the required structure without exposing real values.

## 🐳 Docker Compose & Environment Configuration

Credentials are stored in a `.env` file (excluded from Git via `.gitignore`). Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

`.env.example` (committed — shows structure without real values):
```
SPRING_DATA_MONGODB_URI=mongodb://your_app_user:your_app_password@mongodb:27017/fruit_order_db?authSource=fruit_order_db
MONGODB_LOG_LEVEL=INFO

MONGO_INITDB_ROOT_USERNAME=your_root_username
MONGO_INITDB_ROOT_PASSWORD=your_root_password
MONGO_INITDB_DATABASE=fruit_order_db

APP_DB_USER=your_app_username
APP_DB_PASS=your_app_password
```

## 📋 Potential Improvements

- Integration tests with Testcontainers for full end-to-end testing against real MongoDB (blocked by Docker Desktop 4.65 API v1.53 incompatibility with `docker-java` 3.4.0 — tracked for future resolution).
- Add `createdAt` and `updatedAt` timestamps with `@CreatedDate` and `@LastModifiedDate` for audit trails.
- Add pagination for `GET /api/v1/orders` using Spring Data's `Pageable`.
- Add `spring.jackson.default-property-inclusion=non_null` to exclude null fields from responses.
- Integration with Swagger/OpenAPI for interactive API documentation.

## 🗺 Roadmap

- **Level 1:** ✅ H2 in-memory database, single entity, Dockerfile, 18 tests.
- **Level 2:** ✅ MySQL with Docker Compose, `@ManyToOne` relationship, 15 tests.
- **Level 3:** ✅ MongoDB persistence with embedded subdocuments for managing fruit orders, 15 tests.