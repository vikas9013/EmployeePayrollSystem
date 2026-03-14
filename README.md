# 🧾 Employee Payroll System

A **Spring Boot REST API** that manages employee payroll for both full-time and part-time employees — with a fully **Automated AI-Powered Onboarding Pipeline** that creates a work email, sends a Slack invite, assigns training modules, configures payroll, and generates a personalized AI welcome message using **Groq (LLaMA 3.3)** whenever a new employee is hired.

---

## 🚀 Tech Stack

| Technology | Details |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.4 |
| Spring Data JPA | Hibernate ORM |
| PostgreSQL | Relational Database |
| Spring WebFlux | WebClient for AI API calls |
| Spring Validation | Request body validation |
| Groq API | Free AI — LLaMA 3.3 70B |
| SpringDoc OpenAPI | Swagger UI Documentation |
| Maven | Build tool |

---

## 🧠 OOP Concepts Demonstrated

| Concept | How It's Used |
|---|---|
| **Abstraction** | `Employee` is an abstract class with abstract `calculateSalary()` method |
| **Inheritance** | `FullTimeEmployee` and `PartTimeEmployee` extend `Employee` |
| **Polymorphism** | Each subclass overrides `calculateSalary()` with its own logic |
| **Encapsulation** | All fields are private with public getters/setters |

---

## 📁 Project Structure
```
src/
└── main/java/com/vikas/
    ├── PayrollApplication.java
    ├── config/
    │   └── SwaggerConfig.java               # Swagger UI configuration
    ├── controller/
    │   └── EmployeeController.java
    ├── service/
    │   ├── EmployeeService.java
    │   ├── OnboardingService.java
    │   ├── AIOnboardingService.java         # AI message via Groq
    │   ├── EmailService.java
    │   ├── SlackService.java
    │   ├── TrainingService.java
    │   └── PayrollSetupService.java
    ├── entity/
    │   ├── Employee.java
    │   ├── FullTimeEmployee.java
    │   └── PartTimeEmployee.java
    ├── repository/
    │   └── EmployeeRepository.java
    ├── dto/
    │   ├── EmployeeRequestDTO.java
    │   ├── EmployeeResponseDTO.java
    │   ├── SalaryResponseDTO.java
    │   └── OnboardingResponseDTO.java
    ├── enums/
    │   └── EmployeeType.java
    └── ExceptionHandler/
        ├── GlobalExceptionHandler.java
        └── OnboardingException.java
```

---

## ⚙️ Setup & Configuration

### Prerequisites
- Java 21+
- Maven
- PostgreSQL running locally
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
Copy the example file:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Set these environment variables:

| Variable | Description |
|---|---|
| `DB_USERNAME` | Your PostgreSQL username |
| `DB_PASSWORD` | Your PostgreSQL password |
| `GROQ_API_KEY` | Your Groq API key from console.groq.com |

**Windows CMD:**
```cmd
set DB_USERNAME=postgres
set DB_PASSWORD=yourpassword
set GROQ_API_KEY=gsk_your_key_here
```

**Mac/Linux:**
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export GROQ_API_KEY=gsk_your_key_here
```

### 4. Run the application
```bash
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

---

## 📖 Swagger UI — API Documentation

This project includes **Swagger UI** powered by SpringDoc OpenAPI for interactive API documentation and testing.

| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Interactive Swagger UI |
| `http://localhost:8080/v3/api-docs` | Raw OpenAPI JSON spec |

