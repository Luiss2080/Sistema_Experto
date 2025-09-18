# Sistema Experto Universal en Java

Sistema experto desarrollado en Java con interfaz gráfica Swing que permite crear y ejecutar motores de inferencia para diferentes dominios del conocimiento.

## Características

- **Arquitectura MVC**: Separación clara entre modelo, persistencia y vista
- **Motores de Inferencia**: Forward Chaining y Backward Chaining
- **Tipos de Usuario**: Administrador y Cliente
- **Gestión Completa**: Bases de conocimiento, premisas, objetivos y reglas
- **Interfaz Simple**: Diseño básico y funcional sin elementos innecesarios

## Estructura del Proyecto

```
src/
├── modelo/
│   ├── entidades/          # Clases de dominio
│   ├── motor_inferencia/   # Algoritmos de inferencia
│   ├── reglas/            # Reglas de inferencia lógica
│   └── utilidades/        # Utilidades generales
├── persistencia/
│   ├── dao/               # Acceso a datos
│   ├── conexion/          # Gestión de conexiones
│   └── archivos/          # Manejo de archivos
└── vista/
    ├── administrador/     # Interfaces de administrador
    ├── cliente/           # Interfaces de cliente
    ├── comun/             # Componentes compartidos
    └── login/             # Sistema de autenticación
```

## Requisitos

- Java 11 o superior
- NetBeans IDE (recomendado)

## Instalación y Ejecución

### Desde NetBeans IDE (Recomendado):

1. **Abrir NetBeans IDE** (versión 12.0 o superior)
2. **File** → **Open Project**
3. **Navegar** a la carpeta `Sistema_Experto` y seleccionarla
4. **NetBeans detectará** automáticamente el proyecto Java Application
5. **Click derecho** en el proyecto → **Clean and Build**
6. **Click derecho** en el proyecto → **Run**
7. El sistema iniciará con la ventana de login

**Nota:** Los formularios principales (LoginPrincipal) pueden editarse con el GUI Builder de NetBeans. Para el resto, puedes modificar el código directamente o usar el GUI Builder para crear nuevos formularios.

### Desde línea de comandos:

#### Opción 1: Usando los scripts (Windows):
```cmd
# Compilar el proyecto
compilar.bat

# Ejecutar el sistema
ejecutar.bat
```

#### Opción 2: Comandos manuales:
```bash
# Crear directorios
mkdir build\classes
mkdir dist

# Compilar todas las clases
javac -d build/classes -cp "." src/modelo/entidades/*.java src/modelo/motor_inferencia/*.java src/modelo/reglas/*.java src/persistencia/dao/*.java src/vista/login/*.java src/vista/administrador/*.java src/vista/cliente/*.java src/*.java

# Crear JAR
jar cfm dist/Sistema_Experto.jar manifest.mf -C build/classes .

# Ejecutar
java -jar dist/Sistema_Experto.jar
```

### Estructura del Proyecto NetBeans:

```
Sistema_Experto/
├── nbproject/           # Configuración NetBeans
│   ├── project.xml      # Configuración del proyecto
│   ├── project.properties
│   ├── build-impl.xml   # Script de construcción
│   ├── genfiles.properties
│   └── private/         # Configuración local
├── src/                 # Código fuente
│   ├── modelo/
│   ├── persistencia/
│   ├── vista/
│   └── SistemaExperto.java
├── build/               # Archivos compilados
├── dist/                # JAR final
├── build.xml            # Script Ant
├── manifest.mf          # Manifiesto JAR
├── compilar.bat         # Script compilación (Windows)
├── ejecutar.bat         # Script ejecución (Windows)
└── README.md
```

## Usuarios por Defecto

