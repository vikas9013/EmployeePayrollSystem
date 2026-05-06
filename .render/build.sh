#!/bin/bash
# Build script for Render deployment
# This script builds the Java application and prepares it for Docker deployment

set -e  # Exit on any error

echo "=========================================="
echo "🏗️  Building Employee Payroll System"
echo "=========================================="

# Check Java version
echo "✓ Checking Java version..."
java -version

# Check Maven
echo "✓ Checking Maven..."
mvn --version

# Clean previous builds
echo "✓ Cleaning previous builds..."
mvn clean

# Build JAR with Maven
echo "✓ Building JAR file..."
mvn package -DskipTests

# Verify JAR was created
if [ -f "target/EmployeePayrollSystem-1.0-SNAPSHOT.jar" ]; then
    echo "✅ Build successful! JAR created at:"
    echo "   target/EmployeePayrollSystem-1.0-SNAPSHOT.jar"
else
    echo "❌ Build failed - JAR not found!"
    exit 1
fi

echo "=========================================="
echo "✅ Ready for Docker deployment on Render!"
echo "=========================================="

