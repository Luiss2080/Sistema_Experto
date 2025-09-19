# SISTEMA EXPERTO UNIVERSAL - DOCUMENTACIÓN COMPLETA

## DESCRIPCIÓN
Sistema experto mejorado que permite crear, gestionar y consultar bases de conocimiento mediante algoritmos de inferencia avanzados.

## CARACTERÍSTICAS IMPLEMENTADAS

### 1. MÓDULO ADMINISTRADOR
✅ **Gestión de Bases de Conocimiento**
- Crear nuevas bases de conocimiento
- Editar bases existentes
- Validar consistencia de reglas
- Importar/exportar bases

✅ **Gestión de Premisas**
- Agregar premisas con diferentes tipos de datos (booleana, numérica, string)
- Definir valores por defecto
- Validación automática de tipos
- Interfaz intuitiva con formularios completos

✅ **Gestión de Objetivos**
- Definir objetivos del sistema
- Configurar tipos de respuesta
- Establecer criterios de éxito
- Explicaciones detalladas

✅ **Gestión de Reglas**
- Editor completo de reglas IF-THEN
- Configuración de factores de certeza
- Establecer prioridades
- Validación de sintaxis
- Ayuda contextual

### 2. MÓDULO CLIENTE
✅ **Interfaz de Consulta Mejorada**
- Selección de base de conocimiento
- Formulario dinámico según premisas
- Múltiples algoritmos de inferencia
- Resultados y explicaciones detalladas

✅ **Algoritmos de Inferencia**
- Forward Chaining (encadenamiento hacia adelante)
- Backward Chaining (encadenamiento hacia atrás)
- Motor Avanzado con múltiples estrategias

✅ **Sistema de Explicaciones**
- Razonamiento paso a paso
- Justificación de conclusiones
- Traza completa del proceso
- Factores de certeza

### 3. MOTOR DE INFERENCIA AVANZADO
✅ **Estrategias Implementadas**
- Amplitud primero
- Profundidad primero
- Por prioridad de reglas
- Por factor de certeza

✅ **Características Avanzadas**
- Evaluación automática de condiciones
- Manejo de incertidumbre
- Explicaciones detalladas
- Validación de consistencia

### 4. PERSISTENCIA DE DATOS
✅ **Almacenamiento**
- DataStore centralizado
- Persistencia en memoria
- Exportación/importación
- Respaldos automáticos

✅ **Gestión de Sesiones**
- Historial completo
- Seguimiento de consultas
- Estadísticas de uso

## CÓMO USAR EL SISTEMA

### PARA ADMINISTRADORES:

1. **Inicio de Sesión**
   - Usuario: `admin`
   - Contraseña: `admin123`

2. **Crear Base de Conocimiento**
   - Ir a "Gestión de Bases de Conocimiento"
   - Hacer clic en "Nueva Base"
   - Llenar nombre y descripción
   - Guardar la base

3. **Agregar Premisas**
   - Desde el editor de base, hacer clic en "Gestionar Premisas"
   - Agregar cada premisa con su tipo de dato
   - Definir valores por defecto si es necesario

4. **Definir Objetivos**
   - Hacer clic en "Gestionar Objetivos"
   - Agregar objetivos que el sistema debe alcanzar
   - Definir tipos de respuesta y explicaciones

5. **Crear Reglas**
   - Hacer clic en "Gestionar Reglas"
   - Escribir condiciones (IF) usando la sintaxis:
     - `premisa = valor` para igualdad
     - `premisa > valor` para mayor que
     - `premisa < valor` para menor que
   - Escribir acciones (THEN) usando:
     - `resultado = valor`
   - Configurar factor de certeza y prioridad

### PARA CLIENTES:

1. **Inicio de Sesión**
   - Usuario: `cliente`
   - Contraseña: `cliente123`

2. **Realizar Consulta**
   - Hacer clic en "Consultar Sistema Experto"
   - Seleccionar una base de conocimiento
   - Elegir algoritmo de inferencia
   - Completar el formulario con los datos
   - Hacer clic en "Ejecutar Inferencia"

3. **Interpretar Resultados**
   - Ver conclusiones en el panel derecho
   - Revisar la explicación del razonamiento
   - Consultar el historial si es necesario

## EJEMPLOS DE USO

### Base de Conocimiento: Diagnóstico Médico

