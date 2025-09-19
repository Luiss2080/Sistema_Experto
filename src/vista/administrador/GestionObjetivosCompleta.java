package vista.administrador;

import javax.swing.*;
import java.awt.*;
import modelo.entidades.*;

public class GestionObjetivosCompleta extends JDialog {
    private EditorBaseConocimiento parent;
    private BaseConocimiento baseConocimiento;
    private JList<Objetivo> listaObjetivos;
    private DefaultListModel<Objetivo> modeloLista;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtTipoRespuesta;
    private JTextArea txtExplicacion;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public GestionObjetivosCompleta(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
        super(parent, "Gestión de Objetivos", true);
        this.parent = parent;
        this.baseConocimiento = baseConocimiento;
        initComponents();
        cargarObjetivos();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel izquierdo - Lista de objetivos
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
                    setText("<html><b>" + objetivo.getNombre() + "</b><br/>" + 
                           "Tipo: " + objetivo.getTipoRespuesta() + "<br/>" + 
                           objetivo.getDescripcion() + "</html>");
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaObjetivos);
        scrollLista.setPreferredSize(new Dimension(300, 400));
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        // Panel derecho - Formulario
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Nuevo Objetivo"));
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
        gbc.weighty = 0.2;
        txtDescripcion = new JTextArea(2, 15);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelDerecho.add(scrollDesc, gbc);

        // Tipo de respuesta
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Tipo Respuesta:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTipoRespuesta = new JTextField(15);
        txtTipoRespuesta.setToolTipText("Ej: diagnóstico, recomendación, clasificación");
        panelDerecho.add(txtTipoRespuesta, gbc);

        // Explicación
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panelDerecho.add(new JLabel("Explicación:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtExplicacion = new JTextArea(3, 15);
        txtExplicacion.setLineWrap(true);
        txtExplicacion.setWrapStyleWord(true);
        txtExplicacion.setToolTipText("Descripción de cómo se alcanza este objetivo");
        JScrollPane scrollExpl = new JScrollPane(txtExplicacion);
        panelDerecho.add(scrollExpl, gbc);

        // Botones
        JPanel panelBotonesDerecho = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        btnAgregar.addActionListener(e -> agregarObjetivo());
        btnEliminar.addActionListener(e -> eliminarObjetivo());
        btnCerrar.addActionListener(e -> dispose());

        panelBotonesDerecho.add(btnAgregar);
        panelBotonesDerecho.add(btnEliminar);
        panelBotonesDerecho.add(btnCerrar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panelDerecho.add(panelBotonesDerecho, gbc);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void cargarObjetivos() {
        modeloLista.clear();
        if (baseConocimiento.getObjetivos() != null) {
            for (Objetivo objetivo : baseConocimiento.getObjetivos()) {
                modeloLista.addElement(objetivo);
            }
        }
    }

    private void agregarObjetivo() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipoRespuesta = txtTipoRespuesta.getText().trim();
        String explicacion = txtExplicacion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tipoRespuesta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El tipo de respuesta es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar que no exista un objetivo con el mismo nombre
        for (Objetivo obj : baseConocimiento.getObjetivos()) {
            if (obj.getNombre().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un objetivo con ese nombre", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Objetivo nuevoObjetivo = new Objetivo();
        nuevoObjetivo.setId(baseConocimiento.getObjetivos().size() + 1);
        nuevoObjetivo.setNombre(nombre);
        nuevoObjetivo.setDescripcion(descripcion);
        nuevoObjetivo.setTipoRespuesta(tipoRespuesta);
        nuevoObjetivo.setExplicacion(explicacion);

        baseConocimiento.getObjetivos().add(nuevoObjetivo);
        modeloLista.addElement(nuevoObjetivo);

        // Limpiar campos
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtTipoRespuesta.setText("");
        txtExplicacion.setText("");

        JOptionPane.showMessageDialog(this, "Objetivo agregado correctamente", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarObjetivo() {
        Objetivo seleccionado = listaObjetivos.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un objetivo", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el objetivo '" + seleccionado.getNombre() + "'?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            baseConocimiento.getObjetivos().remove(seleccionado);
            modeloLista.removeElement(seleccionado);
            JOptionPane.showMessageDialog(this, "Objetivo eliminado correctamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}