@echo off
title Codely - Multi-Language Coding & Assessment Platform
echo ================================================================
echo               Codely Enterprise Coding Platform
echo ================================================================
echo.

cd /d "%~dp0"

echo [1/3] Setting up environment paths...
set "PATH=%~dp0..\w64devkit\bin;C:\Users\user\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin;%PATH%"

echo [2/3] Compiling Java backend server...
if not exist "bin" mkdir bin
if not exist "temp" mkdir temp
javac -encoding UTF-8 -d bin src\server\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [3/3] Starting Codely Server on http://localhost:8080...
echo.
start http://localhost:8080
java -cp bin server.CodeTantraServer 8080
pause
