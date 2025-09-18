package vista.comun;

import modelo.entidades.*;
import persistencia.dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Ventana que muestra información del estado actual del sistema
 */
public class InfoSistema extends JDialog {

    private JFrame parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private SesionDAO sesionDAO;

    private JTextArea txtInfo;
    private JButton btnActualizar;
    private JButton btnCerrar;

    public InfoSistema(JFrame parent, Usuario usuario) {
        super(parent, "Información del Sistema", true);
        this.parent = parent;
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        this.sesionDAO = new SesionDAOImpl();

        initComponents();
        actualizarInfo();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Estado del Sistema Experto");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);

        // Área de información
        txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollInfo = new JScrollPane(txtInfo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnActualizar = new JButton("Actualizar Información");
        JButton btnDebug = new JButton("Debug DataStore");
        btnCerrar = new JButton("Cerrar");

        btnActualizar.addActionListener(e -> actualizarInfo());
        btnDebug.addActionListener(e -> {
            DataStore.mostrarEstadisticas();
            actualizarInfo();
        });
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnDebug);
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollInfo, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void actualizarInfo() {
        StringBuilder info = new StringBuilder();

        // Información del usuario
        info.append("=== INFORMACIÓN DEL USUARIO ===\\n\\n");
        info.append(String.format("Usuario: %s\\n", usuario.getNombreUsuario()));
        info.append(String.format("Tipo: %s\\n", usuario.getTipoUsuario()));
        info.append(String.format("Fecha de registro: %s\\n", usuario.getFechaRegistro()));

        // Estadísticas del sistema
        info.append("\\n=== ESTADÍSTICAS DEL SISTEMA ===\\n\\n");

        List<BaseConocimiento> bases = baseDAO.listarTodas();
        info.append(String.format("Total de bases de conocimiento: %d\\n", bases.size()));

        if (!bases.isEmpty()) {
            int totalPremisas = 0;
            int totalObjetivos = 0;
            int totalReglas = 0;

            for (BaseConocimiento base : bases) {
                totalPremisas += base.getPremisas().size();
                totalObjetivos += base.getObjetivos().size();
                totalReglas += base.getReglas().size();
            }

            info.append(String.format("Total de premisas: %d\\n", totalPremisas));
            info.append(String.format("Total de objetivos: %d\\n", totalObjetivos));
            info.append(String.format("Total de reglas: %d\\n", totalReglas));

            info.append("\\n=== DETALLE DE BASES ===\\n\\n");
            for (BaseConocimiento base : bases) {
                info.append(String.format("📁 %s\\n", base.getNombre()));
                info.append(String.format("   Descripción: %s\\n", base.getDescripcion()));
                info.append(String.format("   Premisas: %d | Objetivos: %d | Reglas: %d\\n",
                    base.getPremisas().size(),
                    base.getObjetivos().size(),
                    base.getReglas().size()));

                // Información de sesiones
                List<Sesion> sesiones = sesionDAO.listarPorBase(base.getId());
                info.append(String.format("   Consultas realizadas: %d\\n", sesiones.size()));
                info.append("\\n");
            }
        }

        // Información de sesiones del usuario
        List<Sesion> misSesiones = sesionDAO.buscarPorUsuario(usuario.getId());
        info.append("\\n=== MIS CONSULTAS ===\\n\\n");
        info.append(String.format("Total de consultas realizadas: %d\\n", misSesiones.size()));

        if (!misSesiones.isEmpty()) {
            info.append("\\nÚltimas 5 consultas:\\n");
            List<Sesion> historial = sesionDAO.obtenerHistorial(usuario.getId());
            for (int i = 0; i < Math.min(5, historial.size()); i++) {
                Sesion sesion = historial.get(i);
                BaseConocimiento base = baseDAO.buscarPorId(sesion.getBaseConocimientoId());
                String nombreBase = base != null ? base.getNombre() : "Base no encontrada";

                info.append(String.format("%d. %s - %s\\n",
                    i + 1,
                    sesion.getFechaInicio(),
                    nombreBase));
                info.append(String.format("   Hechos: %d | Conclusiones: %d\\n",
                    sesion.getHechos().size(),
                    sesion.getConclusiones().size()));
            }
        }

        // Información del sistema
        info.append("\\n=== INFORMACIÓN TÉCNICA ===\\n\\n");
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        long memoryMax = runtime.maxMemory();

        info.append(String.format("Versión Java: %s\\n", System.getProperty("java.version")));
        info.append(String.format("Sistema Operativo: %s\\n", System.getProperty("os.name")));
        info.append(String.format("Memoria utilizada: %.1f MB\\n", memoryUsed / (1024.0 * 1024.0)));
        info.append(String.format("Memoria máxima: %.1f MB\\n", memoryMax / (1024.0 * 1024.0)));

        txtInfo.setText(info.toString());
        txtInfo.setCaretPosition(0);

        // Actualizar título con estadísticas
        setTitle(String.format("Info del Sistema - %d bases, %d consultas",
            bases.size(), misSesiones.size()));
    }

    /**
     * Muestra la ventana de información del sistema
     */
    public static void mostrar(JFrame parent, Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            InfoSistema info = new InfoSistema(parent, usuario);
            info.setVisible(true);
        });
    }
}