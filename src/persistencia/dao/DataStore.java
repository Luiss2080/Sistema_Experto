package persistencia.dao;

import modelo.entidades.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.nio.file.*;

/**
 * Almacén central de datos para el sistema
 * Implementa persistencia en memoria compartida entre todas las instancias
 */
public class DataStore {

    // Almacenamiento estático compartido
    private static final Map<Integer, BaseConocimiento> BASES = new ConcurrentHashMap<>();
    private static final Map<Integer, Usuario> USUARIOS = new ConcurrentHashMap<>();
    private static final Map<Integer, Sesion> SESIONES = new ConcurrentHashMap<>();

    // Contadores de ID atómicos
    private static final AtomicInteger CONTADOR_BASE = new AtomicInteger(1);
    private static final AtomicInteger CONTADOR_USUARIO = new AtomicInteger(1);
    private static final AtomicInteger CONTADOR_SESION = new AtomicInteger(1);

    // Flag para inicialización
    private static volatile boolean inicializado = false;

    // Archivo de persistencia
    private static final String ARCHIVO_PERSISTENCIA = "datos_sistema.ser";

    static {
        cargarDatosPersistentes();

        // Agregar shutdown hook para guardar automáticamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Guardando datos del sistema...");
            guardarDatos();
        }));
    }

    /**
     * Inicializa datos de prueba del sistema
     */
    private static void inicializarDatos() {
        if (inicializado) return;

        synchronized (DataStore.class) {
            if (inicializado) return;

            // Crear usuarios por defecto
            Usuario admin = new Usuario("admin", "admin123", Usuario.TipoUsuario.ADMINISTRADOR);
            admin.setId(CONTADOR_USUARIO.getAndIncrement());
            USUARIOS.put(admin.getId(), admin);

            Usuario cliente = new Usuario("cliente", "cliente123", Usuario.TipoUsuario.CLIENTE);
            cliente.setId(CONTADOR_USUARIO.getAndIncrement());
            USUARIOS.put(cliente.getId(), cliente);

            // Crear base de conocimiento de ejemplo
            BaseConocimiento baseEjemplo = new BaseConocimiento(
                "Diagnóstico Médico Básico",
                "Sistema experto para diagnóstico médico básico con síntomas comunes",
                admin.getId()
            );
            baseEjemplo.setId(CONTADOR_BASE.getAndIncrement());

            // Agregar premisas de ejemplo
            Premisa fiebre = new Premisa("fiebre", "¿Tiene fiebre?", Premisa.TipoDato.BOOLEANA);
            fiebre.setId(1);
            fiebre.setValorDefecto("false");

            Premisa temperatura = new Premisa("temperatura", "Temperatura corporal", Premisa.TipoDato.NUMERICA);
            temperatura.setId(2);
            temperatura.setValorDefecto("36.5");

            Premisa dolorCabeza = new Premisa("dolor_cabeza", "¿Tiene dolor de cabeza?", Premisa.TipoDato.BOOLEANA);
            dolorCabeza.setId(3);
            dolorCabeza.setValorDefecto("false");

            baseEjemplo.getPremisas().add(fiebre);
            baseEjemplo.getPremisas().add(temperatura);
            baseEjemplo.getPremisas().add(dolorCabeza);

            // Agregar objetivos de ejemplo
            Objetivo gripe = new Objetivo("gripe", "Posible gripe", "booleana");
            gripe.setId(1);
            gripe.setExplicacion("Determina si los síntomas indican gripe");

            Objetivo migrana = new Objetivo("migrana", "Posible migraña", "booleana");
            migrana.setId(2);
            migrana.setExplicacion("Determina si los síntomas indican migraña");

            baseEjemplo.getObjetivos().add(gripe);
            baseEjemplo.getObjetivos().add(migrana);

            // Agregar reglas de ejemplo
            Regla reglaGripe = new Regla("Diagnóstico Gripe");
            reglaGripe.setId(1);
            reglaGripe.getCondiciones().add("fiebre = true");
            reglaGripe.getCondiciones().add("temperatura > 38");
            reglaGripe.getAcciones().add("gripe = true");
            reglaGripe.setFactorCerteza(0.85);

            Regla reglaMigrana = new Regla("Diagnóstico Migraña");
            reglaMigrana.setId(2);
            reglaMigrana.getCondiciones().add("dolor_cabeza = true");
            reglaMigrana.getCondiciones().add("fiebre = false");
            reglaMigrana.getAcciones().add("migrana = true");
            reglaMigrana.setFactorCerteza(0.75);

            baseEjemplo.getReglas().add(reglaGripe);
            baseEjemplo.getReglas().add(reglaMigrana);

            BASES.put(baseEjemplo.getId(), baseEjemplo);

            // Crear segunda base de ejemplo
            BaseConocimiento baseTecnologia = new BaseConocimiento(
                "Diagnóstico de Problemas Informáticos",
                "Sistema experto para diagnosticar problemas comunes en computadoras",
                admin.getId()
            );
            baseTecnologia.setId(CONTADOR_BASE.getAndIncrement());

            // Premisas para problemas informáticos
            Premisa computadoraEnciende = new Premisa("computadora_enciende", "¿La computadora enciende?", Premisa.TipoDato.BOOLEANA);
            computadoraEnciende.setId(4);
            computadoraEnciende.setValorDefecto("true");

            Premisa pantallaFunciona = new Premisa("pantalla_funciona", "¿La pantalla muestra imagen?", Premisa.TipoDato.BOOLEANA);
            pantallaFunciona.setId(5);
            pantallaFunciona.setValorDefecto("true");

            Premisa ruidos = new Premisa("ruidos_extraños", "¿Hace ruidos extraños?", Premisa.TipoDato.BOOLEANA);
            ruidos.setId(6);
            ruidos.setValorDefecto("false");

            baseTecnologia.getPremisas().add(computadoraEnciende);
            baseTecnologia.getPremisas().add(pantallaFunciona);
            baseTecnologia.getPremisas().add(ruidos);

            // Objetivos para problemas informáticos
            Objetivo problema_fuente = new Objetivo("problema_fuente", "Problema en fuente de poder", "booleana");
            problema_fuente.setId(3);
            problema_fuente.setExplicacion("Determina si el problema está en la fuente de poder");

            Objetivo problema_pantalla = new Objetivo("problema_pantalla", "Problema en pantalla/video", "booleana");
            problema_pantalla.setId(4);
            problema_pantalla.setExplicacion("Determina si el problema está en la pantalla o tarjeta de video");

            baseTecnologia.getObjetivos().add(problema_fuente);
            baseTecnologia.getObjetivos().add(problema_pantalla);

            // Reglas para problemas informáticos
            Regla reglaFuente = new Regla("Diagnóstico Fuente");
            reglaFuente.setId(3);
            reglaFuente.getCondiciones().add("computadora_enciende = false");
            reglaFuente.getAcciones().add("problema_fuente = true");
            reglaFuente.setFactorCerteza(0.90);

            Regla reglaPantalla = new Regla("Diagnóstico Pantalla");
            reglaPantalla.setId(4);
            reglaPantalla.getCondiciones().add("computadora_enciende = true");
            reglaPantalla.getCondiciones().add("pantalla_funciona = false");
            reglaPantalla.getAcciones().add("problema_pantalla = true");
            reglaPantalla.setFactorCerteza(0.80);

            baseTecnologia.getReglas().add(reglaFuente);
            baseTecnologia.getReglas().add(reglaPantalla);

            BASES.put(baseTecnologia.getId(), baseTecnologia);

            // Crear tercera base de ejemplo: Diagnóstico Automotriz
            BaseConocimiento baseAutomotriz = new BaseConocimiento(
                "Diagnóstico de Problemas Automotrices",
                "Sistema experto para diagnosticar problemas comunes en vehículos",
                admin.getId()
            );
            baseAutomotriz.setId(CONTADOR_BASE.getAndIncrement());

            // Premisas automotrices
            Premisa motorEnciende = new Premisa("motor_enciende", "¿El motor enciende?", Premisa.TipoDato.BOOLEANA);
            motorEnciende.setId(7);
            motorEnciende.setValorDefecto("true");

            Premisa ruidoMotor = new Premisa("ruido_motor", "¿Hace ruidos extraños el motor?", Premisa.TipoDato.BOOLEANA);
            ruidoMotor.setId(8);
            ruidoMotor.setValorDefecto("false");

            Premisa temperaturaMotor = new Premisa("temperatura_motor", "Temperatura del motor (°C)", Premisa.TipoDato.NUMERICA);
            temperaturaMotor.setId(9);
            temperaturaMotor.setValorDefecto("90");

            Premisa lucesFuncionan = new Premisa("luces_funcionan", "¿Las luces funcionan correctamente?", Premisa.TipoDato.BOOLEANA);
            lucesFuncionan.setId(10);
            lucesFuncionan.setValorDefecto("true");

            baseAutomotriz.getPremisas().add(motorEnciende);
            baseAutomotriz.getPremisas().add(ruidoMotor);
            baseAutomotriz.getPremisas().add(temperaturaMotor);
            baseAutomotriz.getPremisas().add(lucesFuncionan);

            // Objetivos automotrices
            Objetivo problema_motor = new Objetivo("problema_motor", "Problema en el motor", "booleana");
            problema_motor.setId(5);
            problema_motor.setExplicacion("Determina si hay problemas en el sistema del motor");

            Objetivo problema_electrico = new Objetivo("problema_electrico", "Problema eléctrico", "booleana");
            problema_electrico.setId(6);
            problema_electrico.setExplicacion("Determina si hay problemas en el sistema eléctrico");

            Objetivo sobrecalentamiento = new Objetivo("sobrecalentamiento", "Motor sobrecalentado", "booleana");
            sobrecalentamiento.setId(7);
            sobrecalentamiento.setExplicacion("Determina si el motor está sobrecalentado");

            baseAutomotriz.getObjetivos().add(problema_motor);
            baseAutomotriz.getObjetivos().add(problema_electrico);
            baseAutomotriz.getObjetivos().add(sobrecalentamiento);

            // Reglas automotrices
            Regla reglaMotor = new Regla("Diagnóstico Motor");
            reglaMotor.setId(5);
            reglaMotor.getCondiciones().add("motor_enciende = false");
            reglaMotor.getCondiciones().add("ruido_motor = true");
            reglaMotor.getAcciones().add("problema_motor = true");
            reglaMotor.setFactorCerteza(0.85);

            Regla reglaElectrico = new Regla("Diagnóstico Eléctrico");
            reglaElectrico.setId(6);
            reglaElectrico.getCondiciones().add("luces_funcionan = false");
            reglaElectrico.getCondiciones().add("motor_enciende = false");
            reglaElectrico.getAcciones().add("problema_electrico = true");
            reglaElectrico.setFactorCerteza(0.80);

            Regla reglaCalentamiento = new Regla("Diagnóstico Sobrecalentamiento");
            reglaCalentamiento.setId(7);
            reglaCalentamiento.getCondiciones().add("temperatura_motor > 100");
            reglaCalentamiento.getCondiciones().add("ruido_motor = true");
            reglaCalentamiento.getAcciones().add("sobrecalentamiento = true");
            reglaCalentamiento.setFactorCerteza(0.90);

            baseAutomotriz.getReglas().add(reglaMotor);
            baseAutomotriz.getReglas().add(reglaElectrico);
            baseAutomotriz.getReglas().add(reglaCalentamiento);

            BASES.put(baseAutomotriz.getId(), baseAutomotriz);

            // Crear cuarta base: Diagnóstico Psicológico Básico
            BaseConocimiento basePsicologia = new BaseConocimiento(
                "Evaluación de Bienestar Psicológico",
                "Sistema para evaluar indicadores básicos de bienestar mental y sugerir recomendaciones",
                admin.getId()
            );
            basePsicologia.setId(CONTADOR_BASE.getAndIncrement());

            // Premisas psicológicas
            Premisa estadoAnimo = new Premisa("estado_animo", "¿Cómo se siente generalmente? (1=Muy mal, 5=Excelente)", Premisa.TipoDato.NUMERICA);
            estadoAnimo.setId(11);
            estadoAnimo.setValorDefecto("3");

            Premisa duermeNormal = new Premisa("duerme_normal", "¿Duerme bien por las noches?", Premisa.TipoDato.BOOLEANA);
            duermeNormal.setId(12);
            duermeNormal.setValorDefecto("true");

            Premisa tieneEnergia = new Premisa("tiene_energia", "¿Se siente con energía durante el día?", Premisa.TipoDato.BOOLEANA);
            tieneEnergia.setId(13);
            tieneEnergia.setValorDefecto("true");

            Premisa relacionesSociales = new Premisa("relaciones_sociales", "¿Mantiene buenas relaciones sociales?", Premisa.TipoDato.BOOLEANA);
            relacionesSociales.setId(14);
            relacionesSociales.setValorDefecto("true");

            basePsicologia.getPremisas().add(estadoAnimo);
            basePsicologia.getPremisas().add(duermeNormal);
            basePsicologia.getPremisas().add(tieneEnergia);
            basePsicologia.getPremisas().add(relacionesSociales);

            // Objetivos psicológicos
            Objetivo bienestar_alto = new Objetivo("bienestar_alto", "Alto bienestar psicológico", "booleana");
            bienestar_alto.setId(8);
            bienestar_alto.setExplicacion("Indica un estado de bienestar psicológico favorable");

            Objetivo necesita_atencion = new Objetivo("necesita_atencion", "Necesita atención profesional", "booleana");
            necesita_atencion.setId(9);
            necesita_atencion.setExplicacion("Sugiere buscar apoyo de un profesional de la salud mental");

            Objetivo mejorar_habitos = new Objetivo("mejorar_habitos", "Mejorar hábitos de vida", "booleana");
            mejorar_habitos.setId(10);
            mejorar_habitos.setExplicacion("Recomienda mejorar rutinas de sueño y actividad");

            basePsicologia.getObjetivos().add(bienestar_alto);
            basePsicologia.getObjetivos().add(necesita_atencion);
            basePsicologia.getObjetivos().add(mejorar_habitos);

            // Reglas psicológicas
            Regla reglaBienestar = new Regla("Evaluación Bienestar Alto");
            reglaBienestar.setId(8);
            reglaBienestar.getCondiciones().add("estado_animo >= 4");
            reglaBienestar.getCondiciones().add("duerme_normal = true");
            reglaBienestar.getCondiciones().add("tiene_energia = true");
            reglaBienestar.getAcciones().add("bienestar_alto = true");
            reglaBienestar.setFactorCerteza(0.85);

            Regla reglaAtencion = new Regla("Necesita Atención");
            reglaAtencion.setId(9);
            reglaAtencion.getCondiciones().add("estado_animo <= 2");
            reglaAtencion.getCondiciones().add("duerme_normal = false");
            reglaAtencion.getCondiciones().add("relacionesSociales = false");
            reglaAtencion.getAcciones().add("necesita_atencion = true");
            reglaAtencion.setFactorCerteza(0.90);

            Regla reglaHabitos = new Regla("Mejorar Hábitos");
            reglaHabitos.setId(10);
            reglaHabitos.getCondiciones().add("duerme_normal = false");
            reglaHabitos.getCondiciones().add("tiene_energia = false");
            reglaHabitos.getAcciones().add("mejorar_habitos = true");
            reglaHabitos.setFactorCerteza(0.75);

            basePsicologia.getReglas().add(reglaBienestar);
            basePsicologia.getReglas().add(reglaAtencion);
            basePsicologia.getReglas().add(reglaHabitos);

            BASES.put(basePsicologia.getId(), basePsicologia);

            // Crear quinta base: Sistema de Recomendación de Ejercicios
            BaseConocimiento baseEjercicio = new BaseConocimiento(
                "Recomendador de Rutinas de Ejercicio",
                "Sistema que recomienda rutinas de ejercicio según el perfil y objetivos del usuario",
                admin.getId()
            );
            baseEjercicio.setId(CONTADOR_BASE.getAndIncrement());

            // Premisas de ejercicio
            Premisa edad = new Premisa("edad", "Edad en años", Premisa.TipoDato.NUMERICA);
            edad.setId(15);
            edad.setValorDefecto("25");

            Premisa nivelFisico = new Premisa("nivel_fisico", "Nivel físico actual (1=Principiante, 3=Avanzado)", Premisa.TipoDato.NUMERICA);
            nivelFisico.setId(16);
            nivelFisico.setValorDefecto("1");

            Premisa quierePeso = new Premisa("quiere_perder_peso", "¿Quiere perder peso?", Premisa.TipoDato.BOOLEANA);
            quierePeso.setId(17);
            quierePeso.setValorDefecto("false");

            Premisa tiempoDisponible = new Premisa("tiempo_disponible", "Tiempo disponible por día (minutos)", Premisa.TipoDato.NUMERICA);
            tiempoDisponible.setId(18);
            tiempoDisponible.setValorDefecto("30");

            baseEjercicio.getPremisas().add(edad);
            baseEjercicio.getPremisas().add(nivelFisico);
            baseEjercicio.getPremisas().add(quierePeso);
            baseEjercicio.getPremisas().add(tiempoDisponible);

            // Objetivos de ejercicio
            Objetivo rutina_cardio = new Objetivo("rutina_cardio", "Rutina cardiovascular", "booleana");
            rutina_cardio.setId(11);
            rutina_cardio.setExplicacion("Recomienda ejercicios cardiovasculares para quemar grasa");

            Objetivo rutina_fuerza = new Objetivo("rutina_fuerza", "Rutina de fuerza", "booleana");
            rutina_fuerza.setId(12);
            rutina_fuerza.setExplicacion("Recomienda ejercicios de fuerza para desarrollar músculo");

            Objetivo rutina_principiante = new Objetivo("rutina_principiante", "Rutina para principiantes", "booleana");
            rutina_principiante.setId(13);
            rutina_principiante.setExplicacion("Rutina suave adaptada para principiantes");

            baseEjercicio.getObjetivos().add(rutina_cardio);
            baseEjercicio.getObjetivos().add(rutina_fuerza);
            baseEjercicio.getObjetivos().add(rutina_principiante);

            // Reglas de ejercicio
            Regla reglaCardio = new Regla("Recomendación Cardio");
            reglaCardio.setId(11);
            reglaCardio.getCondiciones().add("quiere_perder_peso = true");
            reglaCardio.getCondiciones().add("tiempo_disponible >= 30");
            reglaCardio.getAcciones().add("rutina_cardio = true");
            reglaCardio.setFactorCerteza(0.85);

            Regla reglaFuerza = new Regla("Recomendación Fuerza");
            reglaFuerza.setId(12);
            reglaFuerza.getCondiciones().add("nivel_fisico >= 2");
            reglaFuerza.getCondiciones().add("edad <= 50");
            reglaFuerza.getCondiciones().add("quiere_perder_peso = false");
            reglaFuerza.getAcciones().add("rutina_fuerza = true");
            reglaFuerza.setFactorCerteza(0.80);

            Regla reglaPrincipiante = new Regla("Rutina Principiante");
            reglaPrincipiante.setId(13);
            reglaPrincipiante.getCondiciones().add("nivel_fisico = 1");
            reglaPrincipiante.getCondiciones().add("edad >= 18");
            reglaPrincipiante.getAcciones().add("rutina_principiante = true");
            reglaPrincipiante.setFactorCerteza(0.90);

            baseEjercicio.getReglas().add(reglaCardio);
            baseEjercicio.getReglas().add(reglaFuerza);
            baseEjercicio.getReglas().add(reglaPrincipiante);

            BASES.put(baseEjercicio.getId(), baseEjercicio);

            inicializado = true;

            System.out.println("DataStore inicializado con datos de ejemplo:");
            System.out.println("- Usuarios: " + USUARIOS.size());
            System.out.println("- Bases: " + BASES.size());
            System.out.println("- Bases disponibles:");
            BASES.values().forEach(base ->
                System.out.println("  * " + base.getNombre() + " (ID: " + base.getId() + ")")
            );
        }
    }

    // === MÉTODOS PARA BASES DE CONOCIMIENTO ===

    public static BaseConocimiento crearBase(BaseConocimiento base) {
        base.setId(CONTADOR_BASE.getAndIncrement());
        BASES.put(base.getId(), base);
        System.out.println("Base creada: " + base.getNombre() + " (ID: " + base.getId() + ")");
        return base;
    }

    public static boolean actualizarBase(BaseConocimiento base) {
        if (BASES.containsKey(base.getId())) {
            BASES.put(base.getId(), base);
            System.out.println("Base actualizada: " + base.getNombre() + " (ID: " + base.getId() + ")");
            return true;
        }
        return false;
    }

    public static boolean eliminarBase(int id) {
        BaseConocimiento base = BASES.remove(id);
        if (base != null) {
            System.out.println("Base eliminada: " + base.getNombre() + " (ID: " + id + ")");
            return true;
        }
        return false;
    }

    public static BaseConocimiento buscarBasePorId(int id) {
        return BASES.get(id);
    }

    public static List<BaseConocimiento> listarTodasLasBases() {
        List<BaseConocimiento> lista = new ArrayList<>(BASES.values());
        System.out.println("Listando bases: " + lista.size() + " encontradas");
        return lista;
    }

    public static List<BaseConocimiento> listarBasesPorUsuario(int usuarioId) {
        return BASES.values().stream()
                .filter(b -> b.getUsuarioCreador() == usuarioId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // === MÉTODOS PARA USUARIOS ===

    public static boolean crearUsuario(Usuario usuario) {
        usuario.setId(CONTADOR_USUARIO.getAndIncrement());
        USUARIOS.put(usuario.getId(), usuario);
        return true;
    }

    public static Usuario buscarUsuarioPorCredenciales(String nombreUsuario, String contrasena) {
        return USUARIOS.values().stream()
                .filter(u -> u.getNombreUsuario().equals(nombreUsuario) &&
                           u.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }

    public static Usuario buscarUsuarioPorId(int id) {
        return USUARIOS.get(id);
    }

    public static List<Usuario> listarTodosUsuarios() {
        return new ArrayList<>(USUARIOS.values());
    }

    // === MÉTODOS PARA SESIONES ===

    public static boolean crearSesion(Sesion sesion) {
        sesion.setId(CONTADOR_SESION.getAndIncrement());
        SESIONES.put(sesion.getId(), sesion);
        return true;
    }

    public static List<Sesion> buscarSesionesPorUsuario(int usuarioId) {
        return SESIONES.values().stream()
                .filter(s -> s.getUsuarioId() == usuarioId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static List<Sesion> listarSesionesPorBase(int baseConocimientoId) {
        return SESIONES.values().stream()
                .filter(s -> s.getBaseConocimientoId() == baseConocimientoId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static List<Sesion> obtenerHistorialUsuario(int usuarioId) {
        return SESIONES.values().stream()
                .filter(s -> s.getUsuarioId() == usuarioId)
                .sorted((s1, s2) -> s2.getFechaInicio().compareTo(s1.getFechaInicio()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static List<Sesion> listarTodasSesiones() {
        return new ArrayList<>(SESIONES.values());
    }

    // === MÉTODOS DE UTILIDAD ===

    public static void mostrarEstadisticas() {
        System.out.println("=== ESTADÍSTICAS DEL DATASTORE ===");
        System.out.println("Bases de conocimiento: " + BASES.size());
        System.out.println("Usuarios: " + USUARIOS.size());
        System.out.println("Sesiones: " + SESIONES.size());

        if (!BASES.isEmpty()) {
            System.out.println("Bases existentes:");
            BASES.values().forEach(base ->
                System.out.println("  - " + base.getNombre() + " (ID: " + base.getId() + ")")
            );
        }
    }

    public static void reinicializar() {
        BASES.clear();
        USUARIOS.clear();
        SESIONES.clear();
        CONTADOR_BASE.set(1);
        CONTADOR_USUARIO.set(1);
        CONTADOR_SESION.set(1);
        inicializado = false;
        inicializarDatos();
    }

    // === MÉTODOS DE PERSISTENCIA ===

    /**
     * Carga datos desde archivo si existe, sino inicializa datos por defecto
     */
    private static void cargarDatosPersistentes() {
        File archivo = new File(ARCHIVO_PERSISTENCIA);
        if (archivo.exists()) {
            try {
                cargarDatos();
                System.out.println("Datos cargados desde archivo: " + ARCHIVO_PERSISTENCIA);
            } catch (Exception e) {
                System.out.println("Error al cargar datos, inicializando datos por defecto: " + e.getMessage());
                inicializarDatos();
            }
        } else {
            System.out.println("Archivo de datos no encontrado, inicializando datos por defecto");
            inicializarDatos();
        }
    }

    /**
     * Guarda todos los datos en un archivo
     */
    public static synchronized void guardarDatos() {
        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_PERSISTENCIA);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // Crear estructura de datos para serializar
            Map<String, Object> datosCompletos = new HashMap<>();
            datosCompletos.put("bases", new HashMap<>(BASES));
            datosCompletos.put("usuarios", new HashMap<>(USUARIOS));
            datosCompletos.put("sesiones", new HashMap<>(SESIONES));
            datosCompletos.put("contadorBase", CONTADOR_BASE.get());
            datosCompletos.put("contadorUsuario", CONTADOR_USUARIO.get());
            datosCompletos.put("contadorSesion", CONTADOR_SESION.get());

            oos.writeObject(datosCompletos);
            System.out.println("Datos guardados exitosamente en: " + ARCHIVO_PERSISTENCIA);

        } catch (IOException e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    /**
     * Carga todos los datos desde archivo
     */
    @SuppressWarnings("unchecked")
    private static synchronized void cargarDatos() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(ARCHIVO_PERSISTENCIA);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Map<String, Object> datosCompletos = (Map<String, Object>) ois.readObject();

            // Restaurar datos
            Map<Integer, BaseConocimiento> basesGuardadas =
                (Map<Integer, BaseConocimiento>) datosCompletos.get("bases");
            Map<Integer, Usuario> usuariosGuardados =
                (Map<Integer, Usuario>) datosCompletos.get("usuarios");
            Map<Integer, Sesion> sesionesGuardadas =
                (Map<Integer, Sesion>) datosCompletos.get("sesiones");

            BASES.clear();
            USUARIOS.clear();
            SESIONES.clear();

            if (basesGuardadas != null) BASES.putAll(basesGuardadas);
            if (usuariosGuardados != null) USUARIOS.putAll(usuariosGuardados);
            if (sesionesGuardadas != null) SESIONES.putAll(sesionesGuardadas);

            // Restaurar contadores
            CONTADOR_BASE.set((Integer) datosCompletos.getOrDefault("contadorBase", 1));
            CONTADOR_USUARIO.set((Integer) datosCompletos.getOrDefault("contadorUsuario", 1));
            CONTADOR_SESION.set((Integer) datosCompletos.getOrDefault("contadorSesion", 1));

            inicializado = true;

            System.out.println("Datos restaurados:");
            System.out.println("- Bases de conocimiento: " + BASES.size());
            System.out.println("- Usuarios: " + USUARIOS.size());
            System.out.println("- Sesiones: " + SESIONES.size());
        }
    }

    /**
     * Fuerza el guardado manual de datos
     */
    public static void forzarGuardado() {
        guardarDatos();
    }
}