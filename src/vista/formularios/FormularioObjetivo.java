package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Objetivo;

/**
 * Formulario para crear/editar objetivos de forma intuitiva
 */
public class FormularioObjetivo extends JDialog {

    // Variables declaration
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbTipoRespuesta;
    private JTextArea txtExplicacion;
    private JTextField txtCriterio;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JButton btnEjemplos;
    private JPanel panelPrincipal;
    private JPanel panelBotones;

    private Objetivo objetivo;
    private boolean guardado = false;

    public FormularioObjetivo(JFrame parent, Objetivo objetivo) {
        super(parent, "Formulario de Objetivo", true);
        this.objetivo = objetivo;
        initComponents();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(getParent());

        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Datos del Objetivo"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(25);
        panelPrincipal.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDescripcion = new JTextArea(3, 25);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelPrincipal.add(scrollDesc, gbc);

        // Tipo de Respuesta
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        panelPrincipal.add(new JLabel("Tipo de Respuesta:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbTipoRespuesta = new JComboBox<>(new String[]{
            "SI/NO", "TEXTO", "NUMÉRICO", "CATEGORÍA", "MÚLTIPLE", "PORCENTAJE"
        });
        panelPrincipal.add(cmbTipoRespuesta, gbc);

        // Criterio
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Criterio:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCriterio = new JTextField(25);
        panelPrincipal.add(txtCriterio, gbc);

        // Explicación
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Explicación:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.7;
        txtExplicacion = new JTextArea(4, 25);
        txtExplicacion.setLineWrap(true);
        txtExplicacion.setWrapStyleWord(true);
        JScrollPane scrollExpl = new JScrollPane(txtExplicacion);
        panelPrincipal.add(scrollExpl, gbc);

        // Panel de botones
        panelBotones = new JPanel(new FlowLayout());

        btnGuardar = new JButton("Guardar");
        btnGuardar.setIcon(createIcon("✓", Color.GREEN));
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(createIcon("↻", Color.BLUE));
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        btnEjemplos = new JButton("Ejemplos");
        btnEjemplos.setIcon(createIcon("?", Color.ORANGE));
        btnEjemplos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEjemplos();
            }
        });

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setIcon(createIcon("✗", Color.RED));
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEjemplos);
        panelBotones.add(btnCancelar);

        // Layout principal
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private Icon createIcon(String text, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(text, x, y + 10);
            }

            @Override
            public int getIconWidth() { return 15; }

            @Override
            public int getIconHeight() { return 15; }
        };
    }

    private void cargarDatos() {
        if (objetivo != null) {
            txtNombre.setText(objetivo.getNombre());
            txtDescripcion.setText(objetivo.getDescripcion());
            txtCriterio.setText(objetivo.getCriterio());
            txtExplicacion.setText(objetivo.getExplicacion());

            if (objetivo.getTipoRespuesta() != null) {
                cmbTipoRespuesta.setSelectedItem(objetivo.getTipoRespuesta());
            }
        }
    }

    private void mostrarEjemplos() {
        String ejemplos = "EJEMPLOS DE OBJETIVOS:\n\n" +
                "1. DIAGNÓSTICO MÉDICO:\n" +
                "   Nombre: diagnostico_gripe\n" +
                "   Tipo: SI/NO\n" +
                "   Criterio: fiebre > 38 AND dolor_cabeza = true\n\n" +
                "2. SISTEMA FINANCIERO:\n" +
                "   Nombre: aprobacion_credito\n" +
                "   Tipo: CATEGORÍA\n" +
                "   Criterio: ingresos > 3000 AND score_crediticio > 600\n\n" +
                "3. SISTEMA EDUCATIVO:\n" +
                "   Nombre: nivel_conocimiento\n" +
                "   Tipo: PORCENTAJE\n" +
                "   Criterio: (respuestas_correctas / total_preguntas) * 100\n\n" +
                "4. SISTEMA DE RECOMENDACIÓN:\n" +
                "   Nombre: producto_recomendado\n" +
                "   Tipo: TEXTO\n" +
                "   Criterio: perfil_usuario MATCHES categoria_producto";

        JTextArea textArea = new JTextArea(ejemplos);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(this, scrollPane, "Ejemplos de Objetivos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipoRespuesta = (String) cmbTipoRespuesta.getSelectedItem();
        String criterio = txtCriterio.getText().trim();
        String explicacion = txtExplicacion.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La descripción es obligatoria",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }

        // Guardar datos
        if (objetivo == null) {
            objetivo = new Objetivo();
        }

        objetivo.setNombre(nombre);
        objetivo.setDescripcion(descripcion);
        objetivo.setTipoRespuesta(tipoRespuesta);
        objetivo.definirCriterios(criterio);
        objetivo.setExplicacion(explicacion);

        guardado = true;

        JOptionPane.showMessageDialog(this,
            "Objetivo guardado exitosamente",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cmbTipoRespuesta.setSelectedIndex(0);
        txtCriterio.setText("");
        txtExplicacion.setText("");
        txtNombre.requestFocus();
    }

    public Objetivo getObjetivo() {
        return guardado ? objetivo : null;
    }

    public boolean isGuardado() {
        return guardado;
    }
}