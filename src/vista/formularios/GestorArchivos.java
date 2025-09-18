package vista.formularios;

import modelo.entidades.*;
import persistencia.archivos.ManejadorArchivos;
import persistencia.dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GestorArchivos extends JDialog {

    private JFrame parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private SesionDAO sesionDAO;
    private boolean operacionExitosa;

    private JComboBox<BaseConocimiento> cmbBases;
    private JCheckBox chkIncluirSesiones;
    private JButton btnExportar;
    private JButton btnImportar;
    private JButton btnCerrar;
    private JTextArea txtInfo;

    public GestorArchivos(JFrame parent, Usuario usuario) {
        super(parent, "Gestión de Archivos", true);
        this.parent = parent;
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        this.sesionDAO = new SesionDAOImpl();
        this.operacionExitosa = false;

        initComponents();
        cargarBases();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior - Exportación
        JPanel panelExportacion = new JPanel(new GridBagLayout());
        panelExportacion.setBorder(BorderFactory.createTitledBorder("Exportar Base de Conocimiento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelExportacion.add(new JLabel("Base de Conocimiento:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbBases = new JComboBox<>();
        cmbBases.addActionListener(e -> actualizarInfo());
        panelExportacion.add(cmbBases, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        chkIncluirSesiones = new JCheckBox("Incluir historial de sesiones", true);
        chkIncluirSesiones.addActionListener(e -> actualizarInfo());
        panelExportacion.add(chkIncluirSesiones, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnExportar = new JButton("Exportar a Archivo");
        btnExportar.addActionListener(this::exportarBase);
        panelExportacion.add(btnExportar, gbc);

        // Panel central - Información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));

        txtInfo = new JTextArea(8, 40);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtInfo.setText("Seleccione una base de conocimiento para ver su información...");
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        panelInfo.add(scrollInfo, BorderLayout.CENTER);

        // Panel inferior - Importación y botones
        JPanel panelInferior = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel panelImportacion = new JPanel(new BorderLayout());
        panelImportacion.setBorder(BorderFactory.createTitledBorder("Importar Base de Conocimiento"));

        btnImportar = new JButton("Importar desde Archivo");
        btnImportar.addActionListener(this::importarBase);
        panelImportacion.add(btnImportar, BorderLayout.CENTER);

        JLabel lblAyuda = new JLabel("<html><i>La importación agregará la base de conocimiento al sistema<br/>" +
                                   "conservando todas sus premisas, objetivos, reglas y sesiones.</i></html>");
        lblAyuda.setFont(lblAyuda.getFont().deriveFont(Font.ITALIC, 11f));
        panelImportacion.add(lblAyuda, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelInferior.add(panelImportacion, gbc);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelInferior.add(panelBotones, gbc);

        panelPrincipal.add(panelExportacion, BorderLayout.NORTH);
        panelPrincipal.add(panelInfo, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Configurar renderer para el combobox
        cmbBases.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BaseConocimiento) {
                    setText(((BaseConocimiento) value).getNombre());
                }
                return this;
            }
        });
    }

    private void cargarBases() {
        cmbBases.removeAllItems();
        for (BaseConocimiento base : baseDAO.listarTodas()) {
            cmbBases.addItem(base);
        }
    }

    private void actualizarInfo() {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) {
            txtInfo.setText("No hay base de conocimiento seleccionada.");
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DE LA BASE ===\\n\\n");
        info.append(String.format("Nombre: %s\\n", base.getNombre()));
        info.append(String.format("Descripción: %s\\n", base.getDescripcion()));
        info.append(String.format("Fecha de creación: %s\\n", base.getFechaCreacion()));
        info.append("\\n=== CONTENIDO ===\\n\\n");
        info.append(String.format("Premisas definidas: %d\\n", base.getPremisas().size()));
        info.append(String.format("Objetivos definidos: %d\\n", base.getObjetivos().size()));
        info.append(String.format("Reglas definidas: %d\\n", base.getReglas().size()));

        if (chkIncluirSesiones.isSelected()) {
            List<Sesion> sesiones = sesionDAO.listarPorBase(base.getId());
            info.append(String.format("Sesiones guardadas: %d\\n", sesiones.size()));

            if (!sesiones.isEmpty()) {
                info.append("\\n=== RESUMEN DE SESIONES ===\\n\\n");
                sesiones.stream().limit(5).forEach(sesion -> {
                    info.append(String.format("- %s: %d hechos, %d conclusiones\\n",
                        sesion.getFechaInicio(),
                        sesion.getHechos().size(),
                        sesion.getConclusiones().size()));
                });
                if (sesiones.size() > 5) {
                    info.append(String.format("... y %d sesiones más\\n", sesiones.size() - 5));
                }
            }
        }

        info.append("\\n=== DETALLES DE PREMISAS ===\\n\\n");
        base.getPremisas().forEach(premisa -> {
            info.append(String.format("• %s (%s)\\n", premisa.getNombre(), premisa.getTipo()));
        });

        if (!base.getObjetivos().isEmpty()) {
            info.append("\\n=== OBJETIVOS ===\\n\\n");
            base.getObjetivos().forEach(objetivo -> {
                info.append(String.format("• %s: %s\\n", objetivo.getNombre(), objetivo.getDescripcion()));
            });
        }

        txtInfo.setText(info.toString());
        txtInfo.setCaretPosition(0);
    }

    private void exportarBase(ActionEvent e) {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una base de conocimiento para exportar.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Sesion> sesiones = null;
        if (chkIncluirSesiones.isSelected()) {
            sesiones = sesionDAO.listarPorBase(base.getId());
        }

        if (ManejadorArchivos.exportarBaseConocimiento((JFrame)getParent(), base, sesiones)) {
            operacionExitosa = true;
        }
    }

    private void importarBase(ActionEvent e) {
        ManejadorArchivos.DatosExportacion datos = ManejadorArchivos.importarBaseConocimiento((JFrame)getParent());
        if (datos != null) {
            BaseConocimiento baseImportada = datos.getBaseConocimiento();

            // Verificar si ya existe una base con el mismo nombre
            boolean existe = baseDAO.listarTodas().stream()
                    .anyMatch(base -> base.getNombre().equals(baseImportada.getNombre()));

            if (existe) {
                int opcion = JOptionPane.showConfirmDialog(this,
                    "Ya existe una base de conocimiento con el nombre '" + baseImportada.getNombre() + "'.\\n" +
                    "¿Desea continuar? Se creará con un nombre modificado.",
                    "Base Existente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (opcion != JOptionPane.YES_OPTION) {
                    return;
                }

                // Modificar nombre para evitar conflictos
                baseImportada.setNombre(baseImportada.getNombre() + "_importada_" + System.currentTimeMillis());
            }

            // Limpiar IDs para nueva inserción
            baseImportada.setId(0);
            baseImportada.setUsuarioCreador(usuario.getId());

            // Limpiar IDs de premisas, objetivos y reglas
            baseImportada.getPremisas().forEach(premisa -> premisa.setId(0));
            baseImportada.getObjetivos().forEach(objetivo -> objetivo.setId(0));
            baseImportada.getReglas().forEach(regla -> regla.setId(0));

            // Guardar en la base de datos
            BaseConocimiento baseCreada = baseDAO.crear(baseImportada);

            // Importar sesiones si existen
            if (!datos.getSesiones().isEmpty()) {
                for (Sesion sesion : datos.getSesiones()) {
                    sesion.setId(0);
                    sesion.setBaseConocimientoId(baseCreada.getId());
                    sesion.setUsuarioId(usuario.getId());
                    sesionDAO.crear(sesion);
                }
            }

            // Actualizar la lista
            cargarBases();

            // Seleccionar la base importada
            for (int i = 0; i < cmbBases.getItemCount(); i++) {
                BaseConocimiento base = cmbBases.getItemAt(i);
                if (base.getId() == baseCreada.getId()) {
                    cmbBases.setSelectedIndex(i);
                    break;
                }
            }

            operacionExitosa = true;
        }
    }

    public boolean isOperacionExitosa() {
        return operacionExitosa;
    }
}