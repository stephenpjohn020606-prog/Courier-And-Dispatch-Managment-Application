@echo off
cd /d "%~dp0"
echo Compiling Java source files
if not exist "compiled files" mkdir "compiled files"
javac --release 8 -cp "libraries/*" -sourcepath "java files" -d "compiled files" "java files/courier/Main.java"
if %ERRORLEVEL% equ 0 (
    echo   Compilation Successful!
) else (
    echo   Compilation Failed! Check errors above.
)
pause
