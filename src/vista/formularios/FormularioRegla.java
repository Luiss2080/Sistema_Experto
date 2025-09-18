package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Regla;
import modelo.entidades.Premisa;
import java.util.List;

/**
 * Formulario para crear/editar reglas de forma intuitiva
 */
public class FormularioRegla extends JDialog {

    // Variables declaration
    private JTextField txtNombre;
    private JTextArea txtCondicionesIF;
    private JTextArea txtAccionesTHEN;
    private JSlider sliderFactorCerteza;
    private JLabel lblFactorCerteza;
    private JSpinner spnPrioridad;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JButton btnAyuda;
    private JButton btnValidar;
    private JPanel panelPrincipal;
    private JPanel panelBotones;

    private Regla regla;
    private List<Premisa> premisasDisponibles;
    private boolean guardado = false;

    public FormularioRegla(JFrame parent, Regla regla, List<Premisa> premisas) {
        super(parent, "Formulario de Regla", true);
        this.regla = regla;
        this.premisasDisponibles = premisas;
        initComponents();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(getParent());

        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Datos de la Regla"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(30);
        panelPrincipal.add(txtNombre, gbc);

        // Condiciones IF
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblCondiciones = new JLabel("Condiciones (IF):");
        lblCondiciones.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblCondiciones, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.4;
        txtCondicionesIF = new JTextArea(4, 30);
        txtCondicionesIF.setLineWrap(true);
        txtCondicionesIF.setWrapStyleWord(true);
        txtCondicionesIF.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollCondiciones = new JScrollPane(txtCondicionesIF);
        scrollCondiciones.setBorder(BorderFactory.createTitledBorder("IF (una condición por línea)"));
        panelPrincipal.add(scrollCondiciones, gbc);

        // Acciones THEN
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        JLabel lblAcciones = new JLabel("Acciones (THEN):");
        lblAcciones.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblAcciones, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.4;
        txtAccionesTHEN = new JTextArea(4, 30);
        txtAccionesTHEN.setLineWrap(true);
        txtAccionesTHEN.setWrapStyleWord(true);
        txtAccionesTHEN.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollAcciones = new JScrollPane(txtAccionesTHEN);
        scrollAcciones.setBorder(BorderFactory.createTitledBorder("THEN (una acción por línea)"));
        panelPrincipal.add(scrollAcciones, gbc);

        // Factor de Certeza
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        panelPrincipal.add(new JLabel("Factor de Certeza:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        sliderFactorCerteza = new JSlider(0, 100, 100);
        sliderFactorCerteza.setMajorTickSpacing(25);
        sliderFactorCerteza.setMinorTickSpacing(5);
        sliderFactorCerteza.setPaintTicks(true);
        sliderFactorCerteza.setPaintLabels(true);
        sliderFactorCerteza.addChangeListener(e -> {
            double valor = sliderFactorCerteza.getValue() / 100.0;
            lblFactorCerteza.setText(String.format("%.2f", valor));
        });
        panelPrincipal.add(sliderFactorCerteza, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        lblFactorCerteza = new JLabel("1.00");
        lblFactorCerteza.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblFactorCerteza, gbc);

        // Prioridad
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Prioridad:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        spnPrioridad = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        panelPrincipal.add(spnPrioridad, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPrioridadInfo = new JLabel("(1=baja, 10=alta)");
        lblPrioridadInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        panelPrincipal.add(lblPrioridadInfo, gbc);

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

        btnValidar = new JButton("Validar");
        btnValidar.setIcon(createIcon("✓", Color.BLUE));
        btnValidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarRegla();
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

        btnAyuda = new JButton("Ayuda");
        btnAyuda.setIcon(createIcon("?", Color.ORANGE));
        btnAyuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAyuda();
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
        panelBotones.add(btnValidar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnAyuda);
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
        if (regla != null) {
            txtNombre.setText(regla.getNombre());

            // Cargar condiciones
            StringBuilder condiciones = new StringBuilder();
            for (String condicion : regla.getCondiciones()) {
                condiciones.append(condicion).append("\n");
            }
            txtCondicionesIF.setText(condiciones.toString().trim());

            // Cargar acciones
            StringBuilder acciones = new StringBuilder();
            for (String accion : regla.getAcciones()) {
                acciones.append(accion).append("\n");
            }
            txtAccionesTHEN.setText(acciones.toString().trim());

            sliderFactorCerteza.setValue((int) (regla.getFactorCerteza() * 100));
            lblFactorCerteza.setText(String.format("%.2f", regla.getFactorCerteza()));
            spnPrioridad.setValue(regla.getPrioridad());
        }
    }

    private void mostrarAyuda() {
        String ayuda = "AYUDA PARA CREAR REGLAS:\n\n" +
                "FORMATO DE CONDICIONES (IF):\n" +
                "- Una condición por línea\n" +
                "- Usar el formato: premisa = valor\n" +
                "- Ejemplo: fiebre = true\n" +
                "- Ejemplo: temperatura > 38\n" +
                "- Ejemplo: edad >= 18\n\n" +
                "FORMATO DE ACCIONES (THEN):\n" +
                "- Una acción por línea\n" +
                "- Usar el formato: objetivo = resultado\n" +
                "- Ejemplo: diagnostico = gripe\n" +
                "- Ejemplo: recomendacion = descanso\n\n" +
                "OPERADORES PERMITIDOS:\n" +
                "- = (igual)\n" +
                "- > (mayor que)\n" +
                "- < (menor que)\n" +
                "- >= (mayor o igual)\n" +
                "- <= (menor o igual)\n" +
                "- != (diferente)\n\n" +
                "PREMISAS DISPONIBLES:\n";

        for (Premisa p : premisasDisponibles) {
            ayuda += "- " + p.getNombre() + " (" + p.getTipo() + ")\n";
        }

        ayuda += "\nEJEMPLO COMPLETO:\n" +
                "Nombre: Diagnóstico de Gripe\n" +
                "IF:\n" +
                "fiebre = true\n" +
                "temperatura > 38\n" +
                "THEN:\n" +
                "diagnostico = gripe\n" +
                "recomendacion = reposo";

        JTextArea textArea = new JTextArea(ayuda);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Ayuda para Reglas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void validarRegla() {
        String nombre = txtNombre.getText().trim();
        String condicionesTexto = txtCondicionesIF.getText().trim();
        String accionesTexto = txtAccionesTHEN.getText().trim();

        StringBuilder resultados = new StringBuilder("RESULTADOS DE VALIDACIÓN:\n\n");

        // Validar nombre
        if (nombre.isEmpty()) {
            resultados.append("❌ Nombre: Falta el nombre de la regla\n");
        } else {
            resultados.append("✓ Nombre: OK\n");
        }

        // Validar condiciones
        if (condicionesTexto.isEmpty()) {
            resultados.append("❌ Condiciones: No hay condiciones definidas\n");
        } else {
            String[] condiciones = condicionesTexto.split("\n");
            resultados.append("✓ Condiciones: ").append(condiciones.length).append(" encontradas\n");
            for (int i = 0; i < condiciones.length; i++) {
                String condicion = condiciones[i].trim();
                if (!condicion.isEmpty()) {
                    if (condicion.contains("=") || condicion.contains(">") || condicion.contains("<")) {
                        resultados.append("  ✓ Condición ").append(i + 1).append(": ").append(condicion).append("\n");
                    } else {
                        resultados.append("  ❌ Condición ").append(i + 1).append(": Formato incorrecto\n");
                    }
                }
            }
        }

        // Validar acciones
        if (accionesTexto.isEmpty()) {
            resultados.append("❌ Acciones: No hay acciones definidas\n");
        } else {
            String[] acciones = accionesTexto.split("\n");
            resultados.append("✓ Acciones: ").append(acciones.length).append(" encontradas\n");
            for (int i = 0; i < acciones.length; i++) {
                String accion = acciones[i].trim();
                if (!accion.isEmpty()) {
                    resultados.append("  ✓ Acción ").append(i + 1).append(": ").append(accion).append("\n");
                }
            }
        }

        // Validar factor de certeza
        double factor = sliderFactorCerteza.getValue() / 100.0;
        resultados.append("✓ Factor de Certeza: ").append(factor).append("\n");

        // Validar prioridad
        int prioridad = (Integer) spnPrioridad.getValue();
        resultados.append("✓ Prioridad: ").append(prioridad).append("\n");

        JTextArea textArea = new JTextArea(resultados.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Validación de Regla", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String condicionesTexto = txtCondicionesIF.getText().trim();
        String accionesTexto = txtAccionesTHEN.getText().trim();
        double factorCerteza = sliderFactorCerteza.getValue() / 100.0;
        int prioridad = (Integer) spnPrioridad.getValue();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (condicionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe definir al menos una condición",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtCondicionesIF.requestFocus();
            return;
        }

        if (accionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe definir al menos una acción",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtAccionesTHEN.requestFocus();
            return;
        }

        // Guardar datos
        if (regla == null) {
            regla = new Regla();
        }

        regla.setNombre(nombre);
        regla.setFactorCerteza(factorCerteza);
        regla.setPrioridad(prioridad);

        // Limpiar condiciones y acciones anteriores
        regla.getCondiciones().clear();
        regla.getAcciones().clear();

        // Agregar condiciones
        String[] condiciones = condicionesTexto.split("\n");
        for (String condicion : condiciones) {
            if (!condicion.trim().isEmpty()) {
                regla.agregarCondicion(condicion.trim());
            }
        }

        // Agregar acciones
        String[] acciones = accionesTexto.split("\n");
        for (String accion : acciones) {
            if (!accion.trim().isEmpty()) {
                regla.agregarAccion(accion.trim());
            }
        }

        guardado = true;

        JOptionPane.showMessageDialog(this,
            "Regla guardada exitosamente",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCondicionesIF.setText("");
        txtAccionesTHEN.setText("");
        sliderFactorCerteza.setValue(100);
        lblFactorCerteza.setText("1.00");
        spnPrioridad.setValue(1);
        txtNombre.requestFocus();
    }

    public Regla getRegla() {
        return guardado ? regla : null;
    }

    public boolean isGuardado() {
        return guardado;
    }
}