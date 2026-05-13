@echo off
title Obsidian Build System
echo.
echo  ============================================
echo   Obsidian Server - Build System
echo   Performance ^& Security focused Paper Fork
echo  ============================================
echo.

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo [*] Using Java: %JAVA_HOME%
echo.

if "%1"=="" goto menu
if "%1"=="build" goto build
if "%1"=="run" goto run
if "%1"=="clean" goto clean
goto menu

:menu
echo  Select an option:
echo.
echo   1) Build Obsidian (createPaperclipJar)
echo   2) Build + Run Dev Server
echo   3) Clean build
echo   4) Apply patches
echo   5) Exit
echo.
set /p choice="  Enter choice (1-5): "

if "%choice%"=="1" goto build
if "%choice%"=="2" goto buildrun
if "%choice%"=="3" goto clean
if "%choice%"=="4" goto patches
if "%choice%"=="5" exit /b

:build
echo.
echo [*] Building Obsidian...
echo.
call gradlew.bat createPaperclipJar
if %errorlevel% neq 0 (
    echo.
    echo [!] Build FAILED!
    pause
    exit /b 1
)
echo.
echo [+] Build SUCCESS!
echo [+] JAR location: paper-server\build\libs\
echo.
pause
exit /b 0

:buildrun
echo.
echo [*] Building and running Obsidian...
echo.
call gradlew.bat runDevServer
pause
exit /b 0

:clean
echo.
echo [*] Cleaning build...
echo.
call gradlew.bat clean
echo [+] Clean complete.
pause
exit /b 0

:patches
echo.
echo [*] Applying patches...
echo.
call gradlew.bat applyPatches
echo [+] Patches applied.
pause
exit /b 0