### How to use Swagger UI
1. Start the application
2. Open `http://localhost:8080/swagger-ui.html` in your browser
3. Click on any endpoint to expand it
4. Click **"Try it out"**
5. Fill in the request body or parameters
6. Click **"Execute"** to test the API live

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/employees` | Get all employees |
| `GET` | `/api/employees/{id}` | Get employee by ID |
| `GET` | `/api/employees/{id}/salary` | Get salary of an employee |
| `POST` | `/api/employees/onboard` | Add employee + run full onboarding pipeline |
| `PUT` | `/api/employees/{id}` | Update an employee |
| `DELETE` | `/api/employees/{id}` | Remove an employee |

---

## 📝 Sample Requests

### Onboard a Full-Time Employee
```json
POST /api/employees/onboard
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
```json
POST /api/employees/onboard
{
  "name": "Rahul Mehta",
  "designation": "Intern",
  "type": "PARTTIME",
  "monthlySalary": 0,
  "hoursWorked": 40,
  "hourlyRate": 200
}
```

### Success Response
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

---

## 🧪 Testing the API

### Using Swagger UI (Recommended)
Open `http://localhost:8080/swagger-ui.html` and use the **Try it out** button on any endpoint.

### Using curl — Windows CMD

**Get all employees:**
```cmd
curl -X GET http://localhost:8080/api/employees
```

**Get employee by ID:**
```cmd
curl -X GET http://localhost:8080/api/employees/1
```

**Get employee salary:**
```cmd
curl -X GET http://localhost:8080/api/employees/1/salary
```

**Onboard Full-Time Employee:**
```cmd
curl -X POST http://localhost:8080/api/employees/onboard -H "Content-Type: application/json" -d "{\"name\": \"Vikas\", \"designation\": \"Software Engineer\", \"type\": \"FULLTIME\", \"monthlySalary\": 85000, \"hoursWorked\": 0, \"hourlyRate\": 0}"
```

**Onboard Part-Time Employee:**
```cmd
curl -X POST http://localhost:8080/api/employees/onboard -H "Content-Type: application/json" -d "{\"name\": \"Rahul\", \"designation\": \"Intern\", \"type\": \"PARTTIME\", \"monthlySalary\": 0, \"hoursWorked\": 40, \"hourlyRate\": 200}"
```

**Update Employee:**
```cmd
curl -X PUT http://localhost:8080/api/employees/1 -H "Content-Type: application/json" -d "{\"name\": \"Vikas Updated\", \"designation\": \"Senior Engineer\", \"type\": \"FULLTIME\", \"monthlySalary\": 95000, \"hoursWorked\": 0, \"hourlyRate\": 0}"
```

**Delete Employee:**
```cmd
curl -X DELETE http://localhost:8080/api/employees/1
```

### Recommended Testing Order
1. `POST /onboard` → create an employee, note the `id` in response
2. `GET /api/employees` → confirm employee is listed
3. `GET /api/employees/{id}` → fetch by id
4. `GET /api/employees/{id}/salary` → check salary calculation
5. `PUT /api/employees/{id}` → update details
6. `DELETE /api/employees/{id}` → remove employee

---

## 🤖 AI-Powered Onboarding Pipeline

When `POST /api/employees/onboard` is called, **5 steps** run automatically:
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

### Training Modules by Designation

| Designation | Modules Assigned |
|---|---|
| Engineer / Developer / SDE | Company Orientation, Secure Coding Practices, Git Workflow |
| Manager / Team Lead | Company Orientation, Leadership Fundamentals, HR Policies |
| HR / Human Resources | Company Orientation, Recruitment Basics, Compliance Training |
| Any other | Company Orientation, Code of Conduct |

---

## 🗄️ Database Schema

| Table | Contents |
|---|---|
| `employees` | Base data — id, name, designation |
| `fulltime_employees` | Monthly salary |
| `parttime_employees` | Hours worked + hourly rate |

---

## 🔒 Security Notes

- Never commit `application.properties` with real credentials
- Always use environment variables for secrets
- `application.properties` is listed in `.gitignore`
- Use `application.properties.example` as a safe template

---

## 👨‍💻 Author

**Vikas Singh Rawat**  
[GitHub](https://github.com/vikas9013) · [LinkedIn](https://www.linkedin.com/in/vikas-singh-rawat-4aa687294/)