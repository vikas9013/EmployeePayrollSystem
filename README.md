# 🧾 Employee Payroll System

A **Spring Boot REST API** that manages employee payroll for both full-time and part-time employees — with an **Automated Onboarding Pipeline** that creates a work email, sends a Slack invite, assigns training modules, and configures payroll whenever a new employee is hired.

---

## 🚀 Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.0 |
| Spring Data JPA | - |
| PostgreSQL | - |
| Spring Validation | - |
| Maven | - |

---

## 🧠 OOP Concepts Demonstrated

| Concept | How It's Used |
|---|---|
| **Abstraction** | `Employee` is an abstract class with an abstract `calculateSalary()` method |
| **Inheritance** | `FullTimeEmployee` and `PartTimeEmployee` extend `Employee` |
| **Polymorphism** | Each subclass overrides `calculateSalary()` with its own logic |
| **Encapsulation** | All fields are private with public getters/setters |

---

## 📁 Project Structure

```
src/
└── main/java/com/vikas/
    ├── PayrollApplication.java              # Entry point
    ├── controller/
    │   └── EmployeeController.java          # REST endpoints
    ├── service/
    │   ├── EmployeeService.java             # Core business logic
    │   ├── OnboardingService.java           # Orchestrates all onboarding steps
    │   ├── EmailService.java                # Step 1 — creates work email
    │   ├── SlackService.java                # Step 2 — sends Slack invite
    │   ├── TrainingService.java             # Step 3 — assigns training modules
    │   └── PayrollSetupService.java         # Step 4 — configures payroll
    ├── entity/
    │   ├── Employee.java                    # Abstract base class
    │   ├── FullTimeEmployee.java            # Fixed monthly salary
    │   └── PartTimeEmployee.java            # Hours x hourly rate
    ├── repository/
    │   └── EmployeeRepository.java          # JPA Repository
    ├── dto/
    │   ├── EmployeeRequestDTO.java          # Request body for add/update
    │   ├── EmployeeResponseDTO.java         # Standard employee response
    │   ├── SalaryResponseDTO.java           # Salary query response
    │   └── OnboardingResponseDTO.java       # Onboarding pipeline result
    ├── enums/
    │   └── EmployeeType.java                # FULLTIME / PARTTIME
    └── ExceptionHandler/
        ├── GlobalExceptionHandler.java      # Centralized error handling
        └── OnboardingException.java         # Thrown when any onboarding step fails
```

---

## ⚙️ Setup & Configuration

### Prerequisites
- Java 17+
- Maven
- PostgreSQL running locally

### 1. Clone the repository
```bash
git clone https://github.com/YourUsername/EmployeePayrollSystem.git
cd EmployeePayrollSystem
```

### 2. Create the database
```sql
CREATE DATABASE payrolldb;
```

### 3. Configure credentials
Copy the example file and fill in your values:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Set these environment variables in your IDE or terminal:

| Variable | Description |
|---|---|
| `DB_USERNAME` | Your PostgreSQL username (e.g. `postgres`) |
| `DB_PASSWORD` | Your PostgreSQL password |

### 4. Run the application
```bash
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/employees` | Get all employees |
| `GET` | `/api/employees/{id}` | Get employee by ID |
| `GET` | `/api/employees/{id}/salary` | Get salary of an employee |
| `POST` | `/api/employees` | Add a new employee (save only) |
| `POST` | `/api/employees/onboard` | Add employee + run full onboarding pipeline |
| `PUT` | `/api/employees/{id}` | Update an employee |
| `DELETE` | `/api/employees/{id}` | Remove an employee |

---

## 📝 Sample Requests

### Add a Full-Time Employee (save only)
```json
POST /api/employees
{
  "name": "Vikas",
  "designation": "Software Engineer",
  "type": "FULLTIME",
  "monthlySalary": 85000
}
```

### Add a Part-Time Employee (save only)
```json
POST /api/employees
{
  "name": "Rahul",
  "designation": "Intern",
  "type": "PARTTIME",
  "hoursWorked": 40,
  "hourlyRate": 200
}
```

### Add Employee + Trigger Full Onboarding
```json
POST /api/employees/onboard
{
  "name": "Vikas",
  "designation": "Software Engineer",
  "type": "FULLTIME",
  "monthlySalary": 85000
}
```

#### Success Response
```json
{
  "employeeId": 1,
  "employeeName": "Vikas",
  "workEmail": "vikas@company.com",
  "slackInviteSent": true,
  "trainingAssigned": true,
  "payrollConfigured": true,
  "message": "Onboarding completed successfully for Vikas"
}
```

#### Failure Response (if any step fails)
```json
{
  "timestamp": "2025-03-13T10:45:22.123",
  "status": 500,
  "error": "Onboarding Failed",
  "failedStep": "SLACK_INVITE",
  "message": "Failed to send Slack invite to: vikas@company.com"
}
```

---

## 🔄 Automated Onboarding Pipeline

When `POST /api/employees/onboard` is called, 4 steps run automatically in sequence:

```
New Employee Saved to DB
        │
        ▼
1. EmailService         → Generates work email  (name@company.com)
        │
        ▼
2. SlackService         → Sends Slack workspace invite
        │
        ▼
3. TrainingService      → Assigns training modules by designation
        │
        ▼
4. PayrollSetupService  → Configures payroll (FULLTIME or PARTTIME)
        │
        ▼
   OnboardingResponseDTO returned ✅
```

If any step fails, execution stops immediately and the response includes `failedStep` and `message` so you know exactly what went wrong.

### Training Modules by Designation

| Designation | Modules Assigned |
|---|---|
| Engineer / Developer / SDE | Company Orientation, Secure Coding Practices, Git Workflow |
| Manager / Team Lead | Company Orientation, Leadership Fundamentals, HR Policies |
| HR / Human Resources | Company Orientation, Recruitment Basics, Compliance Training |
| Any other (e.g. Intern) | Company Orientation, Code of Conduct |

---

## 🗄️ Database

Uses **PostgreSQL**. Tables are auto-created by Hibernate on first run.

| Table | Contents |
|---|---|
| `employees` | Base data — id, name, designation |
| `fulltime_employees` | Monthly salary — joined to employees |
| `parttime_employees` | Hours worked + hourly rate — joined to employees |

---

## 👨‍💻 Author

**Vikas**  
Feel free to connect on [GitHub](https://github.com/vikas9013) or [LinkedIn](https://www.linkedin.com/in/vikas-singh-rawat-4aa687294/) for ant questions or feedback!