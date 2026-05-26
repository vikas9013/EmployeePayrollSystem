# 🧾 Employee Payroll System

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.3.4-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Containerization-blue)
![License](https://img.shields.io/badge/License-MIT-green)

A **production-ready Spring Boot backend** that manages employee payroll for full-time and part-time employees — with a fully **AI-Powered Onboarding Pipeline** that automates email creation, Slack invites, training assignments, payroll configuration, and generates a personalized welcome message using **Groq (LLaMA 3.3)** whenever a new employee is hired.

> 💡 Handles complete employee lifecycle — from onboarding to payroll — with automation, security, and scalability in mind.
> 🚀 Designed as a **production-level backend system**, not just a CRUD API.

---

## 🌐 Live Demo

🔗 **Swagger UI:** https://employeepayroll-app.onrender.com/swagger-ui.html
🔗 **Health Check:** https://employeepayroll-app.onrender.com/actuator/health

> ⚠️ Hosted on Render free tier — may take **50 seconds to wake up** on first request. Please wait and retry if it doesn't load immediately.

**Test credentials:**
| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` |
| `hr` | `hr123` | `ROLE_HR` |

---

## ⚡ Quick Overview

- 🔐 JWT Authentication + Role-Based Access Control (RBAC)
- 🤖 AI-powered onboarding via Groq LLaMA 3.3 70B
- ⚡ Redis caching with smart eviction strategy
- 🧱 Flyway database migrations (version-controlled schema)
- 🛡️ Rate limiting with Bucket4j (prevents AI API abuse)
- 📊 Prometheus metrics + Spring Actuator health checks
- 🗑️ Soft-delete (no permanent data loss)
- 🐳 Fully Dockerized (PostgreSQL + Redis + App)
- 🧪 Unit + Integration testing with Testcontainers + JaCoCo

---

## 🎯 Real-World Use Case

This project simulates an internal HR system used by companies to:

- Automate employee onboarding workflows end-to-end
- Reduce manual HR operations with a 5-step pipeline
- Ensure consistent payroll configuration for all employee types
- Improve new employee experience using AI-generated welcome messages

---

## 🏗️ System Architecture

```
Client (Swagger UI / curl / Postman)
         │
         ▼
  Spring Security (JWT Filter)
         │
         ▼
   REST Controllers
         │
         ├──→ Redis Cache (GET by ID)
         │
         ├──→ Service Layer
         │         │
         │         ├──→ PostgreSQL (via JPA + Flyway)
         │         │
         │         └──→ Onboarding Pipeline
         │                   │
         │                   ├──→ Email Service
         │                   ├──→ Slack Service
         │                   ├──→ Training Service
         │                   ├──→ Payroll Setup Service
         │                   └──→ Groq AI API (WebClient)
         │
         ▼
    JSON Response (DTO)
```

> Note: This is a **modular monolith designed with microservice principles**.

---

## 🚀 Tech Stack

| Category | Technology | Details |
| --- | --- | --- |
| Backend | Java 21, Spring Boot 3.3.4 | Core framework |
| Security | Spring Security + JJWT 0.12.6 | JWT stateless auth |
| Database | PostgreSQL + Spring Data JPA | Hibernate ORM |
| Migrations | Flyway | Version-controlled schema |
| Caching | Redis + Spring Cache | Response caching |
| AI | Groq API (LLaMA 3.3 70B) | Welcome message generation |
| HTTP Client | Spring WebFlux WebClient | Async AI API calls |
| Rate Limiting | Bucket4j | 10 req/min on onboard |
| Observability | Spring Actuator + Micrometer + Prometheus | Health + metrics |
| API Docs | SpringDoc OpenAPI (Swagger UI) | Interactive docs |
| Mapping | MapStruct | DTO mapping |
| Boilerplate | Lombok | Clean code |
| Testing | JUnit + Mockito + Testcontainers + JaCoCo | Full test suite |
| Build | Maven | Dependency management |
| Deployment | Docker + Docker Compose + Render | One-command setup |

---

## 🧠 OOP Concepts Demonstrated

| Concept | How It's Used |
| --- | --- |
| **Abstraction** | `Employee` is an abstract class with abstract `calculateSalary()` method |
| **Inheritance** | `FullTimeEmployee` and `PartTimeEmployee` extend `Employee` |
| **Polymorphism** | Each subclass overrides `calculateSalary()` with its own logic |
| **Encapsulation** | All fields are private with public getters/setters via Lombok |

---

## 🔐 Security & Roles

The entire API is secured with **JWT (JSON Web Token)** stateless authentication and role-based access control.

### Roles & Permissions

| Role | Permissions |
| --- | --- |
| `ROLE_ADMIN` | Full access — GET, POST, PUT, DELETE |
| `ROLE_HR` | Read-only access — GET endpoints only |

### Default Users (auto-created on first startup by DataSeeder)

| Username | Password | Role |
| --- | --- | --- |
| `admin` | `admin123` | `ROLE_ADMIN` |
| `hr` | `hr123` | `ROLE_HR` |

> ⚠️ **Change these passwords immediately before going to production!**

### How Authentication Works

1. Call `POST /api/auth/login` with your credentials
2. Receive a signed JWT token in the response
3. Pass the token as `Authorization: Bearer <token>` on every subsequent request
4. Tokens are fully stateless — no sessions stored on the server

---

## 📁 Project Structure

```
src/
└── main/java/com/vikas/
    ├── PayrollApplication.java
    ├── config/
    │   ├── SecurityConfig.java              # JWT auth, CORS, role-based route protection
    │   ├── SwaggerConfig.java               # Swagger UI + Bearer auth configuration
    │   └── DataSeeder.java                  # Seeds default admin & HR users on startup
    ├── controller/
    │   ├── AuthController.java              # POST /api/auth/login
    │   └── EmployeeController.java          # Employee CRUD + onboarding (rate limited)
    ├── service/
    │   ├── AuthService.java                 # Login — verifies credentials, issues JWT
    │   ├── EmployeeService.java             # Core CRUD + Redis caching + soft-delete
    │   ├── OnboardingService.java           # Orchestrates the 5-step onboarding pipeline
    │   ├── AIOnboardingService.java         # AI welcome message via Groq API
    │   ├── EmailService.java                # Generates work email address
    │   ├── SlackService.java                # Sends Slack workspace invite
    │   ├── TrainingService.java             # Assigns training modules by designation
    │   └── PayrollSetupService.java         # Configures payroll (FULLTIME or PARTTIME)
    ├── entity/
    │   ├── Employee.java                    # Abstract base — soft-delete, audit timestamps
    │   ├── FullTimeEmployee.java            # monthlySalary field
    │   ├── PartTimeEmployee.java            # hoursWorked + hourlyRate fields
    │   └── User.java                        # Login credentials (BCrypt hashed)
    ├── security/
    │   ├── JwtUtil.java                     # Token generation & validation
    │   └── JwtAuthFilter.java               # Per-request JWT filter
    ├── repository/
    │   ├── EmployeeRepository.java
    │   └── UserRepository.java
    ├── dto/
    │   ├── EmployeeRequestDTO.java
    │   ├── EmployeeResponseDTO.java
    │   ├── SalaryResponseDTO.java
    │   ├── OnboardingResponseDTO.java
    │   ├── LoginRequestDTO.java
    │   └── LoginResponseDTO.java
    ├── enums/
    │   └── EmployeeType.java                # FULLTIME | PARTTIME
    ├── exception/
    │   ├── EmployeeNotFoundException.java
    │   └── OnboardingException.java
    └── ExceptionHandler/
        └── GlobalExceptionHandler.java      # Handles 400, 401, 403, 404, 429, 500
```

---

## 🗄️ Database Schema (Flyway Managed)

Schema is version-controlled via `V1__init_schema.sql` and applied **automatically on startup** — no manual SQL needed.

| Table | Contents |
| --- | --- |
| `employees` | id, name, designation, deleted_at (soft-delete), created_at, updated_at |
| `fulltime_employees` | monthly_salary |
| `parttime_employees` | hours_worked, hourly_rate |
| `users` | username, BCrypt-hashed password, role |
| `audit_log` | entity_type, action, changed_by, old_value, new_value, changed_at |

> Records are **never physically deleted**. DELETE sets the `deleted_at` timestamp (soft-delete).

---

## ☁️ Deployed on Render

The app is live on **Render** (free tier) with PostgreSQL as the database.

### Required Environment Variables

| Key | Description |
| --- | --- |
| `spring.datasource.url` | PostgreSQL connection URL |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `jwt.secret` | Random string, minimum 32 characters |
| `jwt.expiration-ms` | Token expiry in ms (e.g. `86400000` = 24 hours) |
| `groq.api.key` | Groq API key from [console.groq.com](https://console.groq.com) |
| `spring.cache.type` | Set to `none` on free tier (no Redis available) |
| `management.health.redis.enabled` | Set to `false` on free tier |

> ⚠️ Free tier instances **spin down after inactivity** — first request may take 50+ seconds.

---

## ⚙️ Local Setup & Configuration

### Prerequisites

- Java 21+
- Maven
- PostgreSQL running locally
- Redis running locally
- Groq API Key — free at [console.groq.com](https://console.groq.com)

### 1. Clone the repository

```bash
git clone https://github.com/vikas9013/EmployeePayrollSystem.git
cd EmployeePayrollSystem
```

### 2. Create the database

```sql
CREATE DATABASE payrolldb;
```

### 3. Configure credentials

Set these environment variables:

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | Your PostgreSQL username |
| `DB_PASSWORD` | Your PostgreSQL password |
| `GROQ_API_KEY` | Your Groq API key from console.groq.com |
| `JWT_SECRET` | A random string, minimum 32 characters |

**Windows CMD:**
```cmd
set DB_USERNAME=postgres
set DB_PASSWORD=yourpassword
set GROQ_API_KEY=gsk_your_key_here
set JWT_SECRET=ThisIsASecretKeyThatMustBe32CharsLong!!
```

**Mac/Linux:**
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export GROQ_API_KEY=gsk_your_key_here
export JWT_SECRET=ThisIsASecretKeyThatMustBe32CharsLong!!
```

### 4. Run the application

```bash
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

> Flyway automatically creates all tables on first startup. No manual SQL needed.

---

## 🐳 Docker Deployment

Run the full stack — App + PostgreSQL + Redis — with a single command:

```bash
docker-compose up --build
```

| Service | Port |
| --- | --- |
| App | `8080` |
| PostgreSQL | `5432` |
| Redis | `6379` |

```bash
# Stop all services
docker-compose down

# Stop and wipe the database
docker-compose down -v
```

> Replace placeholder credentials in `docker-compose.yml` before deploying to any environment.

---

## 📖 Swagger UI

Interactive API documentation — test every endpoint directly in the browser.

| URL | Description |
| --- | --- |
| `https://employeepayroll-app.onrender.com/swagger-ui.html` | Live Swagger UI |
| `https://employeepayroll-app.onrender.com/v3/api-docs` | Live OpenAPI JSON |
| `http://localhost:8080/swagger-ui.html` | Local Swagger UI |
| `http://localhost:8080/v3/api-docs` | Local OpenAPI JSON |

### How to authenticate in Swagger UI

1. Call `POST /api/auth/login` → **Try it out** → Execute
2. Copy the `token` from the response
3. Click **Authorize 🔒** at the top of the page
4. Enter `Bearer <your-token>` → click **Authorize**
5. All subsequent requests will include the token automatically ✅

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | ❌ Public | Login and receive a JWT token |

### Employee Management

| Method | Endpoint | Role Required | Description |
| --- | --- | --- | --- |
| `GET` | `/api/employees` | ADMIN or HR | Get all employees (paginated) |
| `GET` | `/api/employees/{id}` | ADMIN or HR | Get employee by ID (Redis cached) |
| `GET` | `/api/employees/{id}/salary` | ADMIN or HR | Get calculated salary |
| `POST` | `/api/employees/onboard` | ADMIN only | Add employee + full onboarding pipeline |
| `PUT` | `/api/employees/{id}` | ADMIN only | Update an employee |
| `DELETE` | `/api/employees/{id}` | ADMIN only | Soft-delete an employee |

### Observability

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/actuator/health` | ❌ Public | Application health check |
| `GET` | `/actuator/prometheus` | ❌ Public | Prometheus metrics |

---

## 📝 Sample Requests & Responses

### Step 1 — Login

```
POST /api/auth/login
```
```json
{
  "username": "admin",
  "password": "admin123"
}
```
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

Use as: `Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...`

---

### Step 2 — Onboard a Full-Time Employee

```
POST /api/employees/onboard
Authorization: Bearer <your-token>
```
```json
{
  "name": "Vikas Singh Rawat",
  "designation": "Software Engineer",
  "type": "FULLTIME",
  "monthlySalary": 85000,
  "hoursWorked": 0,
  "hourlyRate": 0
}
```

### Onboard a Part-Time Employee

```
POST /api/employees/onboard
Authorization: Bearer <your-token>
```
```json
{
  "name": "Rahul Mehta",
  "designation": "Intern",
  "type": "PARTTIME",
  "monthlySalary": 0,
  "hoursWorked": 40,
  "hourlyRate": 200
}
```

### Onboarding Success Response

```json
{
  "employeeId": 1,
  "employeeName": "Vikas Singh Rawat",
  "workEmail": "vikas.singh.rawat@company.com",
  "slackInviteSent": true,
  "trainingAssigned": true,
  "payrollConfigured": true,
  "message": "Onboarding completed successfully for Vikas Singh Rawat",
  "aiOnboardingMessage": "Welcome aboard, Vikas! We are thrilled to have you join our Engineering team."
}
```

### Get All Employees (Paginated)

```
GET /api/employees?page=0&size=10&sort=id,asc
Authorization: Bearer <your-token>
```

---

## 🧪 Testing the API

### Using Swagger UI (Recommended)

1. Open `https://employeepayroll-app.onrender.com/swagger-ui.html`
2. Login → copy token → click **Authorize 🔒**
3. Use **Try it out** on any endpoint

### Recommended Testing Order

1. `POST /api/auth/login` → get JWT token
2. `POST /api/employees/onboard` → create employee, note the `id`
3. `GET /api/employees` → confirm employee is listed
4. `GET /api/employees/{id}` → fetch by id (Redis cached on second call)
5. `GET /api/employees/{id}/salary` → verify salary calculation
6. `PUT /api/employees/{id}` → update (clears Redis cache)
7. `DELETE /api/employees/{id}` → soft-delete (clears Redis cache)

### Using curl (Windows CMD)

```cmd
:: Login
curl -X POST https://employeepayroll-app.onrender.com/api/auth/login -H "Content-Type: application/json" -d "{\"username\": \"admin\", \"password\": \"admin123\"}"

:: Get all employees
curl -X GET https://employeepayroll-app.onrender.com/api/employees -H "Authorization: Bearer TOKEN"

:: Get by ID
curl -X GET https://employeepayroll-app.onrender.com/api/employees/1 -H "Authorization: Bearer TOKEN"

:: Get salary
curl -X GET https://employeepayroll-app.onrender.com/api/employees/1/salary -H "Authorization: Bearer TOKEN"

:: Onboard full-time
curl -X POST https://employeepayroll-app.onrender.com/api/employees/onboard -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN" -d "{\"name\": \"Vikas\", \"designation\": \"Software Engineer\", \"type\": \"FULLTIME\", \"monthlySalary\": 85000, \"hoursWorked\": 0, \"hourlyRate\": 0}"

:: Onboard part-time
curl -X POST https://employeepayroll-app.onrender.com/api/employees/onboard -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN" -d "{\"name\": \"Rahul\", \"designation\": \"Intern\", \"type\": \"PARTTIME\", \"monthlySalary\": 0, \"hoursWorked\": 40, \"hourlyRate\": 200}"

:: Update
curl -X PUT https://employeepayroll-app.onrender.com/api/employees/1 -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN" -d "{\"name\": \"Vikas Updated\", \"designation\": \"Senior Engineer\", \"type\": \"FULLTIME\", \"monthlySalary\": 95000, \"hoursWorked\": 0, \"hourlyRate\": 0}"

:: Delete
curl -X DELETE https://employeepayroll-app.onrender.com/api/employees/1 -H "Authorization: Bearer TOKEN"
```

---

## 🤖 AI-Powered Onboarding Pipeline

When `POST /api/employees/onboard` is called, **5 steps** execute automatically:

```
New Employee Saved to DB
        │
        ▼
1. EmailService           → Creates work email (name@company.com)
        │
        ▼
2. SlackService           → Sends Slack workspace invite
        │
        ▼
3. TrainingService        → Assigns training modules by designation
        │
        ▼
4. PayrollSetupService    → Configures payroll (FULLTIME or PARTTIME)
        │
        ▼
5. AIOnboardingService    → Generates personalized welcome message via Groq AI
        │
        ▼
   OnboardingResponseDTO returned ✅
```

> The onboard endpoint is **rate-limited to 10 requests per minute** to prevent AI API abuse.

### Training Modules by Designation

| Designation | Modules Assigned |
| --- | --- |
| Engineer / Developer / SDE | Company Orientation, Secure Coding Practices, Git Workflow |
| Manager / Team Lead | Company Orientation, Leadership Fundamentals, HR Policies |
| HR / Human Resources | Company Orientation, Recruitment Basics, Compliance Training |
| Any other | Company Orientation, Code of Conduct |

---

## ⚡ Redis Caching

| Operation | Cache Behaviour |
| --- | --- |
| `GET /api/employees/{id}` | Cached under key `employees::{id}` |
| `PUT /api/employees/{id}` | Cache evicted on update |
| `DELETE /api/employees/{id}` | Cache evicted on delete |

---

## 📊 Observability

| Feature | Details |
| --- | --- |
| **Health Check** | `GET /actuator/health` — reports UP/DOWN |
| **Prometheus Metrics** | `GET /actuator/prometheus` — JVM, HTTP, custom metrics |
| **Structured Logging** | All controllers & services use `@Slf4j` with consistent log levels |

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests + generate JaCoCo coverage report
mvn verify
```

Coverage report: `target/site/jacoco/index.html`

### Test Suite

| Test Class | What It Covers |
| --- | --- |
| `EmployeeControllerTest` | Full HTTP layer tests with MockMvc |
| `EmployeeEntityTest` | OOP inheritance & salary calculation |
| `OnboardingServiceTest` | Mocked 5-step pipeline |
| `AIOnboardServiceTest` | Mocked Groq API calls |
| `ServiceUnitTests` | Core service logic |
| `PayrollSystemTest` | Integration tests with real PostgreSQL (Testcontainers) |

---

## 🚀 Future Improvements

- Convert into a fully distributed microservices architecture
- Add Kafka / RabbitMQ for event-driven async onboarding
- Integrate real email (SendGrid) and Slack APIs
- Add a React frontend dashboard
- Deploy on AWS with a CI/CD pipeline (GitHub Actions)
- Add refresh token support for longer JWT sessions

> This project is actively evolving towards a full production-grade system.

---

## 📸 Screenshots

### Swagger UI — All Endpoints
![Login](screenshots/login-response.png)

### Login — JWT Token Response
![Swagger UI](screenshots/swagger-ui.png)

### Onboarding — AI Pipeline Response
![Onboarding](screenshots/onboarding-response.png)

### Salary Calculation Response
![Salary](screenshots/salary-response.png)

---

## 🔒 Security Notes

- Never commit `application.properties` with real credentials — it is in `.gitignore`
- Always use environment variables for `DB_PASSWORD`, `GROQ_API_KEY`, and `JWT_SECRET`
- Use `application.properties.example` as a safe template for new contributors
- Change default admin/HR passwords before any production deployment
- In production, replace `allowedOrigins("*")` in `SecurityConfig` with your actual frontend URL

---

## 👨‍💻 Author

**Vikas Singh Rawat** — Backend Developer | Java | Spring Boot | System Design

[![GitHub](https://img.shields.io/badge/GitHub-vikas9013-black?logo=github)](https://github.com/vikas9013)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Vikas%20Singh%20Rawat-blue?logo=linkedin)](https://www.linkedin.com/in/vikas-singh-rawat-4aa687294/)

> 🚀 Open to internship and backend development opportunities