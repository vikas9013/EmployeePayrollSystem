# 🚀 Deployment Guide for Render

This guide will help you deploy the Employee Payroll System to Render and get a public URL for your GitHub repo and resume.

---

## 📋 Prerequisites

Before starting, ensure you have:
1. ✅ A GitHub account with this repository pushed
2. ✅ A Render account (free at https://render.com)
3. ✅ A Groq API key (free at https://console.groq.com)
4. ✅ Java 21+ and Maven installed locally (for testing before deployment)

---

## 🎯 Deployment Strategy

We'll deploy using **Render's native Docker support**, which is perfect for this Spring Boot application. Here's what we'll set up:

```
┌─────────────────────────────────────────────────┐
│         Your GitHub Repository                  │
│   (Connected to Render via webhook)              │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │   Render Web Service       │ 🐳 Docker
        │   (Your Java App)          │
        │   Port: 8080               │
        │   Auto-deploys on push     │
        └────────┬───────────────────┘
                 │
        ┌────────┼──────────────────┐
        │        │                  │
        ▼        ▼                  ▼
   ┌─────────┐ ┌──────────┐ ┌────────────────┐
   │PostgreSQL│ │  Redis   │ │ Environment   │
   │ Service  │ │ Service  │ │    Variables  │
   │          │ │          │ │ (Secrets)     │
   └─────────┘ └──────────┘ └────────────────┘
```

---

## 📍 Step 1: Prepare Your GitHub Repository

### 1.1 Push your project to GitHub

```bash
# Navigate to your project directory
cd C:\Users\vikas\IdeaProjects\EmployeePayrollSystem

# Initialize git (if not already done)
git init

# Add the remote repository
git remote add origin https://github.com/YOUR_USERNAME/EmployeePayrollSystem.git

# Add all files
git add .

# Commit
git commit -m "Initial commit: Employee Payroll System ready for deployment"

# Push to GitHub
git branch -M main
git push -u origin main
```

### 1.2 Verify the necessary files are in your repo

Make sure these files exist in the root of your repository:
- ✅ `Dockerfile` (already provided)
- ✅ `pom.xml` (already provided)
- ✅ `src/` folder with the entire source code
- ✅ `.gitignore` (already provided - keeps secrets safe)
- ✅ `render.yaml` (created for you)

---

## 🔧 Step 2: Set Up Render Services

### 2.1 Create a PostgreSQL Database

1. Go to **https://render.com/dashboard**
2. Click **"New +"** → **"PostgreSQL"**
3. Fill in the details:
   - **Name:** `payrolldb` (or anything you prefer)
   - **Database:** `payrolldb`
   - **User:** `payroll_user` (or anything you prefer)
   - **Region:** Choose the region closest to you
   - **PostgreSQL Version:** 16
   - **Plan:** Free (for testing) or Starter+ for production

4. Click **"Create Database"**

5. **Copy the connection details** (shown in the database dashboard):
   - **External Database URL** — looks like: `postgresql://user:password@hostname:5432/payrolldb`
   - **Port:** `5432`
   - **Database:** `payrolldb`
   - **Username:** (shown in dashboard)
   - **Password:** (shown in dashboard)

> ⚠️ These values will be used in Step 3. **Don't share these with anyone!**

---

### 2.2 Create a Redis Instance (Optional but Recommended)

1. Go to **https://render.com/dashboard**
2. Click **"New +"** → **"Redis"**
3. Fill in the details:
   - **Name:** `payroll-redis`
   - **Region:** Same region as PostgreSQL
   - **Plan:** Free (for testing)

4. Click **"Create Redis"**

5. **Copy the connection details:**
   - **Server:** `redis-xxxx.render.com` (hostname)
   - **Port:** `6379` (default)

---

## 🚀 Step 3: Create a Web Service on Render

### 3.1 Create the Web Service

1. Go to **https://render.com/dashboard**
2. Click **"New +"** → **"Web Service"**
3. Select **"Build and deploy from a Git repository"**

### 3.2 Connect Your GitHub Repository

1. Click **"Connect account"** (GitHub)
2. Authorize Render to access your GitHub repositories
3. Search for and select **`EmployeePayrollSystem`**
4. Click **"Connect"**

### 3.3 Configure the Service

Fill in these fields:

| Field | Value |
|---|---|
| **Name** | `payroll-app` |
| **Environment** | `Docker` |
| **Region** | Same as your database |
| **Branch** | `main` |
| **Build Command** | `docker build -t app .` |
| **Start Command** | `java -jar app.jar` |

> **Note:** The Dockerfile already handles the build and run logic!

### 3.4 Set Environment Variables

Click **"Advanced"** and add these environment variables:

#### Database Configuration
```
KEY: DB_URL
VALUE: postgresql://payroll_user:PASSWORD@hostname:5432/payrolldb
```
> Replace with your actual PostgreSQL connection string from Step 2.1

```
KEY: DB_USERNAME
VALUE: payroll_user
```

```
KEY: DB_PASSWORD
VALUE: <your-actual-postgres-password>
```

#### Redis Configuration
```
KEY: REDIS_HOST
VALUE: redis-xxxx.render.com
```
> Replace with your actual Redis hostname from Step 2.2

```
KEY: REDIS_PORT
VALUE: 6379
```

#### API Keys & Secrets
```
KEY: GROQ_API_KEY
VALUE: gsk_your_actual_groq_api_key_here
```
> Get this from https://console.groq.com

```
KEY: JWT_SECRET
VALUE: ThisIsASecretKeyThatMustBe32CharsLong!!ChangeMe123!
```
> **IMPORTANT!** Use a long, random string (minimum 32 characters)

### 3.5 Review and Deploy

1. Scroll to the bottom
2. Click **"Create Web Service"**
3. **Wait for the build** (typically 5-10 minutes)
4. Once deployed, you'll see a green ✅ status
5. Your app URL will be displayed: `https://payroll-app-xxxx.onrender.com`

---

## 🌐 Step 4: Verify Your Deployment

### 4.1 Test Health Check

Open this URL in your browser:
```
https://payroll-app-xxxx.onrender.com/actuator/health
```

You should see:
```json
{
  "status": "UP"
}
```

### 4.2 Test Swagger UI

Open this URL in your browser:
```
https://payroll-app-xxxx.onrender.com/swagger-ui.html
```

You should see the interactive Swagger UI with all API endpoints.

### 4.3 Test Login Endpoint

1. Click **"Authorize"** (🔒 button)
2. Use default credentials:
   - **Username:** `admin`
   - **Password:** `admin123`
3. Click **"Try it out"** on `POST /api/auth/login`
4. Click **"Execute"**
5. You should see a JWT token in the response ✅

---

## 📝 Step 5: Update Your GitHub README

Add a "Deployment" or "Live Demo" section to your `README.md`:

```markdown
## 🌐 Live Demo

The application is deployed and live on Render:

🔗 **[Employee Payroll System - Live](https://payroll-app-xxxx.onrender.com/swagger-ui.html)**

> Replace `payroll-app-xxxx.onrender.com` with your actual Render URL

### Quick Access Links
- **Swagger UI:** https://payroll-app-xxxx.onrender.com/swagger-ui.html
- **Health Check:** https://payroll-app-xxxx.onrender.com/actuator/health
- **API Docs:** https://payroll-app-xxxx.onrender.com/v3/api-docs

### Default Credentials for Testing
- **Username:** `admin`
- **Password:** `admin123`

> ⚠️ Change these credentials before sharing the URL publicly!
```

---

## 🔄 Continuous Deployment

Great news! Every time you push to GitHub, Render **automatically redeploys** your application:

```bash
# Make a change to your code
git add .
git commit -m "Fix: updated employee validation"
git push origin main

# Render will automatically:
# 1. Detect the push
# 2. Trigger a build
# 3. Run tests (if configured)
# 4. Deploy the new version
# 5. Keep your URL the same
```

Monitor deployments in the Render dashboard → Your service → **"Deployments"** tab.

---

## 🛠️ Troubleshooting

### ❌ Service won't start / Shows "Build failed"

1. Check the logs:
   - Go to your service dashboard
   - Click **"Logs"** tab
   - Look for error messages

2. Common issues:
   - **Maven build failed:** Check `pom.xml` dependencies
   - **Docker build failed:** Verify `Dockerfile` is in the root
   - **Java version mismatch:** Ensure Dockerfile uses `eclipse-temurin:21-jre-alpine`

### ❌ Database connection error

1. Verify PostgreSQL service is running (green ✅ status)
2. Check `DB_URL` environment variable format
3. Ensure `DB_USERNAME` and `DB_PASSWORD` match your PostgreSQL service
4. Check timezone settings in Render dashboard

### ❌ Redis connection error (optional service)

1. If Redis service is optional, you can temporarily disable it:
   - Set `spring.cache.type=simple` in `application.properties`
2. Or verify Redis service is running and connection string is correct

### ❌ 502 Bad Gateway / 500 Internal Server Error

1. Wait 1-2 minutes for the service to fully start
2. Check application logs in Render dashboard
3. Verify all environment variables are set correctly
4. Restart the service from the dashboard

---

## 💰 Pricing & Limits (Free Tier)

| Service | Free Tier | Limit |
|---|---|---|
| **Web Service** | ✅ Included | 750 hours/month |
| **PostgreSQL** | ✅ Included | 90 days after creation (renews on deploy) |
| **Redis** | ✅ Included | 90 days after creation (renews on deploy) |
| **Sleep** | ⚠️ Spins down | Auto-sleeps after 15 min inactivity |

> **Tip:** Services wake up instantly on first request (cold start). For production, upgrade to Starter+ ($7/month).

---

## 🔐 Security Best Practices

1. **Never commit secrets** — Use environment variables only
2. **Change default credentials** before going public:
   - Update `admin` and `hr` passwords in `DataSeeder.java`
   - Redeploy

3. **Rotate JWT_SECRET** regularly
4. **Use HTTPS/TLS** — Render provides HTTPS automatically ✅
5. **Monitor logs** regularly for suspicious activity
6. **Keep dependencies updated** — Check for CVEs in `pom.xml`

---

## 📱 Resume Integration

Now you can add this to your resume:

```
🚀 Employee Payroll System Deployment
• Deployed production-ready Java/Spring Boot REST API to Render
• Live URL: https://payroll-app-xxxx.onrender.com/swagger-ui.html
• Features: JWT Auth, PostgreSQL, Redis Caching, AI Onboarding (Groq API)
• CI/CD: Auto-deploys on GitHub push
• Tech: Java 21, Spring Boot 3.3.4, Docker, PostgreSQL, Redis
```

---

## 📚 Additional Resources

- **Render Documentation:** https://render.com/docs
- **Spring Boot Deployment:** https://spring.io/guides/gs/deploying-spring-boot-app-to-cloud/
- **Docker Best Practices:** https://docs.docker.com/develop/dev-best-practices/
- **JWT Security:** https://tools.ietf.org/html/rfc7519

---

## ✅ Deployment Checklist

- [ ] GitHub repository created and code pushed
- [ ] PostgreSQL service created on Render
- [ ] Redis service created on Render
- [ ] Web service created on Render
- [ ] All environment variables configured
- [ ] Health check endpoint responds ✅
- [ ] Swagger UI loads successfully
- [ ] Login endpoint works with default credentials
- [ ] README updated with live URL
- [ ] Default credentials changed to new secure passwords
- [ ] Live URL added to resume/portfolio

---

## 🎉 Success!

Your Employee Payroll System is now live and accessible to the world!

**Share your URL:**
```
https://your-payroll-app-xxxx.onrender.com/swagger-ui.html
```

### Next Steps
1. Add the URL to your GitHub repo About section
2. Include it in your resume under Projects
3. Share on LinkedIn with a demo GIF or screenshots
4. Consider adding a frontend (React/Angular) that consumes your API

---

## 👥 Need Help?

- **GitHub Issues:** Open an issue in your repository
- **Render Support:** https://render.com/support
- **Stack Overflow:** Tag your questions with `[spring-boot]` and `[render]`

Happy deploying! 🚀

