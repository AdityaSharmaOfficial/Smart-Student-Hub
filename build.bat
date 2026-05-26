@echo off
REM ============================================================
REM Smart Student Hub - Windows Build & Run Script
REM ============================================================
REM Prerequisites:
REM   - Java 17+ installed and in PATH
REM   - MySQL 8.x running locally
REM   - mysql-connector-java.jar placed in .\lib\
REM ============================================================

setlocal

set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src\main\java
set OUT_DIR=%PROJECT_DIR%out
set LIB_DIR=%PROJECT_DIR%lib
set JAR_NAME=SmartStudentHub.jar
set CONNECTOR=%LIB_DIR%\mysql-connector-java.jar

echo ==============================
echo  Smart Student Hub Build Tool
echo ==============================

if not exist "%CONNECTOR%" (
    echo ERROR: MySQL connector not found at %CONNECTOR%
    echo Download: https://dev.mysql.com/downloads/connector/j/
    echo Place JAR at: %CONNECTOR%
    pause
    exit /b 1
)

echo.
echo [1/3] Compiling sources...
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
dir /s /b "%SRC_DIR%\*.java" > "%TEMP%\sources.txt"
javac -cp "%CONNECTOR%" -d "%OUT_DIR%" -encoding UTF-8 @"%TEMP%\sources.txt"
if errorlevel 1 (
    echo Compilation FAILED.
    pause
    exit /b 1
)
echo       Compilation successful!

echo.
echo [2/3] Creating JAR...
(
    echo Main-Class: com.smartstudent.Main
    echo Class-Path: lib/mysql-connector-java.jar
) > "%TEMP%\manifest.mf"
jar cfm "%PROJECT_DIR%%JAR_NAME%" "%TEMP%\manifest.mf" -C "%OUT_DIR%" .
echo       JAR created: %JAR_NAME%

echo.
echo [3/3] Launching Smart Student Hub...
echo.
cd /d "%PROJECT_DIR%"
java -cp "%JAR_NAME%;%CONNECTOR%" com.smartstudent.Main

pause
