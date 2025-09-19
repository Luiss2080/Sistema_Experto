package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Objetivo;

public class FormularioObjetivo extends JDialog {
    private JFrame parent;
    private Objetivo objetivoOriginal;
    private Objetivo objetivoCreado;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbTipoRespuesta;
    private JTextArea txtExplicacion;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public FormularioObjetivo(JFrame parent, Objetivo objetivo) {
        super(parent, objetivo == null ? "Agregar Nuevo Objetivo" : "Editar Objetivo", true);
        this.parent = parent;
        this.objetivoOriginal = objetivo;
        this.objetivoCreado = null;

        initComponents();
        if (objetivo != null) {
            cargarDatos(objetivo);
        }
    }

    private void initComponents() {
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de instrucciones mejorado
        JPanel panelInstrucciones = new JPanel(new BorderLayout());
        panelInstrucciones.setBorder(BorderFactory.createTitledBorder("Guía para Crear Objetivos"));

        JTextArea txtInstrucciones = new JTextArea(
            "PASO A PASO PARA CREAR UN OBJETIVO:\\n\\n" +
            "Los objetivos son las CONCLUSIONES que el sistema puede alcanzar después\\n" +
            "de evaluar las premisas del usuario.\\n\\n" +
            "1. NOMBRE: Use identificadores simples sin espacios\\n" +
            "   • Ejemplos CORRECTOS: 'gripe', 'bronquitis', 'ejercicio_cardio'\\n" +
            "   • Ejemplos INCORRECTOS: 'Tiene gripe', 'Diagnóstico de bronquitis'\\n\\n" +
            "2. DESCRIPCIÓN: Resumen breve de qué determina este objetivo\\n" +
            "   • Para DIAGNÓSTICOS: 'Posible gripe común', 'Infección respiratoria'\\n" +
            "   • Para RECOMENDACIONES: 'Ejercicio cardiovascular', 'Descanso médico'\\n" +
            "   • Para SOLUCIONES: 'Actualizar drivers', 'Reemplazar componente'\\n\\n" +
            "3. TIPO DE RESPUESTA: Formato del resultado que verá el cliente\\n" +
            "   • BOOLEANA: Sí/No (¿Tiene esta condición?)\\n" +
            "   • CATEGÓRICA: Lista de opciones (Leve/Moderado/Severo)\\n" +
            "   • NUMÉRICA: Valor específico (Probabilidad 85%)\\n" +
            "   • DESCRIPTIVA: Explicación detallada\\n\\n" +
            "4. EXPLICACIÓN: Justificación médica/técnica del objetivo\\n" +
            "   • Explique POR QUÉ este objetivo es válido\\n" +
            "   • Incluya criterios o síntomas que lo determinan"
        );
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setBackground(new Color(248, 248, 248));
        txtInstrucciones.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollInstrucciones = new JScrollPane(txtInstrucciones);
        scrollInstrucciones.setPreferredSize(new Dimension(0, 200));
        panelInstrucciones.add(scrollInstrucciones, BorderLayout.CENTER);

        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Objetivo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        txtNombre.setToolTipText("Identificador único sin espacios (ej: 'gripe', 'ejercicio_recomendado', 'problema_hardware')");
        panelFormulario.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setToolTipText("Resumen breve de qué determina este objetivo (ej: 'Posible gripe común', 'Necesita ejercicio cardiovascular')");
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelFormulario.add(scrollDesc, gbc);

        // Tipo de Respuesta
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panelFormulario.add(new JLabel("Tipo de Respuesta:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String[] tiposRespuesta = {
            "booleana - Sí/No, Verdadero/Falso",
            "categórica - Opciones específicas",
            "numérica - Valor numérico",
            "descriptiva - Texto explicativo"
        };
        cmbTipoRespuesta = new JComboBox<>(tiposRespuesta);
        panelFormulario.add(cmbTipoRespuesta, gbc);

        // Explicación
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Explicación:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        txtExplicacion = new JTextArea(5, 20);
        txtExplicacion.setLineWrap(true);
        txtExplicacion.setWrapStyleWord(true);
        txtExplicacion.setToolTipText("Justificación médica/técnica: explique POR QUÉ este objetivo es válido y qué criterios lo determinan");
        JScrollPane scrollExp = new JScrollPane(txtExplicacion);
        panelFormulario.add(scrollExp, gbc);

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

    private void cargarDatos(Objetivo objetivo) {
        txtNombre.setText(objetivo.getNombre());
        txtDescripcion.setText(objetivo.getDescripcion());

        // Buscar el tipo de respuesta en el combo
        String tipoRespuesta = objetivo.getTipoRespuesta();
        if (tipoRespuesta != null) {
            for (int i = 0; i < cmbTipoRespuesta.getItemCount(); i++) {
                String item = cmbTipoRespuesta.getItemAt(i);
                if (item.startsWith(tipoRespuesta)) {
                    cmbTipoRespuesta.setSelectedIndex(i);
                    break;
                }
            }
        }

        txtExplicacion.setText(objetivo.getExplicacion() != null ? objetivo.getExplicacion() : "");
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipoRespuestaCompleto = (String) cmbTipoRespuesta.getSelectedItem();
        String tipoRespuesta = tipoRespuestaCompleto.split(" - ")[0]; // Extraer solo el tipo
        String explicacion = txtExplicacion.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (nombre.contains(" ")) {
            JOptionPane.showMessageDialog(this, "El nombre no puede contener espacios.\n" +
                "Use guiones bajos si es necesario (ej: 'ejercicio_recomendado')",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }

        if (explicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La explicación es obligatoria",
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtExplicacion.requestFocus();
            return;
        }

        // Crear o actualizar objetivo
        if (objetivoOriginal == null) {
            // Nuevo objetivo
            objetivoCreado = new Objetivo(nombre, descripcion, tipoRespuesta);
            objetivoCreado.setExplicacion(explicacion);
        } else {
            // Editar objetivo existente
            objetivoOriginal.setNombre(nombre);
            objetivoOriginal.setDescripcion(descripcion);
            objetivoOriginal.setTipoRespuesta(tipoRespuesta);
            objetivoOriginal.setExplicacion(explicacion);
            objetivoCreado = objetivoOriginal;
        }

        JOptionPane.showMessageDialog(this,
            "Objetivo " + (objetivoOriginal == null ? "creado" : "actualizado") + " exitosamente",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public Objetivo getObjetivoCreado() {
        return objetivoCreado;
    }
}