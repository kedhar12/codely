@echo off
title Import Codely Database into MySQL
echo ================================================================
echo           Importing Codely Relational Database to MySQL
echo ================================================================
echo.

set /p MYSQL_USER=Enter MySQL Username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS=Enter MySQL Password (press Enter if none): 

set MYSQL_CMD=mysql -u %MYSQL_USER%
if not "%MYSQL_PASS%"=="" set MYSQL_CMD=mysql -u %MYSQL_USER% -p%MYSQL_PASS%

echo.
echo Executing database/codely_schema.sql...
%MYSQL_CMD% < "%~dp0codely_schema.sql"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to import database. Please verify:
    echo 1. Is MySQL Server running?
    echo 2. Are the username and password correct?
    echo 3. Is 'mysql' in your Windows PATH?
) else (
    echo.
    echo [SUCCESS] Database 'codely_db' and tables created successfully!
    echo.
    echo You can now run:
    echo   mysql -u %MYSQL_USER% -e "USE codely_db; SHOW TABLES;"
)

pause
