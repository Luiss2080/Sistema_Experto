package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import modelo.entidades.*;

/**
 * Formulario dinámico para entrada de datos del usuario
 */
public class FormularioEntradaDatos extends JDialog {

    // Variables declaration
    private BaseConocimiento baseConocimiento;
    private List<Hecho> hechos;
    private Map<Premisa, JComponent> camposPremisas;
    private Map<Premisa, JLabel> etiquetasEstado;

    private JPanel panelDatos;
    private JPanel panelBotones;
    private JPanel panelProgreso;
    private JProgressBar barraProgreso;
    private JLabel lblProgreso;
    private JButton btnSiguiente;
    private JButton btnAnterior;
    private JButton btnFinalizar;
    private JButton btnCancelar;
    private JButton btnLimpiarTodo;
    private JButton btnValidarTodo;

    private int indiceActual = 0;
    private boolean datosCompletos = false;

    public FormularioEntradaDatos(JFrame parent, BaseConocimiento baseConocimiento) {
        super(parent, "Entrada de Datos - " + baseConocimiento.getNombre(), true);
        this.baseConocimiento = baseConocimiento;
        this.hechos = new ArrayList<>();
        this.camposPremisas = new HashMap<>();
        this.etiquetasEstado = new HashMap<>();
        initComponents();
        actualizarProgreso();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(getParent());

        // Panel de progreso
        panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.setBorder(BorderFactory.createTitledBorder("Progreso de Entrada de Datos"));

        barraProgreso = new JProgressBar(0, baseConocimiento.getPremisas().size());
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("0 de " + baseConocimiento.getPremisas().size() + " premisas completadas");

        lblProgreso = new JLabel("Completa la información solicitada para cada premisa");
        lblProgreso.setHorizontalAlignment(SwingConstants.CENTER);

        panelProgreso.add(lblProgreso, BorderLayout.NORTH);
        panelProgreso.add(barraProgreso, BorderLayout.CENTER);

        // Panel principal de datos
        panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        JScrollPane scrollDatos = new JScrollPane(panelDatos);
        scrollDatos.setPreferredSize(new Dimension(650, 350));

        crearCamposPremisas();

        // Panel de botones
        panelBotones = new JPanel(new BorderLayout());

        // Botones de navegación
        JPanel panelNavegacion = new JPanel(new FlowLayout());
        btnAnterior = new JButton("← Anterior");
        btnSiguiente = new JButton("Siguiente →");
        btnAnterior.setEnabled(false);

        btnAnterior.addActionListener(e -> navegarAnterior());
        btnSiguiente.addActionListener(e -> navegarSiguiente());

        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(btnSiguiente);

        // Botones de acción
        JPanel panelAcciones = new JPanel(new FlowLayout());
        btnValidarTodo = new JButton("Validar Todo");
        btnValidarTodo.setIcon(createIcon("✓", Color.BLUE));
        btnValidarTodo.addActionListener(e -> validarTodosLosDatos());

        btnLimpiarTodo = new JButton("Limpiar Todo");
        btnLimpiarTodo.setIcon(createIcon("↻", Color.ORANGE));
        btnLimpiarTodo.addActionListener(e -> limpiarTodosLosDatos());

        btnFinalizar = new JButton("Finalizar");
        btnFinalizar.setIcon(createIcon("✓", Color.GREEN));
        btnFinalizar.addActionListener(e -> finalizar());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setIcon(createIcon("✗", Color.RED));
        btnCancelar.addActionListener(e -> dispose());

        panelAcciones.add(btnValidarTodo);
        panelAcciones.add(btnLimpiarTodo);
        panelAcciones.add(btnFinalizar);
        panelAcciones.add(btnCancelar);

        panelBotones.add(panelNavegacion, BorderLayout.WEST);
        panelBotones.add(panelAcciones, BorderLayout.EAST);

        // Layout principal
        setLayout(new BorderLayout());
        add(panelProgreso, BorderLayout.NORTH);
        add(scrollDatos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void crearCamposPremisas() {
        panelDatos.removeAll();

        for (int i = 0; i < baseConocimiento.getPremisas().size(); i++) {
            Premisa premisa = baseConocimiento.getPremisas().get(i);

            // Panel para cada premisa
            JPanel panelPremisa = new JPanel(new BorderLayout());
            panelPremisa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(i == indiceActual ? Color.BLUE : Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Información de la premisa
            JPanel panelInfo = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Número y nombre
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
            JLabel lblNumero = new JLabel((i + 1) + ".");
            lblNumero.setFont(new Font("Arial", Font.BOLD, 14));
            panelInfo.add(lblNumero, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel lblNombre = new JLabel(premisa.getNombre());
            lblNombre.setFont(new Font("Arial", Font.BOLD, 12));
            panelInfo.add(lblNombre, gbc);

            // Estado
            gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            JLabel lblEstado = new JLabel("⚪ Pendiente");
            lblEstado.setForeground(Color.GRAY);
            etiquetasEstado.put(premisa, lblEstado);
            panelInfo.add(lblEstado, gbc);

            // Descripción
            if (!premisa.getDescripcion().isEmpty()) {
                gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
                JLabel lblDescripcion = new JLabel("<html><i>" + premisa.getDescripcion() + "</i></html>");
                lblDescripcion.setForeground(Color.DARK_GRAY);
                panelInfo.add(lblDescripcion, gbc);
                gbc.gridwidth = 1;
            }

            // Tipo de dato
            gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
            JLabel lblTipo = new JLabel("Tipo: " + premisa.getTipo());
            lblTipo.setFont(new Font("Arial", Font.ITALIC, 10));
            lblTipo.setForeground(Color.BLUE);
            panelInfo.add(lblTipo, gbc);

            // Campo de entrada
            JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JComponent campo = crearCampoPremisa(premisa);
            camposPremisas.put(premisa, campo);

            panelCampo.add(new JLabel("Valor:"));
            panelCampo.add(campo);

            // Botón de validación individual
            JButton btnValidar = new JButton("Validar");
            btnValidar.setIcon(createIcon("✓", Color.GREEN));
            btnValidar.addActionListener(e -> validarPremisa(premisa));
            panelCampo.add(btnValidar);

            // Valor por defecto si existe
            if (premisa.getValorDefecto() != null && !premisa.getValorDefecto().isEmpty()) {
                JButton btnDefecto = new JButton("Usar Defecto");
                btnDefecto.setIcon(createIcon("⚙", Color.ORANGE));
                btnDefecto.addActionListener(e -> usarValorDefecto(premisa));
                panelCampo.add(btnDefecto);
            }

            panelPremisa.add(panelInfo, BorderLayout.NORTH);
            panelPremisa.add(panelCampo, BorderLayout.CENTER);

            // Hacer visible solo si es el índice actual o modo completo
            panelPremisa.setVisible(true);

            panelDatos.add(panelPremisa);
            panelDatos.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelDatos.revalidate();
        panelDatos.repaint();
    }

    private JComponent crearCampoPremisa(Premisa premisa) {
        switch (premisa.getTipo()) {
            case BOOLEANA:
                JComboBox<String> cmbBoolean = new JComboBox<>(new String[]{"", "true", "false"});
                cmbBoolean.addActionListener(e -> actualizarEstadoPremisa(premisa));
                return cmbBoolean;

            case NUMERICA:
                JPanel panelNumerico = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                JTextField txtNumerico = new JTextField(15);
                JSpinner spnNumerico = new JSpinner(new SpinnerNumberModel(0.0, -1000000.0, 1000000.0, 0.1));

                JCheckBox chkSpinner = new JCheckBox("Usar selector");
                chkSpinner.addActionListener(e -> {
                    boolean useSpinner = chkSpinner.isSelected();
                    txtNumerico.setVisible(!useSpinner);
                    spnNumerico.setVisible(useSpinner);
                    panelNumerico.revalidate();
                });

                txtNumerico.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                });

                spnNumerico.addChangeListener(e -> actualizarEstadoPremisa(premisa));

                spnNumerico.setVisible(false);
                panelNumerico.add(txtNumerico);
                panelNumerico.add(spnNumerico);
                panelNumerico.add(chkSpinner);
                return panelNumerico;

            case STRING:
            default:
                JTextField txtTexto = new JTextField(20);
                txtTexto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoPremisa(premisa); }
                });
                return txtTexto;
        }
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

    private void actualizarEstadoPremisa(Premisa premisa) {
        String valor = obtenerValorCampo(premisa);
        JLabel lblEstado = etiquetasEstado.get(premisa);

        if (valor == null || valor.trim().isEmpty()) {
            lblEstado.setText("⚪ Pendiente");
            lblEstado.setForeground(Color.GRAY);
        } else if (premisa.validarTipo(valor)) {
            lblEstado.setText("✓ Válido");
            lblEstado.setForeground(Color.GREEN);
        } else {
            lblEstado.setText("✗ Inválido");
            lblEstado.setForeground(Color.RED);
        }

        actualizarProgreso();
    }

    private void validarPremisa(Premisa premisa) {
        String valor = obtenerValorCampo(premisa);

        if (valor == null || valor.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar un valor para la premisa '" + premisa.getNombre() + "'",
                "Campo Vacío",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (premisa.validarTipo(valor)) {
            JOptionPane.showMessageDialog(this,
                "Valor válido para la premisa '" + premisa.getNombre() + "'",
                "Validación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            actualizarEstadoPremisa(premisa);
        } else {
            JOptionPane.showMessageDialog(this,
                "Valor inválido para el tipo " + premisa.getTipo() +
                "\nPremisa: " + premisa.getNombre(),
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void usarValorDefecto(Premisa premisa) {
        JComponent campo = camposPremisas.get(premisa);
        String valorDefecto = premisa.getValorDefecto();

        if (campo instanceof JComboBox) {
            ((JComboBox<String>) campo).setSelectedItem(valorDefecto);
        } else if (campo instanceof JTextField) {
            ((JTextField) campo).setText(valorDefecto);
        } else if (campo instanceof JPanel) {
            // Para campos numéricos
            JPanel panel = (JPanel) campo;
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText(valorDefecto);
                    break;
                } else if (comp instanceof JSpinner && comp.isVisible()) {
                    try {
                        ((JSpinner) comp).setValue(Double.parseDouble(valorDefecto));
                    } catch (NumberFormatException e) {
                        // Si no se puede parsear, usar el JTextField
                    }
                    break;
                }
            }
        }

        actualizarEstadoPremisa(premisa);
    }

    private String obtenerValorCampo(Premisa premisa) {
        JComponent campo = camposPremisas.get(premisa);

        if (campo instanceof JTextField) {
            return ((JTextField) campo).getText().trim();
        } else if (campo instanceof JComboBox) {
            Object selected = ((JComboBox<?>) campo).getSelectedItem();
            return selected != null ? selected.toString() : "";
        } else if (campo instanceof JPanel) {
            // Para campos numéricos
            JPanel panel = (JPanel) campo;
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JTextField && comp.isVisible()) {
                    return ((JTextField) comp).getText().trim();
                } else if (comp instanceof JSpinner && comp.isVisible()) {
                    return ((JSpinner) comp).getValue().toString();
                }
            }
        }

        return "";
    }

    private void navegarAnterior() {
        if (indiceActual > 0) {
            indiceActual--;
            actualizarNavegacion();
        }
    }

    private void navegarSiguiente() {
        if (indiceActual < baseConocimiento.getPremisas().size() - 1) {
            indiceActual++;
            actualizarNavegacion();
        }
    }

    private void actualizarNavegacion() {
        btnAnterior.setEnabled(indiceActual > 0);
        btnSiguiente.setEnabled(indiceActual < baseConocimiento.getPremisas().size() - 1);
        crearCamposPremisas(); // Actualizar el resaltado
    }

    private void actualizarProgreso() {
        int completadas = 0;
        for (Premisa premisa : baseConocimiento.getPremisas()) {
            String valor = obtenerValorCampo(premisa);
            if (valor != null && !valor.trim().isEmpty() && premisa.validarTipo(valor)) {
                completadas++;
            }
        }

        barraProgreso.setValue(completadas);
        barraProgreso.setString(completadas + " de " + baseConocimiento.getPremisas().size() + " premisas completadas");

        double porcentaje = (double) completadas / baseConocimiento.getPremisas().size() * 100;
        lblProgreso.setText(String.format("Progreso: %.1f%% completado", porcentaje));

        datosCompletos = (completadas == baseConocimiento.getPremisas().size());
        btnFinalizar.setEnabled(completadas > 0); // Permitir finalizar con datos parciales
    }

    private void validarTodosLosDatos() {
        StringBuilder resultado = new StringBuilder("RESULTADO DE VALIDACIÓN:\n\n");
        int validos = 0;
        int invalidos = 0;
        int vacios = 0;

        for (Premisa premisa : baseConocimiento.getPremisas()) {
            String valor = obtenerValorCampo(premisa);
            resultado.append("• ").append(premisa.getNombre()).append(": ");

            if (valor == null || valor.trim().isEmpty()) {
                resultado.append("VACÍO\n");
                vacios++;
            } else if (premisa.validarTipo(valor)) {
                resultado.append("VÁLIDO (").append(valor).append(")\n");
                validos++;
            } else {
                resultado.append("INVÁLIDO (").append(valor).append(")\n");
                invalidos++;
            }
        }

        resultado.append("\nRESUMEN:\n");
        resultado.append("✓ Válidos: ").append(validos).append("\n");
        resultado.append("✗ Inválidos: ").append(invalidos).append("\n");
        resultado.append("⚪ Vacíos: ").append(vacios).append("\n");

        JTextArea textArea = new JTextArea(resultado.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Validación de Datos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarTodosLosDatos() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea limpiar todos los datos ingresados?",
            "Confirmar Limpieza",
            JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            for (Premisa premisa : baseConocimiento.getPremisas()) {
                JComponent campo = camposPremisas.get(premisa);

                if (campo instanceof JTextField) {
                    ((JTextField) campo).setText("");
                } else if (campo instanceof JComboBox) {
                    ((JComboBox<?>) campo).setSelectedIndex(0);
                } else if (campo instanceof JPanel) {
                    JPanel panel = (JPanel) campo;
                    for (Component comp : panel.getComponents()) {
                        if (comp instanceof JTextField) {
                            ((JTextField) comp).setText("");
                        } else if (comp instanceof JSpinner) {
                            ((JSpinner) comp).setValue(0.0);
                        }
                    }
                }

                actualizarEstadoPremisa(premisa);
            }

            JOptionPane.showMessageDialog(this,
                "Todos los datos han sido limpiados",
                "Datos Limpiados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void finalizar() {
        // Recopilar todos los hechos válidos
        hechos.clear();

        for (Premisa premisa : baseConocimiento.getPremisas()) {
            String valor = obtenerValorCampo(premisa);

            if (valor != null && !valor.trim().isEmpty() && premisa.validarTipo(valor)) {
                Hecho hecho = new Hecho(premisa.getId(), premisa.getNombre(), valor);
                hecho.setFuente("usuario");
                hecho.setCerteza(1.0);
                hechos.add(hecho);
            }
        }

        if (hechos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar al menos un dato válido antes de continuar",
                "Sin Datos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar resumen antes de finalizar
        StringBuilder resumen = new StringBuilder("DATOS RECOPILADOS:\n\n");
        for (Hecho hecho : hechos) {
            resumen.append("• ").append(hecho.getPremisaNombre())
                   .append(" = ").append(hecho.getValor()).append("\n");
        }

        resumen.append("\n¿Desea continuar con estos datos?");

        int respuesta = JOptionPane.showConfirmDialog(this,
            resumen.toString(),
            "Confirmar Datos",
            JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public List<Hecho> getHechos() {
        return new ArrayList<>(hechos);
    }

    public boolean isDatosCompletos() {
        return datosCompletos;
    }
}