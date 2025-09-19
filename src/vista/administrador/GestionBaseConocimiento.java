package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;
import persistencia.dao.*;

public class GestionBaseConocimiento extends JFrame {
    private MenuAdministrador parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private JList<BaseConocimiento> listaBases;
    private DefaultListModel<BaseConocimiento> modeloLista;
    private JButton btnNueva;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnVolver;

    public GestionBaseConocimiento(MenuAdministrador parent, Usuario usuario) {
        this.parent = parent;
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        initComponents();
        cargarBases();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Bases de Conocimiento");
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel superior con título e información
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("GESTIÓN DE BASES DE CONOCIMIENTO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInstrucciones = new JLabel(
            "<html><center>Seleccione una base de conocimiento para completar con premisas, objetivos y reglas.<br>" +
            "Las bases completas aparecerán disponibles para los clientes.</center></html>");
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInstrucciones.setForeground(Color.BLUE);
        lblInstrucciones.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(lblInstrucciones, BorderLayout.CENTER);

        // Panel central dividido
        JPanel panelCentral = new JPanel(new BorderLayout());

        // Lista de bases con información detallada
        modeloLista = new DefaultListModel<>();
        listaBases = new JList<>(modeloLista);
        listaBases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaBases.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BaseConocimiento) {
                    BaseConocimiento base = (BaseConocimiento) value;

                    // Verificar completitud de la base
                    String estado = evaluarEstadoBase(base);
                    String icono = base.getPremisas().isEmpty() || base.getReglas().isEmpty() || base.getObjetivos().isEmpty()
                                   ? "[!]" : "[OK]";

                    setText(String.format("<html><b>%s %s</b><br/>" +
                                        "<small>%s</small><br/>" +
                                        "<small>Estado: <b>%s</b></small></html>",
                                        icono, base.getNombre(), base.getDescripcion(), estado));
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(listaBases);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bases de Conocimiento Disponibles"));
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Panel de información de la base seleccionada
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de la Base Seleccionada"));
        panelInfo.setPreferredSize(new Dimension(350, 300));

        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtInfo.setText("Seleccione una base de conocimiento\npara ver su información detallada.");
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        panelInfo.add(scrollInfo, BorderLayout.CENTER);

        // Actualizar información cuando cambie la selección
        listaBases.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                BaseConocimiento base = listaBases.getSelectedValue();
                if (base != null) {
                    txtInfo.setText(generarInformacionBase(base));
                    btnEditar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                } else {
                    txtInfo.setText("Seleccione una base de conocimiento\npara ver su información detallada.");
                    btnEditar.setEnabled(false);
                    btnEliminar.setEnabled(false);
                }
            }
        });

        // Panel central dividido horizontalmente
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, panelInfo);
        splitPane.setDividerLocation(400);
        panelCentral.add(splitPane, BorderLayout.CENTER);

        // Panel de botones mejorado
        JPanel panelBotones = new JPanel(new BorderLayout());

        // Botones principales
        JPanel panelBotonesPrincipales = new JPanel(new FlowLayout());
        btnNueva = new JButton("Nueva Base");
        JButton btnAsistente = new JButton("Asistente Paso a Paso");
        btnEditar = new JButton("Completar/Editar Base");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar Lista");
        btnVolver = new JButton("Volver");

        // Inicialmente deshabilitar botones que requieren selección
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        // Mejorar apariencia de botones
        btnNueva.setBackground(new Color(76, 175, 80));
        btnNueva.setForeground(Color.WHITE);
        btnAsistente.setBackground(new Color(255, 193, 7));
        btnAsistente.setForeground(Color.BLACK);
        btnAsistente.setFont(btnAsistente.getFont().deriveFont(Font.BOLD));
        btnEditar.setBackground(new Color(33, 150, 243));
        btnEditar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);

        btnNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditorBaseConocimiento(GestionBaseConocimiento.this, usuario, null).setVisible(true);
            }
        });

        btnAsistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAsistentePasoAPaso();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BaseConocimiento seleccionada = listaBases.getSelectedValue();
                if (seleccionada != null) {
                    // Abrir el nuevo GestorCompleto en lugar del EditorBaseConocimiento
                    new GestorCompleto(GestionBaseConocimiento.this, usuario, seleccionada).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(GestionBaseConocimiento.this,
                        "Debe seleccionar una base de conocimiento", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarBase();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarLista();
                JOptionPane.showMessageDialog(GestionBaseConocimiento.this,
                    "Lista actualizada correctamente", "Actualización", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotonesPrincipales.add(btnNueva);
        panelBotonesPrincipales.add(btnAsistente);
        panelBotonesPrincipales.add(btnEditar);
        panelBotonesPrincipales.add(btnEliminar);
        panelBotonesPrincipales.add(btnActualizar);
        panelBotonesPrincipales.add(btnVolver);

        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout());
        JLabel lblEstadisticas = new JLabel();
        lblEstadisticas.setFont(new Font("Arial", Font.ITALIC, 11));
        lblEstadisticas.setForeground(Color.GRAY);
        panelEstadisticas.add(lblEstadisticas);

        panelBotones.add(panelBotonesPrincipales, BorderLayout.CENTER);
        panelBotones.add(panelEstadisticas, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarBases() {
        BaseConocimiento seleccionada = listaBases.getSelectedValue();
        int seleccionPrevia = -1;

        if (seleccionada != null) {
            seleccionPrevia = seleccionada.getId();
        }

        modeloLista.clear();
        int nuevaSeleccion = -1;

        for (BaseConocimiento base : baseDAO.listarTodas()) {
            modeloLista.addElement(base);
            if (seleccionPrevia != -1 && base.getId() == seleccionPrevia) {
                nuevaSeleccion = modeloLista.getSize() - 1;
            }
        }

        // Restaurar selección si aún existe
        if (nuevaSeleccion != -1) {
            listaBases.setSelectedIndex(nuevaSeleccion);
        }

        // Actualizar información de estado y estadísticas
        SwingUtilities.invokeLater(() -> {
            actualizarEstadisticas();
        });
    }

    private void eliminarBase() {
        BaseConocimiento seleccionada = listaBases.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de conocimiento",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la base: " + seleccionada.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (baseDAO.eliminar(seleccionada.getId())) {
                cargarBases();
                JOptionPane.showMessageDialog(this, "Base eliminada exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la base",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualizarLista() {
        cargarBases();
    }

    public void actualizarYSeleccionar(BaseConocimiento baseASeleccionar) {
        cargarBases();
        if (baseASeleccionar != null) {
            for (int i = 0; i < modeloLista.size(); i++) {
                BaseConocimiento base = modeloLista.getElementAt(i);
                if (base.getId() == baseASeleccionar.getId()) {
                    listaBases.setSelectedIndex(i);
                    listaBases.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    public void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Evalúa el estado de completitud de una base de conocimiento
     */
    private String evaluarEstadoBase(BaseConocimiento base) {
        int premisas = base.getPremisas().size();
        int objetivos = base.getObjetivos().size();
        int reglas = base.getReglas().size();

        if (premisas == 0 && objetivos == 0 && reglas == 0) {
            return "VACÍA";
        } else if (premisas > 0 && objetivos > 0 && reglas > 0) {
            return "COMPLETA";
        } else {
            return "INCOMPLETA";
        }
    }

    /**
     * Genera información detallada de una base de conocimiento
     */
    private String generarInformacionBase(BaseConocimiento base) {
        StringBuilder info = new StringBuilder();

        info.append("═══ INFORMACIÓN DE LA BASE ═══\n\n");
        info.append("Nombre: ").append(base.getNombre()).append("\n");
        info.append("Descripción: ").append(base.getDescripcion()).append("\n");
        info.append("Creada por: ").append(base.getUsuarioCreador()).append("\n");
        info.append("Fecha: ").append(base.getFechaCreacion().toString()).append("\n\n");

        info.append("═══ ESTADO DE COMPLETITUD ═══\n\n");

        // Premisas
        info.append("PREMISAS: ").append(base.getPremisas().size()).append("\n");
        if (base.getPremisas().isEmpty()) {
            info.append("   [PENDIENTE] No tiene premisas definidas\n");
        } else {
            for (Premisa p : base.getPremisas()) {
                info.append("   • ").append(p.getNombre())
                    .append(" (").append(p.getTipo()).append(")\n");
            }
        }
        info.append("\n");

        // Objetivos
        info.append("OBJETIVOS: ").append(base.getObjetivos().size()).append("\n");
        if (base.getObjetivos().isEmpty()) {
            info.append("   [PENDIENTE] No tiene objetivos definidos\n");
        } else {
            for (Objetivo o : base.getObjetivos()) {
                info.append("   • ").append(o.getNombre()).append("\n");
            }
        }
        info.append("\n");

        // Reglas
        info.append("REGLAS: ").append(base.getReglas().size()).append("\n");
        if (base.getReglas().isEmpty()) {
            info.append("   [PENDIENTE] No tiene reglas definidas\n");
        } else {
            for (Regla r : base.getReglas()) {
                info.append("   • ").append(r.getNombre())
                    .append(" (Prioridad: ").append(r.getPrioridad())
                    .append(", Certeza: ").append(r.getFactorCerteza()).append(")\n");
            }
        }
        info.append("\n");

        // Estado general
        String estado = evaluarEstadoBase(base);
        info.append("═══ ESTADO GENERAL ═══\n\n");
        if (estado.equals("COMPLETA")) {
            info.append("ESTADO: COMPLETA\n");
            info.append("Esta base está lista para ser utilizada\npor los clientes.\n");
        } else if (estado.equals("INCOMPLETA")) {
            info.append("ESTADO: INCOMPLETA\n");
            info.append("Necesita completar:\n");
            if (base.getPremisas().isEmpty()) info.append("   - Agregar premisas\n");
            if (base.getObjetivos().isEmpty()) info.append("   - Agregar objetivos\n");
            if (base.getReglas().isEmpty()) info.append("   - Agregar reglas\n");
        } else {
            info.append("ESTADO: VACÍA\n");
            info.append("Debe agregar premisas, objetivos y reglas\npara que funcione correctamente.\n");
        }

        return info.toString();
    }

    /**
     * Actualiza la información de estadísticas en la parte inferior
     */
    private void actualizarEstadisticas() {
        int total = modeloLista.getSize();
        int completas = 0;
        int incompletas = 0;
        int vacias = 0;

        for (int i = 0; i < total; i++) {
            BaseConocimiento base = modeloLista.getElementAt(i);
            String estado = evaluarEstadoBase(base);
            switch (estado) {
                case "COMPLETA": completas++; break;
                case "INCOMPLETA": incompletas++; break;
                case "VACÍA": vacias++; break;
            }
        }

        // Actualizar label de estadísticas
        Component[] components = getContentPane().getComponents();
        if (components.length > 0) {
            updateEstadisticasLabel(total, completas, incompletas, vacias);
        }
    }

    private void updateEstadisticasLabel(int total, int completas, int incompletas, int vacias) {
        // Buscar el label de estadísticas y actualizarlo
        SwingUtilities.invokeLater(() -> {
            setTitle(String.format("Gestión de Bases de Conocimiento (%d total: %d completas, %d incompletas, %d vacías)",
                    total, completas, incompletas, vacias));
        });
    }

    private void mostrarAsistentePasoAPaso() {
        String[] opciones = {"Continuar", "Cancelar"};

        String mensaje = "ASISTENTE PARA CREAR SISTEMA EXPERTO\n\n" +
                        "Este asistente le guiará paso a paso para crear un sistema experto completo.\n\n" +
                        "PASOS QUE SEGUIREMOS:\n" +
                        "1. Crear información básica del sistema\n" +
                        "2. Definir las preguntas (premisas) que hará a los usuarios\n" +
                        "3. Definir las conclusiones (objetivos) que puede alcanzar\n" +
                        "4. Crear las reglas que conectan preguntas con conclusiones\n" +
                        "5. Probar y validar el sistema\n\n" +
                        "EJEMPLOS DE SISTEMAS QUE PUEDE CREAR:\n" +
                        "• Diagnóstico médico básico\n" +
                        "• Recomendador de productos\n" +
                        "• Evaluador de riesgos\n" +
                        "• Selector de opciones\n" +
                        "• Diagnóstico de problemas técnicos\n\n" +
                        "¿Desea continuar con el asistente?";

        int respuesta = JOptionPane.showOptionDialog(this, mensaje,
            "Asistente para Crear Sistema Experto",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (respuesta == 0) {
            iniciarAsistente();
        }
    }

    private void iniciarAsistente() {
        String nombre = JOptionPane.showInputDialog(this,
            "PASO 1 DE 5: INFORMACIÓN BÁSICA\n\n" +
            "Ingrese el nombre de su sistema experto:\n" +
            "(Ejemplos: 'Diagnóstico de Gripe', 'Recomendador de Ejercicios')",
            "Paso 1: Nombre del Sistema",
            JOptionPane.QUESTION_MESSAGE);

        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Asistente cancelado", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String descripcion = JOptionPane.showInputDialog(this,
            "PASO 1 DE 5: INFORMACIÓN BÁSICA\n\n" +
            "Ingrese una descripción del propósito de su sistema:\n" +
            "(Explique qué problemas resolverá o qué recomendaciones dará)",
            "Paso 1: Descripción del Sistema",
            JOptionPane.QUESTION_MESSAGE);

        if (descripcion == null || descripcion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Asistente cancelado", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            "PASOS 2-5: COMPLETAR EN EL EDITOR\n\n" +
            "A continuación se abrirá el editor donde debe:\n\n" +
            "PASO 2 - CREAR PREGUNTAS (Pestaña Premisas):\n" +
            "• Cree las preguntas que hará a los usuarios\n" +
            "• Use descripciones claras: '¿Tiene fiebre?', 'Edad en años'\n\n" +
            "PASO 3 - CREAR CONCLUSIONES (Pestaña Objetivos):\n" +
            "• Defina qué conclusiones puede alcanzar el sistema\n" +
            "• Ejemplos: 'Tiene gripe', 'Necesita ejercicio'\n\n" +
            "PASO 4 - CREAR REGLAS (Pestaña Reglas):\n" +
            "• Conecte las preguntas con las conclusiones\n" +
            "• Ejemplo: SI 'fiebre = true' ENTONCES 'gripe = true'\n\n" +
            "PASO 5 - VALIDAR (Pestaña Validación):\n" +
            "• Use 'Validar Completitud' y 'Probar Gestor'",
            "Instrucciones Completas",
            JOptionPane.INFORMATION_MESSAGE);

        BaseConocimiento nuevaBase = new BaseConocimiento(nombre.trim(), descripcion.trim(), usuario.getId());
        new GestorCompleto(this, usuario, nuevaBase).setVisible(true);
    }
}