El sistema incluye usuarios predefinidos para pruebas:

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin123`

**Cliente:**
- Usuario: `cliente`
- Contraseña: `cliente123`

## Funcionalidades Principales

### Para Administradores:

1. **Gestión de Bases de Conocimiento**
   - Crear nuevas bases
   - Editar existentes
   - Eliminar bases

2. **Gestión de Premisas** - ¡NUEVO FORMULARIO!
   - Formulario intuitivo con validación
   - Definir premisas (booleanas, numéricas, texto)
   - Establecer valores por defecto
   - Validación automática de tipos
   - Botones de acción (Guardar, Limpiar, Cancelar)

3. **Gestión de Objetivos** - ¡NUEVO FORMULARIO!
   - Formulario con ejemplos integrados
   - Definir objetivos del sistema
   - Tipos de respuesta predefinidos
   - Criterios de evaluación
   - Explicaciones detalladas

4. **Gestión de Reglas** - ¡NUEVO FORMULARIO!
   - Editor avanzado de reglas IF-THEN
   - Validación en tiempo real
   - Factor de certeza con slider
   - Sistema de prioridades
   - Ayuda contextual con ejemplos
   - Premisas disponibles listadas

5. **Gestión de Hechos** - ¡NUEVO FORMULARIO!
   - Asociación automática con premisas
   - Validación de valores según tipo
   - Control de certeza deslizable
   - Fuentes de información configurables

6. **Sistema de Archivos** - ¡NUEVA FUNCIONALIDAD!
   - **Exportar Bases de Conocimiento**: Guardar bases completas en archivos .sexp
   - **Importar Bases de Conocimiento**: Cargar bases desde archivos
   - **Incluir Sesiones**: Opción de exportar/importar historial de consultas
   - **Menú Archivo**: Acceso directo desde la barra de menú
   - **Validación de Archivos**: Verificación de integridad y compatibilidad
   - **Resolución de Conflictos**: Manejo automático de nombres duplicados

7. **Sistema de Actualización Mejorado** - ¡NUEVA FUNCIONALIDAD!
   - **Botones de Actualizar**: En todas las interfaces principales
   - **Actualización Automática**: Tras crear/editar bases de conocimiento
   - **Selección Inteligente**: Mantiene la selección después de operaciones
   - **Sincronización**: Actualiza automáticamente listas relacionadas
   - **Notificaciones**: Mensajes de confirmación tras operaciones exitosas
   - **Información del Sistema**: Estadísticas completas y estado actual

### Para Clientes:

1. **Consulta Avanzada al Sistema Experto** - ¡TOTALMENTE RENOVADO!
   - **Selector de Algoritmos Avanzado**: Interfaz visual para elegir entre Forward Chaining, Backward Chaining o Motor Avanzado
   - **Estrategias Múltiples**: Amplitud, Profundidad, Prioridad o Factor de Certeza
   - **Entrada de Datos Dinámica**: Formulario inteligente con validación en tiempo real
   - **Progreso Visual**: Barra de progreso que muestra completitud de datos
   - **Navegación Intuitiva**: Botones Anterior/Siguiente, validación individual
   - **Valores por Defecto**: Usar automáticamente valores predefinidos

2. **Resultados con Explicaciones Detalladas** - ¡NUEVO!
   - **4 Pestañas de Análisis**: Resumen, Explicación, Pasos, Gráfico
   - **Explicaciones Paso a Paso**: Cada regla aplicada con justificación
   - **Árbol de Inferencia**: Visualización jerárquica del proceso
   - **Factores de Certeza**: Análisis de confianza en las conclusiones
   - **Búsqueda en Explicaciones**: Encontrar términos específicos
   - **Exportación de Resultados**: Guardar análisis completos

3. **Motor de Inferencia Avanzado** - ¡NUEVO!
   - **Múltiples Estrategias**: Cada una optimizada para diferentes casos
   - **Explicaciones Inteligentes**: Justificación detallada de cada paso
   - **Manejo de Certeza**: Cálculos avanzados de probabilidad
   - **Optimización Automática**: Selección de la mejor ruta de inferencia

4. **Historial de Consultas Mejorado**
   - Ver sesiones anteriores con detalles del algoritmo usado
   - Revisar configuraciones completas (algoritmo + estrategia)
   - Comparar resultados entre diferentes enfoques
   - Eliminar sesiones

5. **Importación de Bases de Conocimiento** - ¡NUEVA FUNCIONALIDAD!
   - **Menú Archivo**: Acceso directo para importar bases
   - **Carga Automática**: Integración completa al sistema
   - **Preservación de Datos**: Mantiene premisas, objetivos, reglas y sesiones
   - **Guía de Usuario**: Ayuda integrada en el menú

## Algoritmos de Inferencia

### Forward Chaining (Encadenamiento hacia adelante)
- Parte de los hechos conocidos
- Aplica reglas para inferir nuevos hechos
- Continúa hasta no poder inferir más

### Backward Chaining (Encadenamiento hacia atrás)
- Parte de los objetivos a demostrar
- Busca hechos y reglas que los sustenten
- Trabaja hacia atrás hasta los hechos conocidos

## Reglas de Inferencia Implementadas

- **Modus Ponens**: Si P→Q y P, entonces Q
- **Modus Tollens**: Si P→Q y ¬Q, entonces ¬P
- **Silogismo Hipotético**: Si P→Q y Q→R, entonces P→R
- **Resolución**: Método para lógica proposicional

## Ejemplo de Uso

1. **Login como administrador**
2. **Crear una nueva base de conocimiento**
   - Nombre: "Diagnóstico Médico"
   - Descripción: "Sistema para diagnóstico básico"

3. **Agregar premisas:**
   - "fiebre" (booleana)
   - "dolor_cabeza" (booleana)
   - "temperatura" (numérica)

4. **Agregar objetivos:**
   - "gripe"
   - "migrana"

5. **Agregar reglas:**
   - IF fiebre=true AND temperatura>38 THEN gripe=posible
   - IF dolor_cabeza=true AND fiebre=false THEN migrana=posible

6. **Login como cliente y consultar**
   - Seleccionar la base creada
   - Completar datos (fiebre=true, temperatura=39)
   - Ejecutar inferencia
   - Ver resultado: "gripe=posible"

7. **Exportar/Importar la base**
   - Como administrador: Archivo → Gestión de Archivos
   - Seleccionar la base "Diagnóstico Médico"
   - Exportar con sesiones incluidas
   - El archivo se guarda como "Diagnostico_Medico.sexp"
   - Otro usuario puede importar y usar la base completa

8. **Flujo de trabajo mejorado**
   - **Crear nueva base**: Al guardar, automáticamente se selecciona
   - **Botón Actualizar**: Siempre disponible para refrescar listas
   - **Notificaciones**: Confirmación inmediata de operaciones
   - **Navegación fluida**: Sin perder el contexto de trabajo
   - **Información del sistema**: Herramientas → Información del Sistema

## Gestión de Archivos

### Exportación de Bases de Conocimiento

**Para Administradores:**
1. **Menú Archivo** → **Gestión de Archivos**
2. **Seleccionar base** de conocimiento a exportar
3. **Elegir opciones:**
   - ☑️ Incluir historial de sesiones (recomendado)
   - ☐ Solo estructura (premisas, objetivos, reglas)
4. **Exportar a Archivo** → Elegir ubicación y nombre
5. Se genera archivo `.sexp` con toda la información

**Contenido del archivo exportado:**
- Información de la base (nombre, descripción, fecha)
- Todas las premisas con tipos y valores por defecto
- Todos los objetivos con criterios y explicaciones
- Todas las reglas con condiciones y factores de certeza
- Historial de sesiones (si se incluye)
- Metadatos de versión y compatibilidad

### Importación de Bases de Conocimiento

**Para Administradores y Clientes:**
1. **Menú Archivo** → **Gestión de Archivos** (Admin) o **Importar Base de Conocimiento** (Cliente)
2. **Importar desde Archivo** → Seleccionar archivo `.sexp`
3. **Verificación automática** de integridad y compatibilidad
4. **Resolución de conflictos** si existe base con mismo nombre
5. **Integración completa** al sistema local

**Beneficios:**
- **Compartir conocimiento** entre usuarios y sistemas
- **Backup y restauración** de bases de conocimiento
- **Distribución** de bases expertas especializadas
- **Colaboración** entre administradores
- **Portabilidad** entre instalaciones del sistema

### Formato de Archivo .sexp

Los archivos del Sistema Experto (`.sexp`) utilizan:
- **Serialización Java** para máxima compatibilidad
- **Estructura versionada** para evolución futura
- **Validación de integridad** automática
- **Compresión eficiente** de datos
- **Metadatos incluidos** (fecha, versión, estadísticas)

## Mejoras de Usabilidad

### **Sistema de Actualización Inteligente**

**Problema resuelto:** Las listas no se actualizaban automáticamente tras crear/editar elementos, requiriendo cerrar y abrir ventanas.

**Solución implementada:**
1. **Botones de Actualizar** en todas las interfaces principales
2. **Actualización automática** tras operaciones exitosas
3. **Preservación de selección** cuando es posible
4. **Sincronización** entre ventanas padre e hijas

### **Flujo de Trabajo Optimizado**

**Para Administradores:**
- Al crear una nueva base → Se selecciona automáticamente
- Al editar una base → Se mantiene la selección
- Al eliminar → La lista se actualiza inmediatamente
- **Notificaciones claras** de éxito/error en cada operación

**Para Clientes:**
- Lista de bases siempre actualizada
- Botón "Actualizar Bases" para refrescar tras importaciones
- Acceso a información del sistema desde menú Ayuda

### **Información del Sistema**

**Nueva funcionalidad:** Herramientas → Información del Sistema

**Incluye:**
- Estadísticas de usuario y sesiones
- Resumen de todas las bases de conocimiento
- Últimas consultas realizadas
- Información técnica del sistema
- Uso de memoria y rendimiento

### **Notificaciones Mejoradas**

- **Mensajes específicos** para cada tipo de operación
- **Confirmaciones** de exportación/importación exitosa
- **Guías contextuales** sobre próximos pasos
- **Información de estado** en títulos de ventana

## Personalización

El sistema está diseñado para ser extensible:

- Agregar nuevos tipos de premisas
- Implementar nuevos algoritmos de inferencia
- Extender reglas de inferencia
- Personalizar interfaces gráficas

## Soporte

Sistema desarrollado para fines educativos y de demostración. La interfaz es intencionalmente básica y funcional, siguiendo los requerimientos de simplicidad especificados.