package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Hecho;
import modelo.entidades.Premisa;
import java.util.List;

/**
 * Formulario para crear/editar hechos de forma intuitiva
 */
public class FormularioHecho extends JDialog {

    // Variables declaration
    private JComboBox<Premisa> cmbPremisa;
    private JTextField txtValor;
    private JSlider sliderCerteza;
    private JLabel lblCerteza;
    private JComboBox<String> cmbFuente;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnValidar;
    private JPanel panelPrincipal;
    private JPanel panelBotones;

    private Hecho hecho;
    private List<Premisa> premisasDisponibles;
    private boolean guardado = false;

    public FormularioHecho(JFrame parent, Hecho hecho, List<Premisa> premisas) {
        super(parent, "Formulario de Hecho", true);
        this.hecho = hecho;
        this.premisasDisponibles = premisas;
        initComponents();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(getParent());

        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Datos del Hecho"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Premisa
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Premisa:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbPremisa = new JComboBox<>();
        for (Premisa p : premisasDisponibles) {
            cmbPremisa.addItem(p);
        }
        cmbPremisa.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Premisa) {
                    Premisa p = (Premisa) value;
                    setText(p.getNombre() + " (" + p.getTipo() + ")");
                }
                return this;
            }
        });
        panelPrincipal.add(cmbPremisa, gbc);

        // Valor
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Valor:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtValor = new JTextField(20);
        panelPrincipal.add(txtValor, gbc);

        // Botón validar
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        btnValidar = new JButton("Validar");
        btnValidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarValor();
            }
        });
        panelPrincipal.add(btnValidar, gbc);

        // Certeza
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Certeza:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        sliderCerteza = new JSlider(0, 100, 100);
        sliderCerteza.setMajorTickSpacing(25);
        sliderCerteza.setMinorTickSpacing(5);
        sliderCerteza.setPaintTicks(true);
        sliderCerteza.setPaintLabels(true);
        sliderCerteza.addChangeListener(e -> {
            lblCerteza.setText(sliderCerteza.getValue() + "%");
        });
        panelPrincipal.add(sliderCerteza, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        lblCerteza = new JLabel("100%");
        lblCerteza.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblCerteza, gbc);

        // Fuente
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Fuente:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbFuente = new JComboBox<>(new String[]{"usuario", "inferencia", "sistema", "experto"});
        panelPrincipal.add(cmbFuente, gbc);

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

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setIcon(createIcon("✗", Color.RED));
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnGuardar);
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
        if (hecho != null) {
            // Buscar y seleccionar la premisa
            for (int i = 0; i < cmbPremisa.getItemCount(); i++) {
                Premisa p = cmbPremisa.getItemAt(i);
                if (p.getNombre().equals(hecho.getPremisaNombre())) {
                    cmbPremisa.setSelectedIndex(i);
                    break;
                }
            }

            txtValor.setText(hecho.getValor());
            sliderCerteza.setValue((int) (hecho.getCerteza() * 100));
            lblCerteza.setText((int) (hecho.getCerteza() * 100) + "%");
            cmbFuente.setSelectedItem(hecho.getFuente());
        }
    }

    private void validarValor() {
        Premisa premisaSeleccionada = (Premisa) cmbPremisa.getSelectedItem();
        String valor = txtValor.getText().trim();

        if (premisaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una premisa",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar un valor",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean valido = premisaSeleccionada.validarTipo(valor);

        if (valido) {
            JOptionPane.showMessageDialog(this,
                "Valor válido para la premisa seleccionada",
                "Validación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            txtValor.setBackground(Color.WHITE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Valor no válido para el tipo " + premisaSeleccionada.getTipo(),
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtValor.setBackground(Color.PINK);
            txtValor.requestFocus();
        }
    }

    private void guardar() {
        Premisa premisaSeleccionada = (Premisa) cmbPremisa.getSelectedItem();
        String valor = txtValor.getText().trim();
        double certeza = sliderCerteza.getValue() / 100.0;
        String fuente = (String) cmbFuente.getSelectedItem();

        // Validaciones
        if (premisaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una premisa",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar un valor",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtValor.requestFocus();
            return;
        }

        if (!premisaSeleccionada.validarTipo(valor)) {
            JOptionPane.showMessageDialog(this,
                "El valor no es válido para el tipo " + premisaSeleccionada.getTipo(),
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtValor.requestFocus();
            return;
        }

        // Guardar datos
        if (hecho == null) {
            hecho = new Hecho();
        }

        hecho.setPremisaId(premisaSeleccionada.getId());
        hecho.setPremisaNombre(premisaSeleccionada.getNombre());
        hecho.setValor(valor);
        hecho.setCerteza(certeza);
        hecho.setFuente(fuente);

        guardado = true;

        JOptionPane.showMessageDialog(this,
            "Hecho guardado exitosamente",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public Hecho getHecho() {
        return guardado ? hecho : null;
    }

    public boolean isGuardado() {
        return guardado;
    }
}