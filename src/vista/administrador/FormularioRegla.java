package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import modelo.entidades.Regla;

public class FormularioRegla extends JDialog {
    private JFrame parent;
    private Regla reglaOriginal;
    private Regla reglaCreada;

    private JTextField txtNombre;
    private JTextArea txtCondiciones;
    private JTextArea txtAcciones;
    private JSpinner spnFactorCerteza;
    private JSpinner spnPrioridad;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnAyudaCondiciones;
    private JButton btnAyudaAcciones;

    public FormularioRegla(JFrame parent, Regla regla) {
        super(parent, regla == null ? "Agregar Nueva Regla" : "Editar Regla", true);
        this.parent = parent;
        this.reglaOriginal = regla;
        this.reglaCreada = null;

        initComponents();
        if (regla != null) {
            cargarDatos(regla);
        }
    }

    private void initComponents() {
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de instrucciones
        JPanel panelInstrucciones = new JPanel(new BorderLayout());
        JTextArea txtInstrucciones = new JTextArea(
            "GUÍA PASO A PASO PARA CREAR REGLAS:\n\n" +
            "1. NOMBRE: Descripción clara del propósito (ej: 'Diagnóstico Gripe')\n\n" +
            "2. CONDICIONES (IF) - Una condición por línea:\n" +
            "   • Use nombres EXACTOS de premisas creadas\n" +
            "   • Booleanas: 'fiebre = true' o 'fiebre = false'\n" +
            "   • Numéricas: 'temperatura > 38', 'edad >= 18'\n" +
            "   • Texto: 'sintoma = dolor'\n" +
            "   • IMPORTANTE: Espacios alrededor de operadores\n\n" +
            "3. ACCIONES (THEN) - Una acción por línea:\n" +
            "   • Use nombres EXACTOS de objetivos creados\n" +
            "   • Asigne valores: 'gripe = true', 'riesgo = alto'\n\n" +
            "4. CERTEZA: 0.1 (poco segura) a 1.0 (completamente segura)\n" +
            "5. PRIORIDAD: 1 (baja) a 10 (alta)"
        );
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setBackground(new Color(245, 245, 245));
        txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelEjemplos = new JPanel();
        JButton btnEjemplos = new JButton("Ver Ejemplos");
        btnEjemplos.addActionListener(e -> mostrarEjemplos());
        panelEjemplos.add(btnEjemplos);

        panelInstrucciones.add(txtInstrucciones, BorderLayout.CENTER);
        panelInstrucciones.add(panelEjemplos, BorderLayout.SOUTH);

        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Regla"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(30);
        txtNombre.setToolTipText("Nombre descriptivo de la regla (ej: 'Diagnóstico Gripe')");
        panelFormulario.add(txtNombre, gbc);

        // Condiciones (IF)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Condiciones (IF):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnAyudaCondiciones = new JButton("Ayuda");
        btnAyudaCondiciones.addActionListener(e -> mostrarAyudaCondiciones());
        panelFormulario.add(btnAyudaCondiciones, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        txtCondiciones = new JTextArea(6, 50);
        txtCondiciones.setLineWrap(true);
        txtCondiciones.setWrapStyleWord(true);
        txtCondiciones.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtCondiciones.setToolTipText("Una condición por línea. Ej:\nfiebre = true\ntemperatura > 38");
        JScrollPane scrollCond = new JScrollPane(txtCondiciones);
        scrollCond.setBorder(BorderFactory.createTitledBorder("Escriba una condición por línea"));
        panelFormulario.add(scrollCond, gbc);

        // Acciones (THEN)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panelFormulario.add(new JLabel("Acciones (THEN):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnAyudaAcciones = new JButton("Ayuda");
        btnAyudaAcciones.addActionListener(e -> mostrarAyudaAcciones());
        panelFormulario.add(btnAyudaAcciones, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        txtAcciones = new JTextArea(4, 50);
        txtAcciones.setLineWrap(true);
        txtAcciones.setWrapStyleWord(true);
        txtAcciones.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtAcciones.setToolTipText("Una acción por línea. Ej:\ngripe = true\nrecomendacion = descanso");
        JScrollPane scrollAcc = new JScrollPane(txtAcciones);
        scrollAcc.setBorder(BorderFactory.createTitledBorder("Escriba una acción por línea"));
        panelFormulario.add(scrollAcc, gbc);

        // Factor de Certeza
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panelFormulario.add(new JLabel("Factor de Certeza:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        spnFactorCerteza = new JSpinner(new SpinnerNumberModel(0.85, 0.0, 1.0, 0.05));
        spnFactorCerteza.setToolTipText("Confianza en la regla (0.0 - 1.0). Mayor valor = mayor confianza");
        panelFormulario.add(spnFactorCerteza, gbc);

        // Prioridad
        gbc.gridx = 0; gbc.gridy = 6;
        panelFormulario.add(new JLabel("Prioridad:"), gbc);

        gbc.gridx = 1;
        spnPrioridad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnPrioridad.setToolTipText("Prioridad de aplicación (1-100). Mayor valor = mayor prioridad");
        panelFormulario.add(spnPrioridad, gbc);

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

    private void cargarDatos(Regla regla) {
        txtNombre.setText(regla.getNombre());
        txtCondiciones.setText(String.join("\n", regla.getCondiciones()));
        txtAcciones.setText(String.join("\n", regla.getAcciones()));
        spnFactorCerteza.setValue(regla.getFactorCerteza());
        spnPrioridad.setValue(regla.getPrioridad());
    }

    private void mostrarEjemplos() {
        String ejemplos =
            "EJEMPLOS DE REGLAS COMPLETAS\n\n" +
            "═══ EJEMPLO 1: Diagnóstico Médico ═══\n" +
            "Nombre: Diagnóstico Gripe\n\n" +
            "Condiciones (IF):\n" +
            "fiebre = true\n" +
            "temperatura > 38\n\n" +
            "Acciones (THEN):\n" +
            "gripe = true\n\n" +
            "═══ EJEMPLO 2: Recomendación ═══\n" +
            "Nombre: Ejercicio para Jóvenes\n\n" +
            "Condiciones (IF):\n" +
            "edad < 30\n" +
            "experiencia = true\n\n" +
            "Acciones (THEN):\n" +
            "ejercicio = intenso\n" +
            "duracion = 60\n\n" +
            "═══ EJEMPLO 3: Control de Calidad ═══\n" +
            "Nombre: Producto Defectuoso\n\n" +
            "Condiciones (IF):\n" +
            "temperatura >= 200\n" +
            "presion <= 5\n\n" +
            "Acciones (THEN):\n" +
            "calidad = defectuoso\n" +
            "accion = rechazar";

        JTextArea areaEjemplos = new JTextArea(ejemplos);
        areaEjemplos.setEditable(false);
        areaEjemplos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JDialog dialogEjemplos = new JDialog(this, "Ejemplos de Reglas", true);
        dialogEjemplos.add(new JScrollPane(areaEjemplos));
        dialogEjemplos.setSize(500, 600);
        dialogEjemplos.setLocationRelativeTo(this);
        dialogEjemplos.setVisible(true);
    }

    private void mostrarAyudaCondiciones() {
        String ayuda =
            "AYUDA PARA CONDICIONES\n\n" +
            "Formato: premisa operador valor\n\n" +
            "OPERADORES DISPONIBLES:\n" +
            "= (igual): fiebre = true\n" +
            "> (mayor): temperatura > 38\n" +
            "< (menor): edad < 65\n" +
            ">= (mayor o igual): temperatura >= 37.5\n" +
            "<= (menor o igual): edad <= 80\n\n" +
            "VALORES BOOLEANOS:\n" +
            "true, false, sí, si, no\n\n" +
            "VALORES NUMÉRICOS:\n" +
            "25, 38.5, 100\n\n" +
            "¡IMPORTANTE!\n" +
            "[CORRECTO]: fiebre = true\n" +
            "[INCORRECTO]: fiebre=true (sin espacios)";

        JOptionPane.showMessageDialog(this, ayuda, "Ayuda - Condiciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAyudaAcciones() {
        String ayuda =
            "AYUDA PARA ACCIONES\n\n" +
            "Formato: objetivo = valor\n\n" +
            "EJEMPLOS:\n" +
            "gripe = true\n" +
            "recomendacion = descanso\n" +
            "nivel_riesgo = alto\n" +
            "ejercicio = caminar\n\n" +
            "TIPOS DE VALORES:\n" +
            "• Booleanos: true, false\n" +
            "• Textuales: descanso, caminar, alto\n" +
            "• Numéricos: 30, 60, 100\n\n" +
            "¡IMPORTANTE!\n" +
            "[CORRECTO]: gripe = true\n" +
            "[INCORRECTO]: gripe=true (sin espacios)";

        JOptionPane.showMessageDialog(this, ayuda, "Ayuda - Acciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String condicionesTexto = txtCondiciones.getText().trim();
        String accionesTexto = txtAcciones.getText().trim();
        double factorCerteza = (Double) spnFactorCerteza.getValue();
        int prioridad = (Integer) spnPrioridad.getValue();

        // Validaciones básicas
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (condicionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe definir al menos una condición",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtCondiciones.requestFocus();
            return;
        }

        if (accionesTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe definir al menos una acción",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtAcciones.requestFocus();
            return;
        }

        // Procesar condiciones
        ArrayList<String> condiciones = new ArrayList<>();
        String[] lineasCondiciones = condicionesTexto.split("\n");
        for (String linea : lineasCondiciones) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                if (!validarCondicion(linea)) {
                    return;
                }
                condiciones.add(linea);
            }
        }

        // Procesar acciones
        ArrayList<String> acciones = new ArrayList<>();
        String[] lineasAcciones = accionesTexto.split("\n");
        for (String linea : lineasAcciones) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                if (!validarAccion(linea)) {
                    return;
                }
                acciones.add(linea);
            }
        }

        // Crear o actualizar regla
        if (reglaOriginal == null) {
            // Nueva regla
            reglaCreada = new Regla(nombre);
            reglaCreada.setCondiciones(condiciones);
            reglaCreada.setAcciones(acciones);
            reglaCreada.setFactorCerteza(factorCerteza);
            reglaCreada.setPrioridad(prioridad);
        } else {
            // Editar regla existente
            reglaOriginal.setNombre(nombre);
            reglaOriginal.setCondiciones(condiciones);
            reglaOriginal.setAcciones(acciones);
            reglaOriginal.setFactorCerteza(factorCerteza);
            reglaOriginal.setPrioridad(prioridad);
            reglaCreada = reglaOriginal;
        }

        JOptionPane.showMessageDialog(this,
            "Regla " + (reglaOriginal == null ? "creada" : "actualizada") + " exitosamente",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private boolean validarCondicion(String condicion) {
        String[] operadores = {" >= ", " <= ", " = ", " > ", " < "};
        boolean operadorEncontrado = false;

        for (String op : operadores) {
            if (condicion.contains(op)) {
                operadorEncontrado = true;
                String[] partes = condicion.split(op, 2);
                if (partes.length != 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Formato incorrecto en condición: " + condicion + "\n\n" +
                        "Formato correcto: premisa operador valor\n" +
                        "Ejemplo: fiebre = true",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    txtCondiciones.requestFocus();
                    return false;
                }
                break;
            }
        }

        if (!operadorEncontrado) {
            JOptionPane.showMessageDialog(this,
                "Operador no válido en condición: " + condicion + "\n\n" +
                "Operadores válidos: =, >, <, >=, <=\n" +
                "¡IMPORTANTE! Usar espacios: 'fiebre = true'",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtCondiciones.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarAccion(String accion) {
        if (!accion.contains(" = ")) {
            JOptionPane.showMessageDialog(this,
                "Formato incorrecto en acción: " + accion + "\n\n" +
                "Formato correcto: objetivo = valor\n" +
                "Ejemplo: gripe = true",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtAcciones.requestFocus();
            return false;
        }

        String[] partes = accion.split(" = ", 2);
        if (partes.length != 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Acción incompleta: " + accion + "\n\n" +
                "Debe especificar tanto el objetivo como el valor",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtAcciones.requestFocus();
            return false;
        }

        return true;
    }

    public Regla getReglaCreada() {
        return reglaCreada;
    }
}