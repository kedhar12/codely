Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "               Codely Enterprise Coding Platform" -ForegroundColor White
Write-Host "================================================================" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Add GCC and JDK to Path if present
$w64Path = "$scriptDir\..\w64devkit\bin"
if (Test-Path $w64Path) {
    $env:PATH = "$w64Path;$env:PATH"
}
$jdkPath = "C:\Users\user\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin"
if (Test-Path $jdkPath) {
    $env:PATH = "$jdkPath;$env:PATH"
}

if (!(Test-Path "bin")) { New-Item -ItemType Directory -Name "bin" | Out-Null }
if (!(Test-Path "temp")) { New-Item -ItemType Directory -Name "temp" | Out-Null }

Write-Host "`n[1/2] Compiling Java backend..." -ForegroundColor Yellow
javac -encoding UTF-8 -d bin src\server\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Java compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Starting Codely Server at http://localhost:8080...`n" -ForegroundColor Green
Start-Process "http://localhost:8080"
java -cp bin server.CodeTantraServer 8080
