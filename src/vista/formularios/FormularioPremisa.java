package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Premisa;

/**
 * Formulario para crear/editar premisas de forma intuitiva
 */
public class FormularioPremisa extends JDialog {

    // Variables declaration
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<Premisa.TipoDato> cmbTipo;
    private JTextField txtValorDefecto;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JPanel panelPrincipal;
    private JPanel panelBotones;

    private Premisa premisa;
    private boolean guardado = false;

    public FormularioPremisa(JFrame parent, Premisa premisa) {
        super(parent, "Formulario de Premisa", true);
        this.premisa = premisa;
        initComponents();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(getParent());

        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Datos de la Premisa"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelPrincipal.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelPrincipal.add(scrollDesc, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        panelPrincipal.add(new JLabel("Tipo de Dato:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbTipo = new JComboBox<>(Premisa.TipoDato.values());
        panelPrincipal.add(cmbTipo, gbc);

        // Valor por defecto
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Valor por Defecto:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtValorDefecto = new JTextField(20);
        panelPrincipal.add(txtValorDefecto, gbc);

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
        if (premisa != null) {
            txtNombre.setText(premisa.getNombre());
            txtDescripcion.setText(premisa.getDescripcion());
            cmbTipo.setSelectedItem(premisa.getTipo());
            txtValorDefecto.setText(premisa.getValorDefecto());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Premisa.TipoDato tipo = (Premisa.TipoDato) cmbTipo.getSelectedItem();
        String valorDefecto = txtValorDefecto.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (!valorDefecto.isEmpty()) {
            Premisa temp = new Premisa();
            temp.setTipo(tipo);
            if (!temp.validarTipo(valorDefecto)) {
                JOptionPane.showMessageDialog(this,
                    "El valor por defecto no es válido para el tipo seleccionado",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                txtValorDefecto.requestFocus();
                return;
            }
        }

        // Guardar datos
        if (premisa == null) {
            premisa = new Premisa();
        }

        premisa.setNombre(nombre);
        premisa.setDescripcion(descripcion);
        premisa.setTipo(tipo);
        premisa.setValorDefecto(valorDefecto);

        guardado = true;

        JOptionPane.showMessageDialog(this,
            "Premisa guardada exitosamente",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cmbTipo.setSelectedIndex(0);
        txtValorDefecto.setText("");
        txtNombre.requestFocus();
    }

    public Premisa getPremisa() {
        return guardado ? premisa : null;
    }

    public boolean isGuardado() {
        return guardado;
    }
}