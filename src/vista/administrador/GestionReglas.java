package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;

public class GestionReglas extends JDialog {
    private EditorBaseConocimiento parent;
    private BaseConocimiento baseConocimiento;
    private JList<Regla> listaReglas;
    private DefaultListModel<Regla> modeloLista;

    private JTextField txtNombre;
    private JTextArea txtCondiciones;
    private JTextArea txtAcciones;
    private JSpinner spnFactorCerteza;
    private JSpinner spnPrioridad;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public GestionReglas(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
        super(parent, "Gestión de Reglas", true);
        this.parent = parent;
        this.baseConocimiento = baseConocimiento;
        initComponents();
        cargarReglas();
    }

    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Reglas Existentes"));

        modeloLista = new DefaultListModel<>();
        listaReglas = new JList<>(modeloLista);
        listaReglas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaReglas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Regla) {
                    Regla regla = (Regla) value;
                    setText(regla.getNombre() + " (Prioridad: " + regla.getPrioridad() + ")");
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaReglas);
        scrollLista.setPreferredSize(new Dimension(800, 150));
        panelSuperior.add(scrollLista, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridBagLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Nueva Regla"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelInferior.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelInferior.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelInferior.add(new JLabel("Condiciones (IF):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtCondiciones = new JTextArea(3, 20);
        txtCondiciones.setLineWrap(true);
        txtCondiciones.setWrapStyleWord(true);
        JScrollPane scrollCondiciones = new JScrollPane(txtCondiciones);
        panelInferior.add(scrollCondiciones, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelInferior.add(new JLabel("Acciones (THEN):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtAcciones = new JTextArea(3, 20);
        txtAcciones.setLineWrap(true);
        txtAcciones.setWrapStyleWord(true);
        JScrollPane scrollAcciones = new JScrollPane(txtAcciones);
        panelInferior.add(scrollAcciones, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelInferior.add(new JLabel("Factor Certeza:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        spnFactorCerteza = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));
        panelInferior.add(spnFactorCerteza, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelInferior.add(new JLabel("Prioridad:"), gbc);

        gbc.gridx = 1;
        spnPrioridad = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        panelInferior.add(spnPrioridad, gbc);

        JPanel panelBotonesInferior = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar Regla");
        btnEliminar = new JButton("Eliminar Seleccionada");

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarRegla();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarRegla();
            }
        });

        panelBotonesInferior.add(btnAgregar);
        panelBotonesInferior.add(btnEliminar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelInferior.add(panelBotonesInferior, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelInferior, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarReglas() {
        modeloLista.clear();
        for (Regla regla : baseConocimiento.getReglas()) {
            modeloLista.addElement(regla);
        }
    }

    private void agregarRegla() {
        String nombre = txtNombre.getText().trim();
        String condicionesTexto = txtCondiciones.getText().trim();
        String accionesTexto = txtAcciones.getText().trim();
        double factorCerteza = (Double) spnFactorCerteza.getValue();
        int prioridad = (Integer) spnPrioridad.getValue();

        if (nombre.isEmpty() || condicionesTexto.isEmpty() || accionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Regla regla = new Regla(nombre);
        regla.setFactorCerteza(factorCerteza);
        regla.setPrioridad(prioridad);

        String[] condiciones = condicionesTexto.split("\n");
        for (String condicion : condiciones) {
            if (!condicion.trim().isEmpty()) {
                regla.agregarCondicion(condicion.trim());
            }
        }

        String[] acciones = accionesTexto.split("\n");
        for (String accion : acciones) {
            if (!accion.trim().isEmpty()) {
                regla.agregarAccion(accion.trim());
            }
        }

        baseConocimiento.agregarRegla(regla);
        cargarReglas();

        txtNombre.setText("");
        txtCondiciones.setText("");
        txtAcciones.setText("");
        spnFactorCerteza.setValue(1.0);
        spnPrioridad.setValue(1);

        JOptionPane.showMessageDialog(this, "Regla agregada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarRegla() {
        Regla seleccionada = listaReglas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una regla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la regla: " + seleccionada.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.eliminarRegla(seleccionada);
            cargarReglas();
            JOptionPane.showMessageDialog(this, "Regla eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}