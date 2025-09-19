package persistencia.archivos;

import modelo.entidades.*;
import persistencia.dao.DataStore;
import java.io.*;
import java.util.*;

/**
 * Manejador de archivos mejorado para guardar y cargar datos del sistema
 */
public class ManejadorArchivosMejorado {
    
    private static final String DIRECTORIO_DATOS = "datos/";
    private static final String ARCHIVO_BASES = DIRECTORIO_DATOS + "bases_conocimiento.dat";
    private static final String ARCHIVO_USUARIOS = DIRECTORIO_DATOS + "usuarios.dat";
    private static final String ARCHIVO_SESIONES = DIRECTORIO_DATOS + "sesiones.dat";
    
    static {
        // Crear directorio si no existe
        File directorio = new File(DIRECTORIO_DATOS);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }
    
    /**
     * Guarda todas las bases de conocimiento en archivo
     */
    public static boolean guardarBases(List<BaseConocimiento> bases) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_BASES))) {
            oos.writeObject(bases);
            System.out.println("Bases de conocimiento guardadas: " + bases.size() + " elementos");
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar bases: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga todas las bases de conocimiento desde archivo
     */
    @SuppressWarnings("unchecked")
    public static List<BaseConocimiento> cargarBases() {
        File archivo = new File(ARCHIVO_BASES);
        if (!archivo.exists()) {
            System.out.println("Archivo de bases no existe, retornando lista vacía");
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_BASES))) {
            List<BaseConocimiento> bases = (List<BaseConocimiento>) ois.readObject();
            System.out.println("Bases de conocimiento cargadas: " + bases.size() + " elementos");
            return bases;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar bases: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Guarda todos los usuarios en archivo
     */
    public static boolean guardarUsuarios(List<Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
            oos.writeObject(usuarios);
            System.out.println("Usuarios guardados: " + usuarios.size() + " elementos");
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga todos los usuarios desde archivo
     */
    @SuppressWarnings("unchecked")
    public static List<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            System.out.println("Archivo de usuarios no existe, retornando lista vacía");
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_USUARIOS))) {
            List<Usuario> usuarios = (List<Usuario>) ois.readObject();
            System.out.println("Usuarios cargados: " + usuarios.size() + " elementos");
            return usuarios;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Guarda todas las sesiones en archivo
     */
    public static boolean guardarSesiones(List<Sesion> sesiones) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_SESIONES))) {
            oos.writeObject(sesiones);
            System.out.println("Sesiones guardadas: " + sesiones.size() + " elementos");
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar sesiones: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga todas las sesiones desde archivo
     */
    @SuppressWarnings("unchecked")
    public static List<Sesion> cargarSesiones() {
        File archivo = new File(ARCHIVO_SESIONES);
        if (!archivo.exists()) {
            System.out.println("Archivo de sesiones no existe, retornando lista vacía");
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_SESIONES))) {
            List<Sesion> sesiones = (List<Sesion>) ois.readObject();
            System.out.println("Sesiones cargadas: " + sesiones.size() + " elementos");
            return sesiones;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar sesiones: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Exporta una base de conocimiento específica a un archivo
     */
    public static boolean exportarBaseConocimiento(BaseConocimiento base, String rutaArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(base);
            System.out.println("Base exportada: " + base.getNombre() + " -> " + rutaArchivo);
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar base: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Importa una base de conocimiento desde un archivo
     */
    public static BaseConocimiento importarBaseConocimiento(String rutaArchivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            BaseConocimiento base = (BaseConocimiento) ois.readObject();
            System.out.println("Base importada: " + base.getNombre() + " desde " + rutaArchivo);
            return base;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al importar base: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Crea un respaldo completo del sistema
     */
    public static boolean crearRespaldo() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String directorioRespaldo = DIRECTORIO_DATOS + "respaldo_" + timestamp + "/";
            
            File directorio = new File(directorioRespaldo);
            directorio.mkdirs();
            
            // Guardar bases
            List<BaseConocimiento> bases = DataStore.listarTodasLasBases();
            guardarBasesEnArchivo(bases, directorioRespaldo + "bases.dat");
            
            // Guardar usuarios
            List<Usuario> usuarios = DataStore.listarTodosUsuarios();
            guardarUsuariosEnArchivo(usuarios, directorioRespaldo + "usuarios.dat");
            
            // Guardar sesiones
            List<Sesion> sesiones = DataStore.listarTodasSesiones();
            guardarSesionesEnArchivo(sesiones, directorioRespaldo + "sesiones.dat");
            
            System.out.println("Respaldo creado en: " + directorioRespaldo);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al crear respaldo: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean guardarBasesEnArchivo(List<BaseConocimiento> bases, String archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(bases);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar bases en archivo: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean guardarUsuariosEnArchivo(List<Usuario> usuarios, String archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(usuarios);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios en archivo: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean guardarSesionesEnArchivo(List<Sesion> sesiones, String archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(sesiones);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar sesiones en archivo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Restaura un respaldo del sistema
     */
    public static boolean restaurarRespaldo(String directorioRespaldo) {
        try {
            // Cargar bases
            List<BaseConocimiento> bases = cargarBasesDesdeArchivo(directorioRespaldo + "bases.dat");
            if (bases != null) {
                for (BaseConocimiento base : bases) {
                    DataStore.crearBase(base);
                }
            }
            
            // Cargar usuarios
            List<Usuario> usuarios = cargarUsuariosDesdeArchivo(directorioRespaldo + "usuarios.dat");
            if (usuarios != null) {
                for (Usuario usuario : usuarios) {
                    DataStore.crearUsuario(usuario);
                }
            }
            
            // Cargar sesiones
            List<Sesion> sesiones = cargarSesionesDesdeArchivo(directorioRespaldo + "sesiones.dat");
            if (sesiones != null) {
                for (Sesion sesion : sesiones) {
                    DataStore.crearSesion(sesion);
                }
            }
            
            System.out.println("Respaldo restaurado desde: " + directorioRespaldo);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al restaurar respaldo: " + e.getMessage());
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<BaseConocimiento> cargarBasesDesdeArchivo(String archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<BaseConocimiento>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar bases desde archivo: " + e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<Usuario> cargarUsuariosDesdeArchivo(String archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar usuarios desde archivo: " + e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<Sesion> cargarSesionesDesdeArchivo(String archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Sesion>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar sesiones desde archivo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Exporta una base de conocimiento a formato de texto legible
     */
    public static boolean exportarBaseConocimientoTexto(BaseConocimiento base, String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("=== BASE DE CONOCIMIENTO ===");
            writer.println("Nombre: " + base.getNombre());
            writer.println("Descripción: " + base.getDescripcion());
            writer.println("Fecha de creación: " + base.getFechaCreacion());
            writer.println();
            
            writer.println("=== PREMISAS ===");
            for (Premisa premisa : base.getPremisas()) {
                writer.println("- " + premisa.getNombre() + " (" + premisa.getTipo() + ")");
                writer.println("  Descripción: " + premisa.getDescripcion());
                writer.println("  Valor por defecto: " + premisa.getValorDefecto());
                writer.println();
            }
            
            writer.println("=== OBJETIVOS ===");
            for (Objetivo objetivo : base.getObjetivos()) {
                writer.println("- " + objetivo.getNombre());
                writer.println("  Descripción: " + objetivo.getDescripcion());
                writer.println("  Tipo de respuesta: " + objetivo.getTipoRespuesta());
                writer.println("  Explicación: " + objetivo.getExplicacion());
                writer.println();
            }
            
            writer.println("=== REGLAS ===");
            for (Regla regla : base.getReglas()) {
                writer.println("- " + regla.getNombre());
                writer.println("  Condiciones: " + String.join(" AND ", regla.getCondiciones()));
                writer.println("  Acciones: " + String.join(", ", regla.getAcciones()));
                writer.println("  Factor de certeza: " + regla.getFactorCerteza());
                writer.println("  Prioridad: " + regla.getPrioridad());
                writer.println();
            }
            
            System.out.println("Base exportada a texto: " + rutaArchivo);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar base a texto: " + e.getMessage());
            return false;
        }
    }
}