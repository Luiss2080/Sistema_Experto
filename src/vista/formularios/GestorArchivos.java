package vista.formularios;

import modelo.entidades.*;
import persistencia.archivos.ManejadorArchivosMejorado;
import persistencia.dao.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Gestor simplificado de archivos para importar/exportar bases de conocimiento
 */
public class GestorArchivos extends JDialog {
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private JComboBox<BaseConocimiento> cmbBases;
    private JButton btnExportar;
    private JButton btnImportar;
    private boolean operacionExitosa = false;

    public GestorArchivos(JFrame parent, Usuario usuario) {
        super(parent, "Gestión de Archivos", true);
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        initComponents();
        cargarBases();
    }

    private void initComponents() {
        setSize(500, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        
        // Combo de bases
        JPanel panelTop = new JPanel(new FlowLayout());
        panelTop.add(new JLabel("Base:"));
        cmbBases = new JComboBox<>();
        panelTop.add(cmbBases);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnExportar = new JButton("Exportar");
        btnImportar = new JButton("Importar");
        
        btnExportar.addActionListener(e -> exportarBase());
        btnImportar.addActionListener(e -> importarBase());
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnImportar);
        panelBotones.add(new JButton("Cerrar") {{ addActionListener(e -> dispose()); }});
        
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.SOUTH);
        add(panel);
    }

    private void cargarBases() {
        try {
            cmbBases.removeAllItems();
            for (BaseConocimiento base : baseDAO.listarTodas()) {
                cmbBases.addItem(base);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void exportarBase() {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) return;
        
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(base.getNombre() + ".dat"));
        
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String archivo = fc.getSelectedFile().getAbsolutePath();
            if (ManejadorArchivosMejorado.exportarBaseConocimiento(base, archivo)) {
                operacionExitosa = true;
                JOptionPane.showMessageDialog(this, "Exportado exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al exportar");
            }
        }
    }

    private void importarBase() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String archivo = fc.getSelectedFile().getAbsolutePath();
            BaseConocimiento base = ManejadorArchivosMejorado.importarBaseConocimiento(archivo);
            
            if (base != null) {
                base.setId(0);
                base.setUsuarioCreador(usuario.getId());
                base.setFechaCreacion(java.time.LocalDateTime.now());
                
                try {
                    if (baseDAO.crear(base) != null) {
                        operacionExitosa = true;
                        cargarBases();
                        JOptionPane.showMessageDialog(this, "Importado exitosamente");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al leer archivo");
            }
        }
    }

    public boolean isOperacionExitosa() {
        return operacionExitosa;
    }
}