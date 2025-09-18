package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;

public class GestionObjetivos extends JDialog {
    private EditorBaseConocimiento parent;
    private BaseConocimiento baseConocimiento;
    private JList<Objetivo> listaObjetivos;
    private DefaultListModel<Objetivo> modeloLista;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtTipoRespuesta;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public GestionObjetivos(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
        super(parent, "Gestión de Objetivos", true);
        this.parent = parent;
        this.baseConocimiento = baseConocimiento;
        initComponents();
        cargarObjetivos();
    }

    private void initComponents() {
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Objetivos Existentes"));

        modeloLista = new DefaultListModel<>();
        listaObjetivos = new JList<>(modeloLista);
        listaObjetivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaObjetivos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Objetivo) {
                    Objetivo objetivo = (Objetivo) value;
                    setText(objetivo.getNombre());
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaObjetivos);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Nuevo Objetivo"));
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
        gbc.weighty = 0.5;
        txtDescripcion = new JTextArea(3, 15);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelDerecho.add(scrollDesc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Tipo Respuesta:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTipoRespuesta = new JTextField(15);
        panelDerecho.add(txtTipoRespuesta, gbc);

        JPanel panelBotonesDerecho = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarObjetivo();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarObjetivo();
            }
        });

        panelBotonesDerecho.add(btnAgregar);
        panelBotonesDerecho.add(btnEliminar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelDerecho.add(panelBotonesDerecho, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarObjetivos() {
        modeloLista.clear();
        for (Objetivo objetivo : baseConocimiento.getObjetivos()) {
            modeloLista.addElement(objetivo);
        }
    }

    private void agregarObjetivo() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipoRespuesta = txtTipoRespuesta.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Objetivo objetivo = new Objetivo(nombre, descripcion, tipoRespuesta);
        baseConocimiento.getObjetivos().add(objetivo);
        cargarObjetivos();

        txtNombre.setText("");
        txtDescripcion.setText("");
        txtTipoRespuesta.setText("");

        JOptionPane.showMessageDialog(this, "Objetivo agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarObjetivo() {
        Objetivo seleccionado = listaObjetivos.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un objetivo", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el objetivo: " + seleccionado.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getObjetivos().remove(seleccionado);
            cargarObjetivos();
            JOptionPane.showMessageDialog(this, "Objetivo eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}