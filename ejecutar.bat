@echo off
echo Ejecutando Sistema Experto...

if exist "dist/Sistema_Experto.jar" (
    java -jar dist/Sistema_Experto.jar
) else (
    echo No se encontro el archivo JAR.
    echo Ejecute primero compilar.bat
)

pause