package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import modelo.entidades.*;

public class GestionReglasCompleta extends JDialog {
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

    public GestionReglasCompleta(EditorBaseConocimiento parent, BaseConocimiento baseConocimiento) {
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

        // Panel izquierdo - Lista de reglas
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Reglas Existentes"));

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
                    setText("<html><b>" + regla.getNombre() + "</b><br/>" + 
                           "Prioridad: " + regla.getPrioridad() + " | Certeza: " + regla.getFactorCerteza() + "<br/>" + 
                           "Condiciones: " + regla.getCondiciones().size() + " | Acciones: " + regla.getAcciones().size() +
                           "</html>");
                }
                return this;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaReglas);
        scrollLista.setPreferredSize(new Dimension(350, 500));
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        // Panel derecho - Formulario
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Nueva Regla"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelDerecho.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelDerecho.add(txtNombre, gbc);

        // Condiciones (IF)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelDerecho.add(new JLabel("Condiciones (IF):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtCondiciones = new JTextArea(4, 20);
        txtCondiciones.setLineWrap(true);
        txtCondiciones.setWrapStyleWord(true);
        txtCondiciones.setToolTipText("Una condición por línea. Ej: fiebre = true, temperatura > 38");
        JScrollPane scrollCond = new JScrollPane(txtCondiciones);
        panelDerecho.add(scrollCond, gbc);

        // Acciones (THEN)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Acciones (THEN):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtAcciones = new JTextArea(4, 20);
        txtAcciones.setLineWrap(true);
        txtAcciones.setWrapStyleWord(true);
        txtAcciones.setToolTipText("Una acción por línea. Ej: diagnostico = gripe, recomendar = reposo");
        JScrollPane scrollAcc = new JScrollPane(txtAcciones);
        panelDerecho.add(scrollAcc, gbc);

        // Factor de certeza
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        panelDerecho.add(new JLabel("Factor Certeza:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        spnFactorCerteza = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));
        spnFactorCerteza.setToolTipText("Valor entre 0.0 y 1.0");
        panelDerecho.add(spnFactorCerteza, gbc);

        // Prioridad
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panelDerecho.add(new JLabel("Prioridad:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        spnPrioridad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnPrioridad.setToolTipText("Prioridad de la regla (1 = baja, 100 = alta)");
        panelDerecho.add(spnPrioridad, gbc);

        // Panel de ayuda
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.2;
        
        JTextArea txtAyuda = new JTextArea();
        txtAyuda.setText("AYUDA:\n" +
                        "Condiciones: Use '=' para igualdad, '>' y '<' para comparaciones\n" +
                        "Ejemplo: temperatura > 38, dolor_cabeza = true\n\n" +
                        "Acciones: Defina qué hechos se establecen\n" +
                        "Ejemplo: diagnostico = gripe, severidad = leve");
        txtAyuda.setEditable(false);
        txtAyuda.setBackground(getBackground());
        txtAyuda.setBorder(BorderFactory.createTitledBorder("Ayuda"));
        txtAyuda.setFont(txtAyuda.getFont().deriveFont(12f));
        panelDerecho.add(txtAyuda, gbc);

        // Botones
        JPanel panelBotonesDerecho = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        btnAgregar.addActionListener(e -> agregarRegla());
        btnEliminar.addActionListener(e -> eliminarRegla());
        btnCerrar.addActionListener(e -> dispose());

        panelBotonesDerecho.add(btnAgregar);
        panelBotonesDerecho.add(btnEliminar);
        panelBotonesDerecho.add(btnCerrar);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panelDerecho.add(panelBotonesDerecho, gbc);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void cargarReglas() {
        modeloLista.clear();
        if (baseConocimiento.getReglas() != null) {
            for (Regla regla : baseConocimiento.getReglas()) {
                modeloLista.addElement(regla);
            }
        }
    }

    private void agregarRegla() {
        String nombre = txtNombre.getText().trim();
        String condicionesTexto = txtCondiciones.getText().trim();
        String accionesTexto = txtAcciones.getText().trim();
        double factorCerteza = (Double) spnFactorCerteza.getValue();
        int prioridad = (Integer) spnPrioridad.getValue();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (condicionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar al menos una condición", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (accionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar al menos una acción", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar que no exista una regla con el mismo nombre
        for (Regla r : baseConocimiento.getReglas()) {
            if (r.getNombre().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe una regla con ese nombre", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Crear la nueva regla
        Regla nuevaRegla = new Regla();
        nuevaRegla.setId(baseConocimiento.getReglas().size() + 1);
        nuevaRegla.setNombre(nombre);
        nuevaRegla.setFactorCerteza(factorCerteza);
        nuevaRegla.setPrioridad(prioridad);

        // Procesar condiciones
        ArrayList<String> condiciones = new ArrayList<>();
        String[] lineasCondiciones = condicionesTexto.split("\n");
        for (String linea : lineasCondiciones) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                condiciones.add(linea);
            }
        }
        nuevaRegla.setCondiciones(condiciones);

        // Procesar acciones
        ArrayList<String> acciones = new ArrayList<>();
        String[] lineasAcciones = accionesTexto.split("\n");
        for (String linea : lineasAcciones) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                acciones.add(linea);
            }
        }
        nuevaRegla.setAcciones(acciones);

        // Validar sintaxis básica
        if (!validarSintaxis(condiciones, acciones)) {
            return;
        }

        baseConocimiento.getReglas().add(nuevaRegla);
        modeloLista.addElement(nuevaRegla);

        // Limpiar campos
        txtNombre.setText("");
        txtCondiciones.setText("");
        txtAcciones.setText("");
        spnFactorCerteza.setValue(1.0);
        spnPrioridad.setValue(1);

        JOptionPane.showMessageDialog(this, "Regla agregada correctamente", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validarSintaxis(ArrayList<String> condiciones, ArrayList<String> acciones) {
        // Validar condiciones
        for (String condicion : condiciones) {
            if (!condicion.contains("=") && !condicion.contains(">") && !condicion.contains("<")) {
                JOptionPane.showMessageDialog(this, 
                    "Condición mal formada: '" + condicion + "'\nDebe contener =, > o <", 
                    "Error de Sintaxis", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Validar acciones
        for (String accion : acciones) {
            if (!accion.contains("=")) {
                JOptionPane.showMessageDialog(this, 
                    "Acción mal formada: '" + accion + "'\nDebe contener =", 
                    "Error de Sintaxis", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void eliminarRegla() {
        Regla seleccionada = listaReglas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una regla", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar la regla '" + seleccionada.getNombre() + "'?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            baseConocimiento.getReglas().remove(seleccionada);
            modeloLista.removeElement(seleccionada);
            JOptionPane.showMessageDialog(this, "Regla eliminada correctamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}