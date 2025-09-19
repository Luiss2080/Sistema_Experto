package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;

public class GestionPremisasCompleta extends JDialog {
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

    public GestionPremisasCompleta(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
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

        // Panel izquierdo - Lista de premisas
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
                    setText("<html><b>" + premisa.getNombre() + "</b><br/>" + 
                           "Tipo: " + premisa.getTipo() + "<br/>" + 
                           "Valor defecto: " + (premisa.getValorDefecto() != null ? premisa.getValorDefecto() : "N/A") + 
                           "</html>");
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaPremisas);
        scrollLista.setPreferredSize(new Dimension(300, 400));
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        // Panel derecho - Formulario
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Nueva Premisa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelDerecho.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(15);
        panelDerecho.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
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

        // Tipo
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipo = new JComboBox<>(Premisa.TipoDato.values());
        panelDerecho.add(cmbTipo, gbc);

        // Valor por defecto
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelDerecho.add(new JLabel("Valor por Defecto:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtValorDefecto = new JTextField(15);
        panelDerecho.add(txtValorDefecto, gbc);

        // Botones
        JPanel panelBotonesDerecho = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        btnAgregar.addActionListener(e -> agregarPremisa());
        btnEliminar.addActionListener(e -> eliminarPremisa());
        btnCerrar.addActionListener(e -> dispose());

        panelBotonesDerecho.add(btnAgregar);
        panelBotonesDerecho.add(btnEliminar);
        panelBotonesDerecho.add(btnCerrar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelDerecho.add(panelBotonesDerecho, gbc);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void cargarPremisas() {
        modeloLista.clear();
        if (baseConocimiento.getPremisas() != null) {
            for (Premisa premisa : baseConocimiento.getPremisas()) {
                modeloLista.addElement(premisa);
            }
        }
    }

    private void agregarPremisa() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Premisa.TipoDato tipo = (Premisa.TipoDato) cmbTipo.getSelectedItem();
        String valorDefecto = txtValorDefecto.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar que no exista una premisa con el mismo nombre
        for (Premisa p : baseConocimiento.getPremisas()) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe una premisa con ese nombre", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Validar valor por defecto según el tipo
        if (!valorDefecto.isEmpty() && !validarValorSegunTipo(valorDefecto, tipo)) {
            JOptionPane.showMessageDialog(this, "El valor por defecto no es válido para el tipo seleccionado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Premisa nuevaPremisa = new Premisa();
        nuevaPremisa.setId(baseConocimiento.getPremisas().size() + 1);
        nuevaPremisa.setNombre(nombre);
        nuevaPremisa.setDescripcion(descripcion);
        nuevaPremisa.setTipo(tipo);
        nuevaPremisa.setValorDefecto(valorDefecto);

        baseConocimiento.getPremisas().add(nuevaPremisa);
        modeloLista.addElement(nuevaPremisa);

        // Limpiar campos
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtValorDefecto.setText("");
        cmbTipo.setSelectedIndex(0);

        JOptionPane.showMessageDialog(this, "Premisa agregada correctamente", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validarValorSegunTipo(String valor, Premisa.TipoDato tipo) {
        try {
            switch (tipo) {
                case BOOLEANA:
                    return valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false") ||
                           valor.equalsIgnoreCase("sí") || valor.equalsIgnoreCase("no") ||
                           valor.equalsIgnoreCase("verdadero") || valor.equalsIgnoreCase("falso");
                case NUMERICA:
                    Double.parseDouble(valor);
                    return true;
                case STRING:
                    return true; // Cualquier string es válido
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void eliminarPremisa() {
        Premisa seleccionada = listaPremisas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una premisa", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar la premisa '" + seleccionada.getNombre() + "'?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            baseConocimiento.getPremisas().remove(seleccionada);
            modeloLista.removeElement(seleccionada);
            JOptionPane.showMessageDialog(this, "Premisa eliminada correctamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}