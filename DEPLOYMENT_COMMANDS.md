# 📝 Render Deployment Commands

Copy and paste these commands to get your project ready for Render deployment.

---

## 🔧 Prerequisites (Run These First)

### Check your environment
```powershell
# PowerShell (Windows)
java -version          # Should be 21 or higher
mvn --version          # Should be 3.6+
git --version          # Should be 2.0+
```

---

## 📦 Local Testing Before Deployment

### Build the project locally
```powershell
cd C:\Users\vikas\IdeaProjects\EmployeePayrollSystem
mvn clean package -DskipTests
```

### Test with Docker locally (optional)
```powershell
# Build Docker image
docker build -t payroll-app:local .

# Run the container (requires PostgreSQL and Redis running)
docker run -p 8080:8080 `
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/payrolldb `
  -e DB_USERNAME=postgres `
  -e DB_PASSWORD=your_password `
  -e REDIS_HOST=host.docker.internal `
  -e REDIS_PORT=6379 `
  -e GROQ_API_KEY=your_key `
  -e JWT_SECRET=ThisIsASecretKeyThatMustBe32CharsLong!! `
  payroll-app:local
```

### Run locally with Maven (simpler)
```powershell
# Set environment variables in PowerShell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your_password"
$env:GROQ_API_KEY = "gsk_your_key"
$env:JWT_SECRET = "ThisIsASecretKeyThatMustBe32CharsLong!!"

# Run with Maven
mvn spring-boot:run
```

---

## 📤 Push to GitHub

### Initialize git (if not already done)
```powershell
cd C:\Users\vikas\IdeaProjects\EmployeePayrollSystem
git init
git config user.name "Your Name"
git config user.email "your@email.com"
```

### Add your GitHub remote
```powershell
git remote add origin https://github.com/YOUR_USERNAME/EmployeePayrollSystem.git
```

### Commit and push
```powershell
git add .
git commit -m "Initial commit: Employee Payroll System ready for Render deployment"
git branch -M main
git push -u origin main
```

### Verify it's on GitHub
```powershell
git log --oneline -1  # Shows your latest commit
```

---

## 🔑 Generate Strong Secrets

### Generate a strong JWT_SECRET (32+ characters)
```powershell
# PowerShell method 1: Using System.Web.Security
[Environment]::SetEnvironmentVariable("RandomString", $([System.Web.Security.Membership]::GeneratePassword(32, 4)), "User")

# Or just create a random strong string manually and use it
# Example: aB3$cDe9!Fg2@hIj4%kLm6^nOp7&qRs8*tUv0(wXy1)zAb
```

### Generate strong database password
```powershell
# PowerShell: Generate random string
-join ([char[]](33..126) | Get-Random -Count 16)
```

---

## 🚀 Deploy to Render

### Manual Deployment (5 steps in QUICK_DEPLOY.md)

### Or use Render CLI (optional)
```bash
# Install Render CLI (if available for Windows)
npm install -g @render-oss/render-cli

# Login to Render
render login

# Deploy
render deploy --repo https://github.com/YOUR_USERNAME/EmployeePayrollSystem
```

---

## ✅ Post-Deployment Verification

### Check application health (replace with your URL)
```powershell
$url = "https://payroll-app-xxxx.onrender.com"

# Test health endpoint
Invoke-WebRequest -Uri "$url/actuator/health" | ConvertFrom-Json

# Test Swagger UI (opens in browser)
Start-Process "$url/swagger-ui.html"

# Test login endpoint
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$url/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody | ConvertFrom-Json
```

---

## 📝 Update GitHub README

### Add deployment section
```markdown
## 🌐 Live Deployment

**App is deployed and live on Render:**

🔗 [View Live Application](https://payroll-app-xxxx.onrender.com/swagger-ui.html)

### Quick Links
- [Swagger UI](https://payroll-app-xxxx.onrender.com/swagger-ui.html)
- [Health Check](https://payroll-app-xxxx.onrender.com/actuator/health)
- [API Docs](https://payroll-app-xxxx.onrender.com/v3/api-docs)

### Test Credentials
- Username: `admin`
- Password: `admin123`

> ⚠️ Change these before sharing publicly!
```

### Update your Windows PATH to ensure git is included
```powershell
# Check if git is in PATH
git --version

# If not found, add it manually to PATH
# Git typically installs to: C:\Program Files\Git\bin
$env:Path += ";C:\Program Files\Git\bin"
git --version  # Should work now
```

---

## 🔄 Update Code and Redeploy

### After making changes
```powershell
cd C:\Users\vikas\IdeaProjects\EmployeePayrollSystem

# Stage changes
git add .

# Commit
git commit -m "Feature: description of changes"

# Push (Render automatically redeploys)
git push origin main

# Watch deployment in Render dashboard
# Or check logs with:
render logs --service payroll-app
```

---

## 🧪 Common Test Commands

### Login and get JWT token
```powershell
$response = Invoke-WebRequest -Uri "https://payroll-app-xxxx.onrender.com/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123"}'

$token = $response.Content | ConvertFrom-Json | Select-Object -ExpandProperty token
$token  # Copy this value
```

### Get all employees (authenticated)
```powershell
Invoke-WebRequest -Uri "https://payroll-app-xxxx.onrender.com/api/employees" `
  -Method GET `
  -Headers @{"Authorization" = "Bearer $token"}
```

### Onboard a new full-time employee
```powershell
$body = @{
    name = "Test Employee"
    designation = "Software Engineer"
    type = "FULLTIME"
    monthlySalary = 85000
    hoursWorked = 0
    hourlyRate = 0
} | ConvertTo-Json

Invoke-WebRequest -Uri "https://payroll-app-xxxx.onrender.com/api/employees/onboard" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{"Authorization" = "Bearer $token"} `
  -Body $body
```

---

## ⚙️ Environment Variables Needed on Render

```
DB_URL=postgresql://user:password@host:5432/payrolldb
DB_USERNAME=payroll_user
DB_PASSWORD=your_strong_password
REDIS_HOST=redis-xxxx.render.com
REDIS_PORT=6379
GROQ_API_KEY=gsk_your_api_key
JWT_SECRET=your_32_char_secret_key
```

---

## 🚨 If Things Go Wrong

### Check Render logs
```bash
# View real-time logs
render tail --service payroll-app

# View last N lines
render logs --service payroll-app --lines 100
```

### Restart the service
```bash
render restart --service payroll-app
```

### Redeploy from specific GitHub commit
```bash
# Push a new commit to GitHub
git push origin main

# Render automatically picks it up and redeploys
```

---

## 📋 Full Deployment Workflow

1. **Prepare**: Run `mvn clean package -DskipTests`
2. **Test locally**: `mvn spring-boot:run`
3. **Commit**: `git add . && git commit -m "message"`
4. **Push**: `git push origin main`
5. **Deploy on Render**: (5-step process in QUICK_DEPLOY.md)
6. **Verify**: Open `/swagger-ui.html` in browser
7. **Test**: Use the interactive Swagger UI to test endpoints
8. **Share**: Copy your URL and add to GitHub/Resume

---

## 🎯 Quick Command Summary

```powershell
# Build
mvn clean package -DskipTests

# Test
mvn spring-boot:run

# Git push
git add . ; git commit -m "message" ; git push origin main

# Check Render logs
render logs --service payroll-app

# Restart Render service
render restart --service payroll-app
```

---

**Save these commands for quick reference during deployment!** 📌

