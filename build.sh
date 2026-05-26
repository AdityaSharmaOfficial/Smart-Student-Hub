#!/bin/bash
# ============================================================
# Smart Student Hub - Build & Run Script
# ============================================================
# Prerequisites:
#   - Java 17+ installed
#   - MySQL 8.x running
#   - mysql-connector-java-8.x.jar in lib/
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src/main/java"
OUT_DIR="$PROJECT_DIR/out"
LIB_DIR="$PROJECT_DIR/lib"
JAR_NAME="SmartStudentHub.jar"

echo "=============================="
echo " Smart Student Hub Build Tool"
echo "=============================="

# Check for MySQL connector
if [ ! -f "$LIB_DIR/mysql-connector-java.jar" ]; then
    echo "ERROR: MySQL JDBC driver not found at $LIB_DIR/mysql-connector-java.jar"
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    echo "Place the JAR as: $LIB_DIR/mysql-connector-java.jar"
    exit 1
fi

# Step 1: Compile
echo ""
echo "[1/3] Compiling Java sources..."
mkdir -p "$OUT_DIR"
find "$SRC_DIR" -name "*.java" > /tmp/sources.txt
javac -cp "$LIB_DIR/mysql-connector-java.jar" \
      -d "$OUT_DIR" \
      -encoding UTF-8 \
      @/tmp/sources.txt

echo "      Compilation successful!"

# Step 2: Package JAR
echo ""
echo "[2/3] Creating executable JAR..."
echo "Main-Class: com.smartstudent.Main" > /tmp/manifest.mf
echo "Class-Path: lib/mysql-connector-java.jar" >> /tmp/manifest.mf
jar cfm "$PROJECT_DIR/$JAR_NAME" /tmp/manifest.mf -C "$OUT_DIR" .
echo "      JAR created: $JAR_NAME"

# Step 3: Run
echo ""
echo "[3/3] Launching Smart Student Hub..."
echo ""
cd "$PROJECT_DIR"
java -jar "$JAR_NAME" -cp "$LIB_DIR/mysql-connector-java.jar"