**Premisas:**
- `fiebre` (booleana): ¿Tiene fiebre?
- `temperatura` (numérica): Temperatura corporal
- `dolor_cabeza` (booleana): ¿Tiene dolor de cabeza?

**Reglas:**
1. **Diagnóstico Gripe**
   - IF: `fiebre = true` AND `temperatura > 38`
   - THEN: `gripe = true`
   - Factor certeza: 0.85

2. **Diagnóstico Migraña**
   - IF: `dolor_cabeza = true` AND `fiebre = false`
   - THEN: `migrana = true`
   - Factor certeza: 0.75

**Ejemplo de Consulta:**
- Datos: `fiebre = true`, `temperatura = 39`, `dolor_cabeza = false`
- Resultado: `gripe = true (certeza: 0.85)`
- Explicación: "La regla 'Diagnóstico Gripe' se aplicó porque se cumplieron las condiciones..."

### Base de Conocimiento: Problemas Informáticos

**Premisas:**
- `computadora_enciende` (booleana): ¿La computadora enciende?
- `pantalla_funciona` (booleana): ¿La pantalla muestra imagen?
- `ruidos_extraños` (booleana): ¿Hace ruidos extraños?

**Reglas:**
1. **Problema Fuente**
   - IF: `computadora_enciende = false`
   - THEN: `problema_fuente = true`

2. **Problema Pantalla**
   - IF: `computadora_enciende = true` AND `pantalla_funciona = false`
   - THEN: `problema_pantalla = true`

## ARQUITECTURA DEL SISTEMA

```
Sistema_Experto/
├── src/
│   ├── modelo/
│   │   ├── entidades/          # Clases de dominio
│   │   └── motor_inferencia/   # Algoritmos de inferencia
│   ├── persistencia/
│   │   ├── dao/               # Acceso a datos
│   │   └── archivos/          # Manejo de archivos
│   └── vista/
│       ├── administrador/     # Interfaces admin
│       ├── cliente/          # Interfaces cliente
│       ├── comun/            # Componentes compartidos
│       ├── formularios/      # Formularios especializados
│       └── login/            # Sistema de login
└── build/classes/            # Clases compiladas
```

## MEJORAS IMPLEMENTADAS

1. **Interfaces Completas**: Formularios funcionales para todos los tipos de datos
2. **Motor de Inferencia Robusto**: Múltiples algoritmos con explicaciones detalladas
3. **Validación Avanzada**: Verificación de sintaxis y consistencia
4. **Experiencia de Usuario**: Interfaces intuitivas con ayuda contextual
5. **Persistencia Mejorada**: Almacenamiento confiable y respaldos
6. **Sistema de Explicaciones**: Razonamiento transparente y comprensible

## SOLUCIÓN DE PROBLEMAS

### Problema: "No se obtuvieron conclusiones"
**Soluciones:**
- Verificar que los datos ingresados cumplan las condiciones de las reglas
- Revisar la sintaxis de las reglas
- Asegurar que hay reglas aplicables a los datos proporcionados

### Problema: "Error de sintaxis en reglas"
**Soluciones:**
- Usar sintaxis correcta: `premisa = valor`, `premisa > número`, `premisa < número`
- Separar múltiples condiciones en líneas diferentes
- Verificar que las premisas existan en la base de conocimiento

### Problema: "Base de conocimiento vacía"
**Soluciones:**
- Agregar al menos una premisa, un objetivo y una regla
- Verificar que la base esté guardada correctamente
- Contactar al administrador si es cliente

## CREDENCIALES DE ACCESO

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin123`
- Permisos: Gestión completa del sistema

**Cliente:**
- Usuario: `cliente`
- Contraseña: `cliente123`
- Permisos: Solo consultas al sistema

## NOTAS TÉCNICAS

- El sistema utiliza Java Swing para la interfaz gráfica
- La persistencia se maneja en memoria con capacidad de exportación
- Los algoritmos de inferencia son completamente funcionales
- El sistema incluye datos de ejemplo para pruebas inmediatas

## CONCLUSIÓN

El sistema experto ha sido completamente implementado y es totalmente funcional. Permite:

1. **Administradores**: Crear y gestionar bases de conocimiento completas
2. **Clientes**: Realizar consultas y obtener conclusiones con explicaciones detalladas
3. **Sistema**: Ejecutar inferencias robustas con múltiples algoritmos

El sistema está listo para uso en producción y puede adaptarse a diferentes dominios de conocimiento según las necesidades específicas.