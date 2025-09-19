package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Premisa;

public class FormularioPremisa extends JDialog {
    private JFrame parent;
    private Premisa premisaOriginal;
    private Premisa premisaCreada;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<Premisa.TipoDato> cmbTipo;
    private JTextField txtValorDefecto;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public FormularioPremisa(JFrame parent, Premisa premisa) {
        super(parent, premisa == null ? "Agregar Nueva Premisa" : "Editar Premisa", true);
        this.parent = parent;
        this.premisaOriginal = premisa;
        this.premisaCreada = null;

        initComponents();
        if (premisa != null) {
            cargarDatos(premisa);
        }
    }

    private void initComponents() {
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de instrucciones mejorado
        JPanel panelInstrucciones = new JPanel(new BorderLayout());
        panelInstrucciones.setBorder(BorderFactory.createTitledBorder("Guía para Crear Premisas"));

        JTextArea txtInstrucciones = new JTextArea(
            "PASO A PASO PARA CREAR UNA PREMISA:\n\n" +
            "1. NOMBRE: Use identificadores simples sin espacios\n" +
            "   • Ejemplos CORRECTOS: 'fiebre', 'edad', 'temperatura_motor'\n" +
            "   • Ejemplos INCORRECTOS: 'Tiene fiebre', 'Edad del paciente'\n\n" +
            "2. DESCRIPCIÓN: Escriba la pregunta exacta que verá el cliente\n" +
            "   • Para BOOLEANA: '¿Tiene fiebre?', '¿Funciona correctamente?'\n" +
            "   • Para NUMERICA: 'Edad en años', 'Temperatura en grados'\n" +
            "   • Para STRING: 'Nombre del paciente', 'Describe el problema'\n\n" +
            "3. TIPO DE DATO:\n" +
            "   • BOOLEANA: Preguntas Sí/No (cliente verá opciones Sí/No)\n" +
            "   • NUMERICA: Valores numéricos (edad, temperatura, etc.)\n" +
            "   • STRING: Texto libre (nombres, descripciones)\n\n" +
            "4. VALOR POR DEFECTO: Valor inicial que aparecerá en el formulario"
        );
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setBackground(new Color(248, 248, 248));
        txtInstrucciones.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollInstrucciones = new JScrollPane(txtInstrucciones);
        scrollInstrucciones.setPreferredSize(new Dimension(0, 180));
        panelInstrucciones.add(scrollInstrucciones, BorderLayout.CENTER);

        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Premisa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        txtNombre.setToolTipText("Identificador único (sin espacios, ej: 'fiebre', 'temperatura')");
        panelFormulario.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setToolTipText("Pregunta que verá el cliente (ej: '¿Tiene fiebre?')");
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelFormulario.add(scrollDesc, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panelFormulario.add(new JLabel("Tipo de Dato:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbTipo = new JComboBox<>(Premisa.TipoDato.values());
        cmbTipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Premisa.TipoDato) {
                    Premisa.TipoDato tipo = (Premisa.TipoDato) value;
                    switch (tipo) {
                        case BOOLEANA:
                            setText("BOOLEANA - Para preguntas Sí/No (true/false)");
                            break;
                        case NUMERICA:
                            setText("NUMERICA - Para valores numéricos (enteros/decimales)");
                            break;
                        case STRING:
                            setText("STRING - Para texto libre");
                            break;
                    }
                }
                return this;
            }
        });
        panelFormulario.add(cmbTipo, gbc);

        // Valor por defecto
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Valor por Defecto:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtValorDefecto = new JTextField(20);
        actualizarTooltipValorDefecto();
        panelFormulario.add(txtValorDefecto, gbc);

        // Listener para actualizar tooltip según el tipo seleccionado
        cmbTipo.addActionListener(e -> actualizarTooltipValorDefecto());

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));

        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelInstrucciones, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarDatos(Premisa premisa) {
        txtNombre.setText(premisa.getNombre());
        txtDescripcion.setText(premisa.getDescripcion());
        cmbTipo.setSelectedItem(premisa.getTipo());
        txtValorDefecto.setText(premisa.getValorDefecto() != null ? premisa.getValorDefecto() : "");
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Premisa.TipoDato tipo = (Premisa.TipoDato) cmbTipo.getSelectedItem();
        String valorDefecto = txtValorDefecto.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (nombre.contains(" ")) {
            JOptionPane.showMessageDialog(this, "El nombre no puede contener espacios.\n" +
                "Use guiones bajos si es necesario (ej: 'dolor_cabeza')",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }

        // Validar valor por defecto según el tipo
        if (!valorDefecto.isEmpty()) {
            if (!validarValorDefecto(valorDefecto, tipo)) {
                return;
            }
        }

        // Crear o actualizar premisa
        if (premisaOriginal == null) {
            // Nueva premisa
            premisaCreada = new Premisa(nombre, descripcion, tipo);
            if (!valorDefecto.isEmpty()) {
                premisaCreada.setValorDefecto(valorDefecto);
            }
        } else {
            // Editar premisa existente
            premisaOriginal.setNombre(nombre);
            premisaOriginal.setDescripcion(descripcion);
            premisaOriginal.setTipo(tipo);
            premisaOriginal.setValorDefecto(valorDefecto.isEmpty() ? null : valorDefecto);
            premisaCreada = premisaOriginal;
        }

        JOptionPane.showMessageDialog(this,
            "Premisa " + (premisaOriginal == null ? "creada" : "actualizada") + " exitosamente",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private boolean validarValorDefecto(String valor, Premisa.TipoDato tipo) {
        switch (tipo) {
            case BOOLEANA:
                String valorLower = valor.toLowerCase();
                if (!valorLower.equals("true") && !valorLower.equals("false") &&
                    !valorLower.equals("sí") && !valorLower.equals("si") &&
                    !valorLower.equals("no")) {
                    JOptionPane.showMessageDialog(this,
                        "Para tipo BOOLEANA, use: true, false, sí, si, no",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    txtValorDefecto.requestFocus();
                    return false;
                }
                break;

            case NUMERICA:
                try {
                    Double.parseDouble(valor);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Para tipo NUMERICA, ingrese un número válido (ej: 36.5, 25, 100)",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    txtValorDefecto.requestFocus();
                    return false;
                }
                break;

            case STRING:
                // Cualquier valor es válido para STRING
                break;
        }
        return true;
    }

    public Premisa getPremisaCreada() {
        return premisaCreada;
    }

    private void actualizarTooltipValorDefecto() {
        Premisa.TipoDato tipo = (Premisa.TipoDato) cmbTipo.getSelectedItem();
        if (tipo != null) {
            String tooltip;
            switch (tipo) {
                case BOOLEANA:
                    tooltip = "Para preguntas Sí/No. Valores válidos: 'true', 'false', 'sí', 'no'";
                    break;
                case NUMERICA:
                    tooltip = "Para valores numéricos. Ejemplos: '25', '36.5', '100'";
                    break;
                case STRING:
                    tooltip = "Para texto libre. Ejemplo: 'Sin síntomas', 'Descripción del problema'";
                    break;
                default:
                    tooltip = "Valor inicial que aparecerá en el formulario (opcional)";
            }
            txtValorDefecto.setToolTipText(tooltip);
        }
    }
}