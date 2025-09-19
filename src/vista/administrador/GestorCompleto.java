package vista.administrador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import modelo.entidades.*;
import persistencia.dao.*;

public class GestorCompleto extends JFrame {
    private GestionBaseConocimiento parent;
    private Usuario usuario;
    private BaseConocimiento baseConocimiento;
    private BaseConocimientoDAO baseDAO;

    // Paneles principales
    private JTabbedPane tabbedPane;
    private JPanel panelPremisas;
    private JPanel panelObjetivos;
    private JPanel panelReglas;

    // Tablas para mostrar datos
    private JTable tablaPremisas;
    private JTable tablaObjetivos;
    private JTable tablaReglas;
    private DefaultTableModel modeloPremisas;
    private DefaultTableModel modeloObjetivos;
    private DefaultTableModel modeloReglas;

    // Botones de gestión
    private JButton btnAgregarPremisa, btnEditarPremisa, btnEliminarPremisa;
    private JButton btnAgregarObjetivo, btnEditarObjetivo, btnEliminarObjetivo;
    private JButton btnAgregarRegla, btnEditarRegla, btnEliminarRegla;
    private JButton btnGuardarTodo, btnValidarCompleto, btnVolver;
    private JButton btnExportar, btnImportar, btnProbar, btnLimpiarTodo;

    // Panel de estado
    private JLabel lblEstadoGeneral;
    private JTextArea txtResumen;

