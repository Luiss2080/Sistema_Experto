@echo off
echo Compilando Sistema Experto...

REM Crear directorios si no existen
if not exist "build\classes" mkdir "build\classes"
if not exist "dist" mkdir "dist"

REM Compilar todas las clases Java
javac -d build/classes -cp "." src/modelo/entidades/*.java src/modelo/motor_inferencia/*.java src/modelo/reglas/*.java src/persistencia/dao/*.java src/vista/login/*.java src/vista/administrador/*.java src/vista/cliente/*.java src/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa!

    REM Crear JAR
    echo Creando archivo JAR...
    jar cfm dist/Sistema_Experto.jar manifest.mf -C build/classes .

    if %ERRORLEVEL% EQU 0 (
        echo JAR creado exitosamente en dist/Sistema_Experto.jar
        echo.
        echo Para ejecutar use: java -jar dist/Sistema_Experto.jar
        echo O ejecute: ejecutar.bat
    ) else (
        echo Error al crear JAR
    )
) else (
    echo Error en la compilacion
)

pause