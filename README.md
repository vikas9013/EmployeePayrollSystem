# Employee Payroll System 💼

A console-based Java application that demonstrates core Object-Oriented Programming (OOP) concepts through a simple employee payroll management system.

## 📌 About the Project

This project models a basic payroll system where different types of employees (full-time and part-time) can be added, displayed, and removed. It was built to practice and demonstrate OOP principles in Java.

## 🧠 OOP Concepts Demonstrated

- **Abstraction** – `Employee` is an abstract class with an abstract `calculateSalary()` method
- **Inheritance** – `FullTimeEmployee` and `PartTimeEmployee` extend the `Employee` class
- **Polymorphism** – Salary is calculated differently for each employee type at runtime
- **Encapsulation** – All fields are private and accessed via public getters

## 🛠️ Tech Stack

- Java 24
- Maven

## 📁 Project Structure

```
src/
└── main/
    └── java/
        └── com/vikas/
            ├── Employee.java          # Abstract base class
            ├── FullTimeEmployee.java  # Full-time employee (fixed monthly salary)
            ├── PartTimeEmployee.java  # Part-time employee (hours × hourly rate)
            ├── PayrollSystem.java     # Manages employee records
            └── Main.java             # Entry point
```

## ▶️ How to Run

**Prerequisites:** Java 17+ and Maven installed

```bash
# Clone the repository
git clone https://github.com/your-username/EmployeePayrollSystem.git

# Navigate to project folder
cd EmployeePayrollSystem

# Build the project
mvn compile

# Run the application
mvn exec:java -Dexec.mainClass="com.vikas.Main"
```

## 📊 Sample Output

```
Employee details:
FullTimeEmployee[name=Vikas, id=1001, salary=85000.0]
PartTimeEmployee[name=Rahul, id=1002, salary=8000.0]
Removing Employees
Remaining Employees details:
FullTimeEmployee[name=Vikas, id=1001, salary=85000.0]
```

## 🚀 Future Improvements

- Add a REST API using Spring Boot
- Integrate a database (MySQL / H2) for persistent storage
- Add more comprehensive unit tests
- Add tax deduction logic to salary calculation

## 👨‍💻 Author

**Vikas** – 3rd Year B.Tech Student  
[GitHub](https://github.com/vikas9013) | [LinkedIn](https://www.linkedin.com/in/vikas-singh-rawat-4aa687294/)