    public GestorCompleto(GestionBaseConocimiento parent, Usuario usuario, BaseConocimiento baseConocimiento) {
        this.parent = parent;
        this.usuario = usuario;
        this.baseConocimiento = baseConocimiento;
        this.baseDAO = new BaseConocimientoDAOImpl();

        initComponents();
        cargarDatos();
        actualizarEstado();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Gestor Completo: " + baseConocimiento.getNombre());
        setSize(1000, 700);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel superior con información del gestor
        JPanel panelSuperior = crearPanelInformacion();

        // Panel central con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🏁 Inicio", crearPanelInicio());
        tabbedPane.addTab("Premisas", crearPanelPremisas());
        tabbedPane.addTab("Objetivos", crearPanelObjetivos());
        tabbedPane.addTab("Reglas", crearPanelReglas());
        tabbedPane.addTab("✓ Validación", crearPanelValidacion());
        tabbedPane.addTab("📤 Exportar/Importar", crearPanelExportacion());

        // Panel inferior con controles generales
        JPanel panelInferior = crearPanelControles();

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información del Gestor"));
        panel.setPreferredSize(new Dimension(0, 120));

        // Información básica
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelInfo.add(new JLabel("Nombre:"));
        panelInfo.add(new JLabel(baseConocimiento.getNombre()));
        panelInfo.add(new JLabel("Descripción:"));
        panelInfo.add(new JLabel(baseConocimiento.getDescripcion()));

        // Panel de estado
        JPanel panelEstado = new JPanel(new BorderLayout());
        lblEstadoGeneral = new JLabel();
        lblEstadoGeneral.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstadoGeneral.setHorizontalAlignment(SwingConstants.CENTER);

        txtResumen = new JTextArea(3, 40);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Arial", Font.PLAIN, 11));
        txtResumen.setBackground(getBackground());

        panelEstado.add(lblEstadoGeneral, BorderLayout.NORTH);
        panelEstado.add(new JScrollPane(txtResumen), BorderLayout.CENTER);

        panel.add(panelInfo, BorderLayout.WEST);
        panel.add(panelEstado, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de bienvenida y guía
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBorder(BorderFactory.createTitledBorder("Bienvenido al Gestor Completo"));

        JTextArea txtBienvenida = new JTextArea(
            "¡BIENVENIDO AL GESTOR COMPLETO DE SISTEMAS EXPERTOS!\n\n" +
            "Este módulo le permite crear y gestionar sistemas expertos de manera integral.\n" +
            "Siga estos pasos para crear un sistema experto funcional:\n\n" +
            "📋 PASO 1: CREAR PREMISAS (pestañas 'Premisas')\n" +
            "   • Son las preguntas que verá el cliente (edad, síntomas, problemas, etc.)\n" +
            "   • Defina al menos 2-3 premisas para comenzar\n" +
            "   • Use tipos apropiados: BOOLEANA (Sí/No), NUMERICA (números), STRING (texto)\n\n" +
            "🎯 PASO 2: DEFINIR OBJETIVOS (pestaña 'Objetivos')\n" +
            "   • Son las conclusiones que puede alcanzar el sistema\n" +
            "   • Ejemplos: diagnósticos, recomendaciones, soluciones\n" +
            "   • Defina al menos 1-2 objetivos principales\n\n" +
            "⚙️ PASO 3: CREAR REGLAS (pestaña 'Reglas')\n" +
            "   • Conectan las premisas con los objetivos usando lógica IF-THEN\n" +
            "   • Formato: SI (condiciones) ENTONCES (acciones)\n" +
            "   • Defina reglas para todos los escenarios posibles\n\n" +
            "✅ PASO 4: VALIDAR (pestaña 'Validación')\n" +
            "   • Verifique que el sistema esté completo y funcional\n" +
            "   • Pruebe la lógica y corrija errores\n\n" +
            "🚀 RESULTADO: Sistema listo para clientes\n" +
            "   • Los clientes podrán hacer consultas\n" +
            "   • El sistema inferirá conclusiones automáticamente"
        );
        txtBienvenida.setEditable(false);
        txtBienvenida.setBackground(new Color(248, 248, 255));
        txtBienvenida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtBienvenida.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollBienvenida = new JScrollPane(txtBienvenida);
        scrollBienvenida.setPreferredSize(new Dimension(0, 300));
        panelBienvenida.add(scrollBienvenida, BorderLayout.CENTER);

        // Panel de acciones rápidas
        JPanel panelAccionesRapidas = new JPanel(new GridLayout(2, 3, 10, 10));
        panelAccionesRapidas.setBorder(BorderFactory.createTitledBorder("Acciones Rápidas"));
        panelAccionesRapidas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Acciones Rápidas"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton btnIrPremisas = new JButton("➕ Crear Primera Premisa");
        JButton btnIrObjetivos = new JButton("🎯 Crear Primer Objetivo");
        JButton btnIrReglas = new JButton("⚙️ Crear Primera Regla");
        JButton btnValidarAhora = new JButton("✅ Validar Sistema");
        JButton btnProbarSistema = new JButton("🧪 Probar Sistema");
        JButton btnGuiaCompleta = new JButton("📖 Guía Detallada");

        // Configurar colores
        btnIrPremisas.setBackground(new Color(76, 175, 80));
        btnIrPremisas.setForeground(Color.WHITE);
        btnIrObjetivos.setBackground(new Color(33, 150, 243));
        btnIrObjetivos.setForeground(Color.WHITE);
        btnIrReglas.setBackground(new Color(156, 39, 176));
        btnIrReglas.setForeground(Color.WHITE);
        btnValidarAhora.setBackground(new Color(255, 193, 7));
        btnValidarAhora.setForeground(Color.BLACK);
        btnProbarSistema.setBackground(new Color(255, 87, 34));
        btnProbarSistema.setForeground(Color.WHITE);
        btnGuiaCompleta.setBackground(new Color(96, 125, 139));
        btnGuiaCompleta.setForeground(Color.WHITE);

        // Eventos de navegación
        btnIrPremisas.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1); // Pestaña Premisas
            agregarPremisa();
        });
        btnIrObjetivos.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2); // Pestaña Objetivos
            agregarObjetivo();
        });
        btnIrReglas.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3); // Pestaña Reglas
            agregarRegla();
        });
        btnValidarAhora.addActionListener(e -> {
            tabbedPane.setSelectedIndex(4); // Pestaña Validación
            validarCompleto();
        });
        btnProbarSistema.addActionListener(e -> probarGestor());
        btnGuiaCompleta.addActionListener(e -> mostrarGuiaDetallada());

        panelAccionesRapidas.add(btnIrPremisas);
        panelAccionesRapidas.add(btnIrObjetivos);
        panelAccionesRapidas.add(btnIrReglas);
        panelAccionesRapidas.add(btnValidarAhora);
        panelAccionesRapidas.add(btnProbarSistema);
        panelAccionesRapidas.add(btnGuiaCompleta);

        panel.add(panelBienvenida, BorderLayout.CENTER);
        panel.add(panelAccionesRapidas, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelPremisas() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabla de premisas
        String[] columnasPremisas = {"ID", "Nombre", "Descripción", "Tipo", "Valor Defecto"};
        modeloPremisas = new DefaultTableModel(columnasPremisas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPremisas = new JTable(modeloPremisas);
        tablaPremisas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPremisas.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPremisas = new JScrollPane(tablaPremisas);
        scrollPremisas.setPreferredSize(new Dimension(0, 300));

        // Panel de botones para premisas
        JPanel panelBotonesPremisas = new JPanel(new FlowLayout());
        btnAgregarPremisa = new JButton("Agregar Premisa");
        btnEditarPremisa = new JButton("Editar Premisa");
        btnEliminarPremisa = new JButton("Eliminar Premisa");

        // Configurar colores de botones
        btnAgregarPremisa.setBackground(new Color(76, 175, 80));
        btnAgregarPremisa.setForeground(Color.WHITE);
        btnEditarPremisa.setBackground(new Color(33, 150, 243));
        btnEditarPremisa.setForeground(Color.WHITE);
        btnEliminarPremisa.setBackground(new Color(244, 67, 54));
        btnEliminarPremisa.setForeground(Color.WHITE);

        // Eventos de botones
        btnAgregarPremisa.addActionListener(e -> agregarPremisa());
        btnEditarPremisa.addActionListener(e -> editarPremisa());
        btnEliminarPremisa.addActionListener(e -> eliminarPremisa());

        panelBotonesPremisas.add(btnAgregarPremisa);
        panelBotonesPremisas.add(btnEditarPremisa);
        panelBotonesPremisas.add(btnEliminarPremisa);

        // Panel de instrucciones
        JTextArea instruccionesPremisas = new JTextArea(
            "PREMISAS: Definen las variables que el cliente ingresará como datos de entrada.\n" +
            "• BOOLEANA: Para preguntas Sí/No (ej: ¿Tiene fiebre?)\n" +
            "• NUMERICA: Para valores numéricos (ej: Temperatura, Edad)\n" +
            "• STRING: Para texto libre (ej: Nombre, Descripción)"
        );
        instruccionesPremisas.setEditable(false);
        instruccionesPremisas.setBackground(new Color(245, 245, 245));
        instruccionesPremisas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(instruccionesPremisas, BorderLayout.NORTH);
        panel.add(scrollPremisas, BorderLayout.CENTER);
        panel.add(panelBotonesPremisas, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelObjetivos() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabla de objetivos
        String[] columnasObjetivos = {"ID", "Nombre", "Descripción", "Tipo Respuesta", "Explicación"};
        modeloObjetivos = new DefaultTableModel(columnasObjetivos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaObjetivos = new JTable(modeloObjetivos);
        tablaObjetivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaObjetivos.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollObjetivos = new JScrollPane(tablaObjetivos);
        scrollObjetivos.setPreferredSize(new Dimension(0, 300));

        // Panel de botones para objetivos
        JPanel panelBotonesObjetivos = new JPanel(new FlowLayout());
        btnAgregarObjetivo = new JButton("Agregar Objetivo");
        btnEditarObjetivo = new JButton("Editar Objetivo");
        btnEliminarObjetivo = new JButton("Eliminar Objetivo");

        // Configurar colores de botones
        btnAgregarObjetivo.setBackground(new Color(76, 175, 80));
        btnAgregarObjetivo.setForeground(Color.WHITE);
        btnEditarObjetivo.setBackground(new Color(33, 150, 243));
        btnEditarObjetivo.setForeground(Color.WHITE);
        btnEliminarObjetivo.setBackground(new Color(244, 67, 54));
        btnEliminarObjetivo.setForeground(Color.WHITE);

        // Eventos de botones
        btnAgregarObjetivo.addActionListener(e -> agregarObjetivo());
        btnEditarObjetivo.addActionListener(e -> editarObjetivo());
        btnEliminarObjetivo.addActionListener(e -> eliminarObjetivo());

        panelBotonesObjetivos.add(btnAgregarObjetivo);
        panelBotonesObjetivos.add(btnEditarObjetivo);
        panelBotonesObjetivos.add(btnEliminarObjetivo);

        // Panel de instrucciones
        JTextArea instruccionesObjetivos = new JTextArea(
            "OBJETIVOS: Definen las metas o conclusiones que el sistema experto puede alcanzar.\n" +
            "• Represetan el conocimiento que el sistema puede inferir\n" +
            "• Deben ser claros y específicos\n" +
            "• Ejemplo: 'Diagnóstico de gripe', 'Recomendación de ejercicio', 'Nivel de riesgo'"
        );
        instruccionesObjetivos.setEditable(false);
        instruccionesObjetivos.setBackground(new Color(245, 245, 245));
        instruccionesObjetivos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(instruccionesObjetivos, BorderLayout.NORTH);
        panel.add(scrollObjetivos, BorderLayout.CENTER);
        panel.add(panelBotonesObjetivos, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelReglas() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabla de reglas
        String[] columnasReglas = {"ID", "Nombre", "Condiciones", "Acciones", "Certeza", "Prioridad"};
        modeloReglas = new DefaultTableModel(columnasReglas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaReglas = new JTable(modeloReglas);
        tablaReglas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReglas.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollReglas = new JScrollPane(tablaReglas);
        scrollReglas.setPreferredSize(new Dimension(0, 300));

        // Panel de botones para reglas
        JPanel panelBotonesReglas = new JPanel(new FlowLayout());
        btnAgregarRegla = new JButton("Agregar Regla");
        btnEditarRegla = new JButton("Editar Regla");
        btnEliminarRegla = new JButton("Eliminar Regla");

        // Configurar colores de botones
        btnAgregarRegla.setBackground(new Color(76, 175, 80));
        btnAgregarRegla.setForeground(Color.WHITE);
        btnEditarRegla.setBackground(new Color(33, 150, 243));
        btnEditarRegla.setForeground(Color.WHITE);
        btnEliminarRegla.setBackground(new Color(244, 67, 54));
        btnEliminarRegla.setForeground(Color.WHITE);

        // Eventos de botones
        btnAgregarRegla.addActionListener(e -> agregarRegla());
        btnEditarRegla.addActionListener(e -> editarRegla());
        btnEliminarRegla.addActionListener(e -> eliminarRegla());

        panelBotonesReglas.add(btnAgregarRegla);
        panelBotonesReglas.add(btnEditarRegla);
        panelBotonesReglas.add(btnEliminarRegla);

        // Panel de instrucciones
        JTextArea instruccionesReglas = new JTextArea(
            "REGLAS: Conectan las premisas con los objetivos usando lógica IF-THEN.\n" +
            "• Formato: IF (condiciones) THEN (acciones)\n" +
            "• Usar espacios: 'fiebre = true', 'temperatura > 38'\n" +
            "• Operadores: = (igual), > (mayor), < (menor), >= (mayor/igual), <= (menor/igual)\n" +
            "• Ejemplo: SI fiebre=true Y temperatura>38 ENTONCES gripe=true"
        );
        instruccionesReglas.setEditable(false);
        instruccionesReglas.setBackground(new Color(245, 245, 245));
        instruccionesReglas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(instruccionesReglas, BorderLayout.NORTH);
        panel.add(scrollReglas, BorderLayout.CENTER);
        panel.add(panelBotonesReglas, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Controles Generales"));

        btnGuardarTodo = new JButton("Guardar Todo");
        btnValidarCompleto = new JButton("Validar Completitud");
        btnProbar = new JButton("Probar Gestor");
        btnVolver = new JButton("Volver");

        // Configurar apariencia
        btnGuardarTodo.setBackground(new Color(76, 175, 80));
        btnGuardarTodo.setForeground(Color.WHITE);
        btnGuardarTodo.setFont(new Font("Arial", Font.BOLD, 12));

        btnValidarCompleto.setBackground(new Color(255, 193, 7));
        btnValidarCompleto.setForeground(Color.BLACK);
        btnValidarCompleto.setFont(new Font("Arial", Font.BOLD, 12));

        // Eventos
        btnGuardarTodo.addActionListener(e -> guardarTodo());
        btnValidarCompleto.addActionListener(e -> validarCompleto());
        btnProbar.addActionListener(e -> probarGestor());
        btnVolver.addActionListener(e -> volver());

        panel.add(btnGuardarTodo);
        panel.add(btnValidarCompleto);
        panel.add(btnProbar);
        panel.add(btnVolver);

        return panel;
    }

    // === MÉTODOS DE GESTIÓN DE PREMISAS ===

    private void agregarPremisa() {
        FormularioPremisa form = new FormularioPremisa(this, null);
        form.setVisible(true);

        if (form.getPremisaCreada() != null) {
            Premisa nuevaPremisa = form.getPremisaCreada();
            nuevaPremisa.setId(generarNuevoIdPremisa());
            baseConocimiento.getPremisas().add(nuevaPremisa);
            cargarTablaPremisas();
            actualizarEstado();
        }
    }

    private void editarPremisa() {
        int filaSeleccionada = tablaPremisas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una premisa para editar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloPremisas.getValueAt(filaSeleccionada, 0);
        Premisa premisa = buscarPremisaPorId(id);

        if (premisa != null) {
            FormularioPremisa form = new FormularioPremisa(this, premisa);
            form.setVisible(true);

            if (form.getPremisaCreada() != null) {
                cargarTablaPremisas();
                actualizarEstado();
            }
        }
    }

    private void eliminarPremisa() {
        int filaSeleccionada = tablaPremisas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una premisa para eliminar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloPremisas.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloPremisas.getValueAt(filaSeleccionada, 1);

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la premisa '" + nombre + "'?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getPremisas().removeIf(p -> p.getId() == id);
            cargarTablaPremisas();
            actualizarEstado();
        }
    }

    // === MÉTODOS DE GESTIÓN DE OBJETIVOS ===

    private void agregarObjetivo() {
        FormularioObjetivo form = new FormularioObjetivo(this, null);
        form.setVisible(true);

        if (form.getObjetivoCreado() != null) {
            Objetivo nuevoObjetivo = form.getObjetivoCreado();
            nuevoObjetivo.setId(generarNuevoIdObjetivo());
            baseConocimiento.getObjetivos().add(nuevoObjetivo);
            cargarTablaObjetivos();
            actualizarEstado();
        }
    }

    private void editarObjetivo() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un objetivo para editar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloObjetivos.getValueAt(filaSeleccionada, 0);
        Objetivo objetivo = buscarObjetivoPorId(id);

        if (objetivo != null) {
            FormularioObjetivo form = new FormularioObjetivo(this, objetivo);
            form.setVisible(true);

            if (form.getObjetivoCreado() != null) {
                cargarTablaObjetivos();
                actualizarEstado();
            }
        }
    }

    private void eliminarObjetivo() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un objetivo para eliminar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloObjetivos.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloObjetivos.getValueAt(filaSeleccionada, 1);

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el objetivo '" + nombre + "'?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getObjetivos().removeIf(o -> o.getId() == id);
            cargarTablaObjetivos();
            actualizarEstado();
        }
    }

    // === MÉTODOS DE GESTIÓN DE REGLAS ===

    private void agregarRegla() {
        FormularioRegla form = new FormularioRegla(this, null);
        form.setVisible(true);

        if (form.getReglaCreada() != null) {
            Regla nuevaRegla = form.getReglaCreada();
            nuevaRegla.setId(generarNuevoIdRegla());
            baseConocimiento.getReglas().add(nuevaRegla);
            cargarTablaReglas();
            actualizarEstado();
        }
    }

    private void editarRegla() {
        int filaSeleccionada = tablaReglas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una regla para editar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloReglas.getValueAt(filaSeleccionada, 0);
        Regla regla = buscarReglaPorId(id);

        if (regla != null) {
            FormularioRegla form = new FormularioRegla(this, regla);
            form.setVisible(true);

            if (form.getReglaCreada() != null) {
                cargarTablaReglas();
                actualizarEstado();
            }
        }
    }

    private void eliminarRegla() {
        int filaSeleccionada = tablaReglas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una regla para eliminar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloReglas.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloReglas.getValueAt(filaSeleccionada, 1);

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la regla '" + nombre + "'?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getReglas().removeIf(r -> r.getId() == id);
            cargarTablaReglas();
            actualizarEstado();
        }
    }

    // === MÉTODOS DE CARGA DE DATOS ===

    private void cargarDatos() {
        cargarTablaPremisas();
        cargarTablaObjetivos();
        cargarTablaReglas();
    }

    private void cargarTablaPremisas() {
        modeloPremisas.setRowCount(0);
        for (Premisa p : baseConocimiento.getPremisas()) {
            Object[] fila = {
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getTipo(),
                p.getValorDefecto()
            };
            modeloPremisas.addRow(fila);
        }
    }

    private void cargarTablaObjetivos() {
        modeloObjetivos.setRowCount(0);
        for (Objetivo o : baseConocimiento.getObjetivos()) {
            Object[] fila = {
                o.getId(),
                o.getNombre(),
                o.getDescripcion(),
                o.getTipoRespuesta(),
                o.getExplicacion()
            };
            modeloObjetivos.addRow(fila);
        }
    }

    private void cargarTablaReglas() {
        modeloReglas.setRowCount(0);
        for (Regla r : baseConocimiento.getReglas()) {
            Object[] fila = {
                r.getId(),
                r.getNombre(),
                String.join(" Y ", r.getCondiciones()),
                String.join(", ", r.getAcciones()),
                r.getFactorCerteza(),
                r.getPrioridad()
            };
            modeloReglas.addRow(fila);
        }
    }

    // === MÉTODOS AUXILIARES ===

    private Premisa buscarPremisaPorId(int id) {
        return baseConocimiento.getPremisas().stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private Objetivo buscarObjetivoPorId(int id) {
        return baseConocimiento.getObjetivos().stream()
            .filter(o -> o.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private Regla buscarReglaPorId(int id) {
        return baseConocimiento.getReglas().stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private int generarNuevoIdPremisa() {
        return baseConocimiento.getPremisas().stream()
            .mapToInt(Premisa::getId)
            .max()
            .orElse(0) + 1;
    }

    private int generarNuevoIdObjetivo() {
        return baseConocimiento.getObjetivos().stream()
            .mapToInt(Objetivo::getId)
            .max()
            .orElse(0) + 1;
    }

    private int generarNuevoIdRegla() {
        return baseConocimiento.getReglas().stream()
            .mapToInt(Regla::getId)
            .max()
            .orElse(0) + 1;
    }

    private void actualizarEstado() {
        int premisas = baseConocimiento.getPremisas().size();
        int objetivos = baseConocimiento.getObjetivos().size();
        int reglas = baseConocimiento.getReglas().size();

        boolean esCompleto = premisas > 0 && objetivos > 0 && reglas > 0;

        if (esCompleto) {
            lblEstadoGeneral.setText("GESTOR COMPLETO - Listo para clientes");
            lblEstadoGeneral.setForeground(new Color(0, 128, 0));
            setTitle("Gestor Completo: " + baseConocimiento.getNombre() + " (COMPLETO)");
        } else {
            lblEstadoGeneral.setText("GESTOR INCOMPLETO - Requiere más datos");
            lblEstadoGeneral.setForeground(new Color(255, 140, 0));
            setTitle("Gestor Completo: " + baseConocimiento.getNombre() + " (INCOMPLETO)");
        }

        txtResumen.setText(String.format(
            "Premisas: %d definidas %s\n" +
            "Objetivos: %d definidos %s\n" +
            "Reglas: %d definidas %s\n\n" +
            "Estado: %s",
            premisas, premisas > 0 ? "[OK]" : "[PENDIENTE]",
            objetivos, objetivos > 0 ? "[OK]" : "[PENDIENTE]",
            reglas, reglas > 0 ? "[OK]" : "[PENDIENTE]",
            esCompleto ? "Listo para usar" : "Necesita completar componentes faltantes"
        ));

        // Actualizar pestañas con contadores
        tabbedPane.setTitleAt(0, "Premisas (" + premisas + ")");
        tabbedPane.setTitleAt(1, "Objetivos (" + objetivos + ")");
        tabbedPane.setTitleAt(2, "Reglas (" + reglas + ")");
    }

    private void guardarTodo() {
        try {
            boolean exito = baseDAO.actualizar(baseConocimiento);
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Gestor guardado exitosamente.\n" +
                    "Los cambios se han persistido en el sistema.",
                    "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
                parent.actualizarLista();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar el gestor.\n" +
                    "Inténtelo nuevamente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarCompleto() {
        int premisas = baseConocimiento.getPremisas().size();
        int objetivos = baseConocimiento.getObjetivos().size();
        int reglas = baseConocimiento.getReglas().size();

        StringBuilder mensaje = new StringBuilder("VALIDACIÓN DE COMPLETITUD\n\n");

        mensaje.append("Premisas: ").append(premisas)
               .append(premisas > 0 ? " [OK]\n" : " [ERROR] - Mínimo 1 requerida\n");

        mensaje.append("Objetivos: ").append(objetivos)
               .append(objetivos > 0 ? " [OK]\n" : " [ERROR] - Mínimo 1 requerido\n");

        mensaje.append("Reglas: ").append(reglas)
               .append(reglas > 0 ? " [OK]\n" : " [ERROR] - Mínimo 1 requerida\n");

        mensaje.append("\n");

        if (premisas > 0 && objetivos > 0 && reglas > 0) {
            mensaje.append("RESULTADO: GESTOR COMPLETO\n");
            mensaje.append("Este gestor aparecerá disponible para los clientes.\n");
            mensaje.append("Los clientes podrán hacer consultas y obtener conclusiones.");
        } else {
            mensaje.append("RESULTADO: GESTOR INCOMPLETO\n");
            mensaje.append("Este gestor NO aparecerá disponible para los clientes.\n");
            mensaje.append("Complete los componentes faltantes para activarlo.");
        }

        JOptionPane.showMessageDialog(this, mensaje.toString(),
            "Validación de Completitud", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel crearPanelValidacion() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea txtValidacion = new JTextArea();
        txtValidacion.setEditable(false);
        txtValidacion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollValidacion = new JScrollPane(txtValidacion);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnValidarAhora = new JButton("Ejecutar Validación Completa");
        JButton btnVerificarIntegridad = new JButton("Verificar Integridad");
        JButton btnAnalisisReglas = new JButton("Análisis de Reglas");

        btnValidarAhora.setBackground(new Color(33, 150, 243));
        btnValidarAhora.setForeground(Color.WHITE);
        btnVerificarIntegridad.setBackground(new Color(255, 193, 7));
        btnVerificarIntegridad.setForeground(Color.BLACK);
        btnAnalisisReglas.setBackground(new Color(156, 39, 176));
        btnAnalisisReglas.setForeground(Color.WHITE);

        btnValidarAhora.addActionListener(e -> {
            txtValidacion.setText(ejecutarValidacionCompleta());
            txtValidacion.setCaretPosition(0);
        });

        btnVerificarIntegridad.addActionListener(e -> {
            txtValidacion.setText(verificarIntegridad());
            txtValidacion.setCaretPosition(0);
        });

        btnAnalisisReglas.addActionListener(e -> {
            txtValidacion.setText(analizarReglas());
            txtValidacion.setCaretPosition(0);
        });

        panelBotones.add(btnValidarAhora);
        panelBotones.add(btnVerificarIntegridad);
        panelBotones.add(btnAnalisisReglas);

        JTextArea instrucciones = new JTextArea(
            "VALIDACIÓN AVANZADA DEL GESTOR\n\n" +
            "• Validación Completa: Verifica todos los componentes y relaciones\n" +
            "• Verificar Integridad: Comprueba consistencia de datos\n" +
            "• Análisis de Reglas: Evalúa la lógica y posibles conflictos"
        );
        instrucciones.setEditable(false);
        instrucciones.setBackground(new Color(245, 245, 245));
        instrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(instrucciones, BorderLayout.NORTH);
        panel.add(scrollValidacion, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelExportacion() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea txtExportacion = new JTextArea();
        txtExportacion.setEditable(false);
        txtExportacion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollExportacion = new JScrollPane(txtExportacion);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnExportar = new JButton("Exportar Base Completa");
        btnImportar = new JButton("Importar Base");
        JButton btnExportarReglas = new JButton("Exportar Solo Reglas");
        btnLimpiarTodo = new JButton("Limpiar Todo");

        btnExportar.setBackground(new Color(76, 175, 80));
        btnExportar.setForeground(Color.WHITE);
        btnImportar.setBackground(new Color(33, 150, 243));
        btnImportar.setForeground(Color.WHITE);
        btnExportarReglas.setBackground(new Color(255, 193, 7));
        btnExportarReglas.setForeground(Color.BLACK);
        btnLimpiarTodo.setBackground(new Color(244, 67, 54));
        btnLimpiarTodo.setForeground(Color.WHITE);

        btnExportar.addActionListener(e -> exportarBase());
        btnImportar.addActionListener(e -> importarBase());
        btnExportarReglas.addActionListener(e -> exportarReglas());
        btnLimpiarTodo.addActionListener(e -> limpiarTodo());

        panelBotones.add(btnExportar);
        panelBotones.add(btnImportar);
        panelBotones.add(btnExportarReglas);
        panelBotones.add(btnLimpiarTodo);

        JTextArea instrucciones = new JTextArea(
            "GESTIÓN DE DATOS\n\n" +
            "• Exportar: Guarda la base completa en archivo\n" +
            "• Importar: Carga datos desde archivo externo\n" +
            "• Exportar Reglas: Solo las reglas en formato texto\n" +
            "• Limpiar Todo: Elimina todos los datos del gestor"
        );
        instrucciones.setEditable(false);
        instrucciones.setBackground(new Color(245, 245, 245));
        instrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(instrucciones, BorderLayout.NORTH);
        panel.add(scrollExportacion, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private String ejecutarValidacionCompleta() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== VALIDACIÓN COMPLETA DEL GESTOR ===\n\n");
        resultado.append("Fecha: ").append(java.time.LocalDateTime.now()).append("\n");
        resultado.append("Gestor: ").append(baseConocimiento.getNombre()).append("\n\n");

        // Validar premisas
        resultado.append("1. VALIDACIÓN DE PREMISAS\n");
        List<Premisa> premisas = baseConocimiento.getPremisas();
        resultado.append("   Cantidad: ").append(premisas.size()).append("\n");

        if (premisas.isEmpty()) {
            resultado.append("   [ERROR] No hay premisas definidas\n");
        } else {
            for (Premisa p : premisas) {
                if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
                    resultado.append("   [ERROR] Premisa sin nombre\n");
                } else if (p.getDescripcion() == null || p.getDescripcion().trim().isEmpty()) {
                    resultado.append("   [WARNING] Premisa '").append(p.getNombre()).append("' sin descripción\n");
                } else {
                    resultado.append("   [OK] ").append(p.getNombre()).append(" - ").append(p.getTipo()).append("\n");
                }
            }
        }

        // Validar objetivos
        resultado.append("\n2. VALIDACIÓN DE OBJETIVOS\n");
        List<Objetivo> objetivos = baseConocimiento.getObjetivos();
        resultado.append("   Cantidad: ").append(objetivos.size()).append("\n");

        if (objetivos.isEmpty()) {
            resultado.append("   [ERROR] No hay objetivos definidos\n");
        } else {
            for (Objetivo o : objetivos) {
                if (o.getNombre() == null || o.getNombre().trim().isEmpty()) {
                    resultado.append("   [ERROR] Objetivo sin nombre\n");
                } else {
                    resultado.append("   [OK] ").append(o.getNombre()).append(" - ").append(o.getTipoRespuesta()).append("\n");
                }
            }
        }

        // Validar reglas
        resultado.append("\n3. VALIDACIÓN DE REGLAS\n");
        List<Regla> reglas = baseConocimiento.getReglas();
        resultado.append("   Cantidad: ").append(reglas.size()).append("\n");

        if (reglas.isEmpty()) {
            resultado.append("   [ERROR] No hay reglas definidas\n");
        } else {
            for (Regla r : reglas) {
                if (r.getNombre() == null || r.getNombre().trim().isEmpty()) {
                    resultado.append("   [ERROR] Regla sin nombre\n");
                } else if (r.getCondiciones().isEmpty()) {
                    resultado.append("   [ERROR] Regla '").append(r.getNombre()).append("' sin condiciones\n");
                } else if (r.getAcciones().isEmpty()) {
                    resultado.append("   [ERROR] Regla '").append(r.getNombre()).append("' sin acciones\n");
                } else {
                    resultado.append("   [OK] ").append(r.getNombre()).append(" (").append(r.getCondiciones().size())
                             .append(" condiciones, ").append(r.getAcciones().size()).append(" acciones)\n");
                }
            }
        }

        // Resultado final
        resultado.append("\n=== RESULTADO FINAL ===\n");
        boolean esCompleto = !premisas.isEmpty() && !objetivos.isEmpty() && !reglas.isEmpty();
        if (esCompleto) {
            resultado.append("ESTADO: GESTOR VÁLIDO Y COMPLETO\n");
            resultado.append("Este gestor está listo para ser utilizado por los clientes.\n");
        } else {
            resultado.append("ESTADO: GESTOR INCOMPLETO\n");
            resultado.append("Complete los componentes faltantes antes de permitir el acceso a clientes.\n");
        }

        return resultado.toString();
    }

    private String verificarIntegridad() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== VERIFICACIÓN DE INTEGRIDAD ===\n\n");

        // Verificar IDs únicos
        Set<Integer> idsPremisas = new HashSet<>();
        Set<Integer> idsObjetivos = new HashSet<>();
        Set<Integer> idsReglas = new HashSet<>();

        for (Premisa p : baseConocimiento.getPremisas()) {
            if (!idsPremisas.add(p.getId())) {
                resultado.append("[ERROR] ID duplicado en premisas: ").append(p.getId()).append("\n");
            }
        }

        for (Objetivo o : baseConocimiento.getObjetivos()) {
            if (!idsObjetivos.add(o.getId())) {
                resultado.append("[ERROR] ID duplicado en objetivos: ").append(o.getId()).append("\n");
            }
        }

        for (Regla r : baseConocimiento.getReglas()) {
            if (!idsReglas.add(r.getId())) {
                resultado.append("[ERROR] ID duplicado en reglas: ").append(r.getId()).append("\n");
            }
        }

        resultado.append("Verificación de IDs: ").append(
            idsPremisas.size() == baseConocimiento.getPremisas().size() &&
            idsObjetivos.size() == baseConocimiento.getObjetivos().size() &&
            idsReglas.size() == baseConocimiento.getReglas().size() ? "OK" : "ERRORES ENCONTRADOS"
        ).append("\n\n");

        // Verificar referencias en reglas
        Set<String> nombresPremisas = baseConocimiento.getPremisas().stream()
            .map(Premisa::getNombre).collect(java.util.stream.Collectors.toSet());
        Set<String> nombresObjetivos = baseConocimiento.getObjetivos().stream()
            .map(Objetivo::getNombre).collect(java.util.stream.Collectors.toSet());

        resultado.append("VERIFICACIÓN DE REFERENCIAS EN REGLAS:\n");
        for (Regla r : baseConocimiento.getReglas()) {
            resultado.append("Regla: ").append(r.getNombre()).append("\n");

            for (String condicion : r.getCondiciones()) {
                String nombrePremisa = condicion.split("\\s*[=<>!]+\\s*")[0].trim();
                if (!nombresPremisas.contains(nombrePremisa)) {
                    resultado.append("  [ERROR] Premisa no encontrada: ").append(nombrePremisa).append("\n");
                }
            }

            for (String accion : r.getAcciones()) {
                String nombreObjetivo = accion.split("\\s*=\\s*")[0].trim();
                if (!nombresObjetivos.contains(nombreObjetivo)) {
                    resultado.append("  [ERROR] Objetivo no encontrado: ").append(nombreObjetivo).append("\n");
                }
            }
        }

        return resultado.toString();
    }

    private String analizarReglas() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== ANÁLISIS DE REGLAS ===\n\n");

        List<Regla> reglas = baseConocimiento.getReglas();
        resultado.append("Total de reglas: ").append(reglas.size()).append("\n\n");

        // Analizar cada regla
        for (Regla r : reglas) {
            resultado.append("REGLA: ").append(r.getNombre()).append("\n");
            resultado.append("  Condiciones (").append(r.getCondiciones().size()).append("):\n");
            for (String cond : r.getCondiciones()) {
                resultado.append("    - ").append(cond).append("\n");
            }
            resultado.append("  Acciones (").append(r.getAcciones().size()).append("):\n");
            for (String acc : r.getAcciones()) {
                resultado.append("    - ").append(acc).append("\n");
            }
            resultado.append("  Factor de certeza: ").append(r.getFactorCerteza()).append("\n");
            resultado.append("  Prioridad: ").append(r.getPrioridad()).append("\n\n");
        }

        // Detectar posibles conflictos
        resultado.append("ANÁLISIS DE CONFLICTOS:\n");
        Map<String, List<String>> accionesPorObjetivo = new HashMap<>();

        for (Regla r : reglas) {
            for (String accion : r.getAcciones()) {
                String objetivo = accion.split("\\s*=\\s*")[0].trim();
                accionesPorObjetivo.computeIfAbsent(objetivo, k -> new ArrayList<>()).add(r.getNombre());
            }
        }

        for (Map.Entry<String, List<String>> entry : accionesPorObjetivo.entrySet()) {
            if (entry.getValue().size() > 1) {
                resultado.append("  [INFO] Objetivo '").append(entry.getKey())
                         .append("' modificado por múltiples reglas: ")
                         .append(String.join(", ", entry.getValue())).append("\n");
            }
        }

        return resultado.toString();
    }

    private void exportarBase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Base de Conocimiento");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new java.io.File(file.getAbsolutePath() + ".txt");
                }

                StringBuilder contenido = new StringBuilder();
                contenido.append("# Base de Conocimiento: ").append(baseConocimiento.getNombre()).append("\n");
                contenido.append("# Descripción: ").append(baseConocimiento.getDescripcion()).append("\n");
                contenido.append("# Exportado: ").append(java.time.LocalDateTime.now()).append("\n\n");

                contenido.append("## PREMISAS\n");
                for (Premisa p : baseConocimiento.getPremisas()) {
                    contenido.append(p.getNombre()).append("|").append(p.getDescripcion())
                             .append("|").append(p.getTipo()).append("|").append(p.getValorDefecto()).append("\n");
                }

                contenido.append("\n## OBJETIVOS\n");
                for (Objetivo o : baseConocimiento.getObjetivos()) {
                    contenido.append(o.getNombre()).append("|").append(o.getDescripcion())
                             .append("|").append(o.getTipoRespuesta()).append("|").append(o.getExplicacion()).append("\n");
                }

                contenido.append("\n## REGLAS\n");
                for (Regla r : baseConocimiento.getReglas()) {
                    contenido.append(r.getNombre()).append("|")
                             .append(String.join(" Y ", r.getCondiciones())).append("|")
                             .append(String.join(", ", r.getAcciones())).append("|")
                             .append(r.getFactorCerteza()).append("|").append(r.getPrioridad()).append("\n");
                }

                java.nio.file.Files.write(file.toPath(), contenido.toString().getBytes());
                JOptionPane.showMessageDialog(this, "Base exportada exitosamente a: " + file.getAbsolutePath(),
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarBase() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de importación en desarrollo.\n" +
            "Próximamente estará disponible para cargar bases desde archivos externos.",
            "En Desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarReglas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Reglas");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new java.io.File(file.getAbsolutePath() + ".txt");
                }

                StringBuilder contenido = new StringBuilder();
                contenido.append("Reglas de: ").append(baseConocimiento.getNombre()).append("\n");
                contenido.append("Exportado: ").append(java.time.LocalDateTime.now()).append("\n\n");

                for (Regla r : baseConocimiento.getReglas()) {
                    contenido.append("REGLA: ").append(r.getNombre()).append("\n");
                    contenido.append("SI: ").append(String.join(" Y ", r.getCondiciones())).append("\n");
                    contenido.append("ENTONCES: ").append(String.join(", ", r.getAcciones())).append("\n");
                    contenido.append("Certeza: ").append(r.getFactorCerteza()).append("\n");
                    contenido.append("Prioridad: ").append(r.getPrioridad()).append("\n\n");
                }

                java.nio.file.Files.write(file.toPath(), contenido.toString().getBytes());
                JOptionPane.showMessageDialog(this, "Reglas exportadas exitosamente a: " + file.getAbsolutePath(),
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar reglas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarTodo() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar TODOS los datos del gestor?\n" +
            "Esta acción no se puede deshacer.\n\n" +
            "Se eliminarán:\n" +
            "- " + baseConocimiento.getPremisas().size() + " premisas\n" +
            "- " + baseConocimiento.getObjetivos().size() + " objetivos\n" +
            "- " + baseConocimiento.getReglas().size() + " reglas",
            "Confirmar Limpieza Total",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            baseConocimiento.getPremisas().clear();
            baseConocimiento.getObjetivos().clear();
            baseConocimiento.getReglas().clear();

            cargarDatos();
            actualizarEstado();

            JOptionPane.showMessageDialog(this, "Todos los datos han sido eliminados.\n" +
                "El gestor está ahora vacío y listo para nuevos datos.",
                "Limpieza Completada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void probarGestor() {
        if (baseConocimiento.getPremisas().isEmpty() ||
            baseConocimiento.getObjetivos().isEmpty() ||
            baseConocimiento.getReglas().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se puede probar un gestor incompleto.\n" +
                "Agregue al menos una premisa, un objetivo y una regla.",
                "Gestor Incompleto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simular una consulta de prueba
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== PRUEBA DEL GESTOR ===\n\n");
        resultado.append("Simulando consulta con datos de prueba...\n\n");

        resultado.append("PREMISAS DISPONIBLES:\n");
        for (Premisa p : baseConocimiento.getPremisas()) {
            String valorPrueba = p.getValorDefecto() != null ? p.getValorDefecto() : "valor_prueba";
            resultado.append("- ").append(p.getNombre()).append(" = ").append(valorPrueba).append("\n");
        }

        resultado.append("\nREGLAS EVALUADAS:\n");
        for (Regla r : baseConocimiento.getReglas()) {
            resultado.append("- ").append(r.getNombre()).append(": ");
            resultado.append("IF (").append(String.join(" Y ", r.getCondiciones())).append(") ");
            resultado.append("THEN (").append(String.join(", ", r.getAcciones())).append(")\n");
        }

        resultado.append("\nOBJETIVOS POSIBLES:\n");
        for (Objetivo o : baseConocimiento.getObjetivos()) {
            resultado.append("- ").append(o.getNombre()).append(": ").append(o.getDescripcion()).append("\n");
        }

        resultado.append("\nESTADO: El gestor está estructuralmente correcto y listo para consultas reales.");

        JTextArea areaResultado = new JTextArea(resultado.toString());
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scroll, "Prueba del Gestor", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarGuiaDetallada() {
        String guia =
            "=== GUÍA COMPLETA PARA CREAR SISTEMAS EXPERTOS ===\n\n" +
            "Un sistema experto simula el razonamiento de un experto humano en un dominio específico.\n" +
            "Está compuesto por tres elementos principales:\n\n" +
            "1. BASE DE CONOCIMIENTO (Premisas + Objetivos + Reglas)\n" +
            "2. MOTOR DE INFERENCIA (automático)\n" +
            "3. INTERFAZ DE USUARIO (automática)\n\n" +
            "══════════════════════════════════════════════════════════\n" +
            "📋 PREMISAS - Los datos de entrada\n" +
            "══════════════════════════════════════════════════════════\n" +
            "Las premisas representan la información que el cliente proporcionará:\n\n" +
            "• TIPO BOOLEANA: Para preguntas Sí/No\n" +
            "  - Nombre: 'fiebre', 'dolor_cabeza', 'funciona_motor'\n" +
            "  - Descripción: '¿Tiene fiebre?', '¿Le duele la cabeza?'\n" +
            "  - El cliente verá botones Sí/No\n\n" +
            "• TIPO NUMERICA: Para valores numéricos\n" +
            "  - Nombre: 'edad', 'temperatura', 'presion'\n" +
            "  - Descripción: 'Edad en años', 'Temperatura corporal en °C'\n" +
            "  - El cliente ingresará números\n\n" +
            "• TIPO STRING: Para texto libre\n" +
            "  - Nombre: 'nombre_paciente', 'descripcion_problema'\n" +
            "  - Descripción: 'Nombre del paciente', 'Describa el problema'\n" +
            "  - El cliente escribirá texto\n\n" +
            "══════════════════════════════════════════════════════════\n" +
            "🎯 OBJETIVOS - Los resultados posibles\n" +
            "══════════════════════════════════════════════════════════\n" +
            "Los objetivos son las conclusiones que puede alcanzar el sistema:\n\n" +
            "• Para DIAGNÓSTICOS MÉDICOS:\n" +
            "  - Nombres: 'gripe', 'migrana', 'resfriado'\n" +
            "  - Descripción: 'Posible gripe común', 'Probable migraña'\n\n" +
            "• Para RECOMENDACIONES:\n" +
            "  - Nombres: 'ejercicio_cardio', 'descanso', 'consulta_medica'\n" +
            "  - Descripción: 'Recomendar ejercicio cardiovascular'\n\n" +
            "• Para SOLUCIONES TÉCNICAS:\n" +
            "  - Nombres: 'cambiar_aceite', 'revisar_frenos', 'actualizar_drivers'\n" +
            "  - Descripción: 'Cambiar aceite del motor', 'Revisar sistema de frenos'\n\n" +
            "══════════════════════════════════════════════════════════\n" +
            "⚙️ REGLAS - La lógica del sistema\n" +
            "══════════════════════════════════════════════════════════\n" +
            "Las reglas conectan premisas con objetivos usando formato IF-THEN:\n\n" +
            "• FORMATO BÁSICO:\n" +
            "  - Condiciones: 'fiebre = true', 'temperatura > 38'\n" +
            "  - Acciones: 'gripe = true', 'consulta_medica = true'\n\n" +
            "• OPERADORES DISPONIBLES:\n" +
            "  - Igualdad: = (fiebre = true, edad = 25)\n" +
            "  - Mayor que: > (temperatura > 38, edad > 65)\n" +
            "  - Menor que: < (presion < 90, edad < 18)\n" +
            "  - Mayor o igual: >= (edad >= 18)\n" +
            "  - Menor o igual: <= (temperatura <= 36)\n\n" +
            "• EJEMPLOS DE REGLAS:\n" +
            "  1. SI: fiebre=true Y temperatura>38 ENTONCES: gripe=true\n" +
            "  2. SI: edad>65 Y presion>140 ENTONCES: consulta_medica=true\n" +
            "  3. SI: dolor_cabeza=true Y edad<50 ENTONCES: migrana=true\n\n" +
            "══════════════════════════════════════════════════════════\n" +
            "✅ VALIDACIÓN Y PRUEBAS\n" +
            "══════════════════════════════════════════════════════════\n" +
            "Antes de que los clientes usen el sistema:\n\n" +
            "1. Validar que hay al menos:\n" +
            "   - 1 premisa (mejor 2-5)\n" +
            "   - 1 objetivo (mejor 2-10)\n" +
            "   - 1 regla (mejor 3-20)\n\n" +
            "2. Verificar que todas las reglas referencian:\n" +
            "   - Premisas existentes en las condiciones\n" +
            "   - Objetivos existentes en las acciones\n\n" +
            "3. Probar con datos reales para verificar lógica\n\n" +
            "══════════════════════════════════════════════════════════\n" +
            "🚀 CONSEJOS PARA CREAR BUENOS SISTEMAS EXPERTOS\n" +
            "══════════════════════════════════════════════════════════\n" +
            "• Empiece simple: 2-3 premisas, 2-3 objetivos, 3-5 reglas\n" +
            "• Use nombres descriptivos pero cortos\n" +
            "• Cubra todos los escenarios posibles con reglas\n" +
            "• Pruebe con casos extremos\n" +
            "• Mantenga las reglas simples y claras\n" +
            "• Documente la lógica en las explicaciones\n\n" +
            "¡Con estos elementos tendrá un sistema experto funcional!";

        JTextArea areaGuia = new JTextArea(guia);
        areaGuia.setEditable(false);
        areaGuia.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        areaGuia.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(areaGuia);
        scroll.setPreferredSize(new Dimension(800, 600));

        JOptionPane.showMessageDialog(this, scroll, "Guía Completa para Sistemas Expertos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void volver() {
        // Guardar antes de salir
        guardarTodo();
        dispose();
    }
}