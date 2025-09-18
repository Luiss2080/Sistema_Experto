package persistencia.dao;

import modelo.entidades.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    static {
        inicializarDatos();
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
}