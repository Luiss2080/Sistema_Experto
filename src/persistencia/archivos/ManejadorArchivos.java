package persistencia.archivos;

import modelo.entidades.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManejadorArchivos {

    private static final String EXTENSION = "sexp";
    private static final String DESCRIPCION = "Archivo Sistema Experto (.sexp)";

    public static class DatosExportacion implements Serializable {
        private static final long serialVersionUID = 1L;
        private BaseConocimiento baseConocimiento;
        private List<Sesion> sesiones;
        private String version;
        private long fechaExportacion;

        public DatosExportacion() {
            this.version = "1.0";
            this.fechaExportacion = System.currentTimeMillis();
            this.sesiones = new ArrayList<>();
        }

        // Getters y setters
        public BaseConocimiento getBaseConocimiento() { return baseConocimiento; }
        public void setBaseConocimiento(BaseConocimiento baseConocimiento) { this.baseConocimiento = baseConocimiento; }
        public List<Sesion> getSesiones() { return sesiones; }
        public void setSesiones(List<Sesion> sesiones) { this.sesiones = sesiones; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public long getFechaExportacion() { return fechaExportacion; }
        public void setFechaExportacion(long fechaExportacion) { this.fechaExportacion = fechaExportacion; }
    }

    /**
     * Exporta una base de conocimiento completa a un archivo
     */
    public static boolean exportarBaseConocimiento(JFrame parent, BaseConocimiento base, List<Sesion> sesiones) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Base de Conocimiento");

        // Configurar filtro de archivos
        FileNameExtensionFilter filter = new FileNameExtensionFilter(DESCRIPCION, EXTENSION);
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Nombre por defecto
        String nombreArchivo = base.getNombre().replaceAll("[^a-zA-Z0-9]", "_");
        fileChooser.setSelectedFile(new File(nombreArchivo + "." + EXTENSION));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            // Asegurar extensión correcta
            if (!archivo.getName().toLowerCase().endsWith("." + EXTENSION)) {
                archivo = new File(archivo.getParentFile(), archivo.getName() + "." + EXTENSION);
            }

            try {
                DatosExportacion datos = new DatosExportacion();
                datos.setBaseConocimiento(base);
                datos.setSesiones(sesiones != null ? sesiones : new ArrayList<>());

                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
                    oos.writeObject(datos);
                }

                JOptionPane.showMessageDialog(parent,
                    "Base de conocimiento exportada exitosamente a:\\n" + archivo.getAbsolutePath(),
                    "Exportación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

                return true;

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Error al exportar el archivo:\\n" + e.getMessage(),
                    "Error de Exportación",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return false;
    }

    /**
     * Importa una base de conocimiento desde un archivo
     */
    public static DatosExportacion importarBaseConocimiento(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir Base de Conocimiento");

        // Configurar filtro de archivos
        FileNameExtensionFilter filter = new FileNameExtensionFilter(DESCRIPCION, EXTENSION);
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            try {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                    Object objeto = ois.readObject();

                    if (objeto instanceof DatosExportacion) {
                        DatosExportacion datos = (DatosExportacion) objeto;

                        JOptionPane.showMessageDialog(parent,
                            "Base de conocimiento importada exitosamente:\\n" +
                            "Nombre: " + datos.getBaseConocimiento().getNombre() + "\\n" +
                            "Premisas: " + datos.getBaseConocimiento().getPremisas().size() + "\\n" +
                            "Objetivos: " + datos.getBaseConocimiento().getObjetivos().size() + "\\n" +
                            "Reglas: " + datos.getBaseConocimiento().getReglas().size() + "\\n" +
                            "Sesiones: " + datos.getSesiones().size(),
                            "Importación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                        return datos;
                    } else {
                        JOptionPane.showMessageDialog(parent,
                            "El archivo seleccionado no es una base de conocimiento válida.",
                            "Archivo Inválido",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(parent,
                    "Error al importar el archivo:\\n" + e.getMessage() +
                    "\\n\\nVerifique que el archivo sea válido y no esté corrupto.",
                    "Error de Importación",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        return null;
    }

    /**
     * Exporta solo la estructura de la base (sin sesiones)
     */
    public static boolean exportarEstructuraBase(JFrame parent, BaseConocimiento base) {
        return exportarBaseConocimiento(parent, base, new ArrayList<>());
    }

    /**
     * Verifica si un archivo es una base de conocimiento válida
     */
    public static boolean esArchivoValido(File archivo) {
        if (!archivo.exists() || !archivo.canRead()) {
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object objeto = ois.readObject();
            return objeto instanceof DatosExportacion;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene información básica de un archivo sin cargarlo completamente
     */
    public static String obtenerInfoArchivo(File archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object objeto = ois.readObject();

            if (objeto instanceof DatosExportacion) {
                DatosExportacion datos = (DatosExportacion) objeto;
                BaseConocimiento base = datos.getBaseConocimiento();

                return String.format(
                    "Nombre: %s\\nDescripción: %s\\nPremisas: %d\\nObjetivos: %d\\nReglas: %d\\nSesiones: %d",
                    base.getNombre(),
                    base.getDescripcion(),
                    base.getPremisas().size(),
                    base.getObjetivos().size(),
                    base.getReglas().size(),
                    datos.getSesiones().size()
                );
            }
        } catch (Exception e) {
            return "Error al leer archivo: " + e.getMessage();
        }

        return "Archivo inválido";
    }
}