package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;

public class GestionPremisas extends JDialog {
    private EditorBaseConocimiento parent;
    private BaseConocimiento baseConocimiento;
    private JList<Premisa> listaPremisas;
    private DefaultListModel<Premisa> modeloLista;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<Premisa.TipoDato> cmbTipo;
    private JTextField txtValorDefecto;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public GestionPremisas(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
        super(parent, "Gestión de Premisas", true);
        this.parent = parent;
        this.baseConocimiento = baseConocimiento;
        initComponents();
        cargarPremisas();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Premisas Existentes"));

        modeloLista = new DefaultListModel<>();
        listaPremisas = new JList<>(modeloLista);
        listaPremisas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPremisas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Premisa) {
                    Premisa premisa = (Premisa) value;
                    setText(premisa.getNombre() + " (" + premisa.getTipo() + ")");
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaPremisas);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Nueva Premisa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelDerecho.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(15);
        panelDerecho.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelDerecho.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtDescripcion = new JTextArea(3, 15);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelDerecho.add(scrollDesc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipo = new JComboBox<>(Premisa.TipoDato.values());
        panelDerecho.add(cmbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelDerecho.add(new JLabel("Valor por Defecto:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtValorDefecto = new JTextField(15);
        panelDerecho.add(txtValorDefecto, gbc);

        JPanel panelBotonesDerecho = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar Seleccionada");

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarPremisa();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPremisa();
            }
        });

        panelBotonesDerecho.add(btnAgregar);
        panelBotonesDerecho.add(btnEliminar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelDerecho.add(panelBotonesDerecho, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarPremisas() {
        modeloLista.clear();
        for (Premisa premisa : baseConocimiento.getPremisas()) {
            modeloLista.addElement(premisa);
        }
    }

    private void agregarPremisa() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Premisa.TipoDato tipo = (Premisa.TipoDato) cmbTipo.getSelectedItem();
        String valorDefecto = txtValorDefecto.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Premisa premisa = new Premisa(nombre, descripcion, tipo);
        if (!valorDefecto.isEmpty()) {
            if (premisa.validarTipo(valorDefecto)) {
                premisa.setValorDefecto(valorDefecto);
            } else {
                JOptionPane.showMessageDialog(this, "El valor por defecto no es válido para el tipo seleccionado",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        baseConocimiento.getPremisas().add(premisa);
        cargarPremisas();

        txtNombre.setText("");
        txtDescripcion.setText("");
        txtValorDefecto.setText("");
        cmbTipo.setSelectedIndex(0);

        JOptionPane.showMessageDialog(this, "Premisa agregada exitosamente",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarPremisa() {
        Premisa seleccionada = listaPremisas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una premisa",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la premisa: " + seleccionada.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getPremisas().remove(seleccionada);
            cargarPremisas();
            JOptionPane.showMessageDialog(this, "Premisa eliminada exitosamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}